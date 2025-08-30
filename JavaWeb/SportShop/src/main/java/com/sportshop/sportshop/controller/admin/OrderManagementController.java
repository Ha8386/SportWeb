package com.sportshop.sportshop.controller.admin;

import com.sportshop.sportshop.dto.response.OrderDetailResponse;
import com.sportshop.sportshop.dto.response.OrderResponse;
import com.sportshop.sportshop.entity.OrderDetailEntity;
import com.sportshop.sportshop.entity.OrderEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.sportshop.sportshop.enums.StatusOrderEnum;
import com.sportshop.sportshop.service.OrderDetailService;
import com.sportshop.sportshop.service.OrderService;
import com.sportshop.sportshop.service.QRCodeService;

import java.util.List;

@Controller
@RequestMapping("/admin/order")
public class OrderManagementController {
    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderDetailService orderDetailService;

    @Autowired
    private QRCodeService qrCodeService;

    @GetMapping()
    public ModelAndView orderManagement(){
        return new ModelAndView("/admin/order/management")
                .addObject("orders", orderService.getAllOrder())
                .addObject("status_orders", StatusOrderEnum.values());
    }

    @GetMapping("/{orderId}")
    public ModelAndView getOrderById(@PathVariable Long orderId) {
        // Lấy thông tin đơn hàng và các item
        OrderResponse order = orderService.getOrderById(orderId);
        List<OrderDetailResponse> items = orderDetailService.getItemByOrderId(orderId);

        // Tạo nội dung cho QR từ đơn hàng
        StringBuilder qrContent = new StringBuilder("Đơn hàng #" + order.getId() + ":\n");
        for (OrderDetailResponse item : items) {
            qrContent.append("- ")
                    .append(item.getProduct().getName())
                    .append(" x").append(item.getQuantity())
                    .append(" - ").append(item.getProduct().getPrice())
                    .append("đ\n");
        }

        // Sinh mã QR (Base64)
        String qrCode = qrCodeService.generateQRCodeBase64(qrContent.toString(), 200, 200);

        // Trả về view
        return new ModelAndView("/admin/order/view")
                .addObject("order", order)
                .addObject("items", items)
                .addObject("qrCode", qrCode);
    }

    @GetMapping("/{orderId}/{status}")
    public String updateStatusOrder(@PathVariable("orderId") Long orderId,
                                    @PathVariable("status") StatusOrderEnum status) {
        orderService.updateStatusOrder(orderId, status);
        return "redirect:/admin/order";
    }
}
