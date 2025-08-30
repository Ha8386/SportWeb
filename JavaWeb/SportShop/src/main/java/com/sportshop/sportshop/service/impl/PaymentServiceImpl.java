package com.sportshop.sportshop.service.impl;

import com.sportshop.sportshop.config.VNPayConfig;
import com.sportshop.sportshop.service.PaymentService;
import com.sportshop.sportshop.service.OrderService;
import com.sportshop.sportshop.enums.StatusOrderEnum;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    private OrderService orderService;

    @Override
    public String createPaymentUrl(Long orderId, Long amount, String orderInfo, HttpServletRequest req) throws UnsupportedEncodingException {
        String vnp_Version = "2.1.0";
        String vnp_Command = "pay";
        String vnp_TxnRef = orderId.toString();
        String vnp_IpAddr = getIpAddress(req);
        String vnp_TmnCode = VNPayConfig.vnp_TmnCode;

        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", vnp_Version);
        vnp_Params.put("vnp_Command", vnp_Command);
        vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
        vnp_Params.put("vnp_Amount", String.valueOf(amount * 100));
        vnp_Params.put("vnp_CurrCode", "VND");
        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
        vnp_Params.put("vnp_OrderInfo", orderInfo);
        vnp_Params.put("vnp_OrderType", "other");

        String locate = req.getParameter("language");
        vnp_Params.put("vnp_Locale", (locate != null && !locate.isEmpty()) ? locate : "vn");
        vnp_Params.put("vnp_ReturnUrl", VNPayConfig.vnp_ReturnUrl);
        vnp_Params.put("vnp_IpAddr", vnp_IpAddr);

        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

        cld.add(Calendar.MINUTE, 15);
        String vnp_ExpireDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

        List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        Iterator<String> itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = itr.next();
            String fieldValue = vnp_Params.get(fieldName);
            if (fieldValue != null && !fieldValue.isEmpty()) {
                hashData.append(fieldName).append('=').append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));
                query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII)).append('=').append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));
                if (itr.hasNext()) {
                    hashData.append('&');
                    query.append('&');
                }
            }
        }
        String queryUrl = query.toString();
        String vnp_SecureHash = VNPayConfig.hmacSHA512(VNPayConfig.vnp_HashSecret, hashData.toString());
        queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
        return VNPayConfig.vnp_PayUrl + "?" + queryUrl;
    }

    @Override
    public boolean validatePaymentResponse(HttpServletRequest request) {
        try {
            String vnp_SecureHash = request.getParameter("vnp_SecureHash");
            String vnp_ResponseCode = request.getParameter("vnp_ResponseCode");
            String vnp_TransactionStatus = request.getParameter("vnp_TransactionStatus");

            System.out.println("Validating VNPay response:");
            System.out.println("SecureHash: " + vnp_SecureHash);
            System.out.println("ResponseCode: " + vnp_ResponseCode);
            System.out.println("TransactionStatus: " + vnp_TransactionStatus);

            if (vnp_SecureHash == null) {
                System.out.println("Validation failed: SecureHash is null");
                return false;
            }

            Map<String, String> fields = new HashMap<>();
            for (Enumeration<String> paramNames = request.getParameterNames(); paramNames.hasMoreElements();) {
                String fieldName = paramNames.nextElement();
                String fieldValue = request.getParameter(fieldName);
                if (fieldValue != null && !fieldValue.isEmpty()
                        && !fieldName.equals("vnp_SecureHashType")
                        && !fieldName.equals("vnp_SecureHash")) {
                    fields.put(fieldName, fieldValue);
                }
            }

            System.out.println("Fields to validate: " + fields);
            
            String signValue = VNPayConfig.hashAllFields(fields);
            System.out.println("Calculated hash: " + signValue);
            System.out.println("Received hash:   " + vnp_SecureHash);
            
            boolean isValidHash = signValue.equals(vnp_SecureHash);
            System.out.println("Hash validation: " + (isValidHash ? "SUCCESS" : "FAILED"));

            if (!"00".equals(vnp_ResponseCode) || !"00".equals(vnp_TransactionStatus)) {
                System.out.println("Validation failed: Invalid response code or transaction status");
                return false;
            }

            return isValidHash;
        } catch (Exception e) {
            System.err.println("Error validating payment: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void processPaymentCallback(HttpServletRequest request) {
        try {
            String vnp_ResponseCode = request.getParameter("vnp_ResponseCode");
            String vnp_TxnRef = request.getParameter("vnp_TxnRef");
            String vnp_TransactionStatus = request.getParameter("vnp_TransactionStatus");

            if ("00".equals(vnp_ResponseCode) && "00".equals(vnp_TransactionStatus)) {
                Long orderId = Long.parseLong(vnp_TxnRef);
                orderService.updateStatusOrder(orderId, StatusOrderEnum.Dang_Xu_Ly);
            } else {
                throw new RuntimeException("Payment validation failed");
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to process VNPay payment callback", e);
        }
    }

    private String getIpAddress(HttpServletRequest request) {
        String ipAddress;
        try {
            ipAddress = request.getHeader("X-FORWARDED-FOR");
            if (ipAddress == null) {
                ipAddress = request.getRemoteAddr();
            }
        } catch (Exception e) {
            ipAddress = "Invalid IP:" + e.getMessage();
        }
        return ipAddress;
    }
} 