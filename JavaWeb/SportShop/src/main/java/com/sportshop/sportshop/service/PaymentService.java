package com.sportshop.sportshop.service;

import jakarta.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;

public interface PaymentService {
    String createPaymentUrl(Long orderId, Long amount, String orderInfo, HttpServletRequest req) throws UnsupportedEncodingException;
    
    boolean validatePaymentResponse(HttpServletRequest request);
    
    void processPaymentCallback(HttpServletRequest request);
} 