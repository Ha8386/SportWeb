package com.sportshop.sportshop.controller.user;

import com.sportshop.sportshop.dto.request.CancelOrderRequest;
import com.sportshop.sportshop.dto.request.OrderHistoryRequest;
import com.sportshop.sportshop.dto.response.OrderItemRowResponse;
import com.sportshop.sportshop.dto.response.OrderResponse;
import com.sportshop.sportshop.enums.StatusOrderEnum;
import com.sportshop.sportshop.service.OrderHistoryService;
import com.sportshop.sportshop.service.OrderService;
import com.sportshop.sportshop.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/user/history")
@RequiredArgsConstructor
public class OrderHistoryController {

    private final OrderHistoryService historyService;
    private final UserService userService;
    private final OrderService orderService;

    /** Parse status an toàn, mặc định Dang_Xu_Ly nếu null/sai */
    private StatusOrderEnum parseStatus(String s) {
        if (s == null || s.isBlank()) return StatusOrderEnum.Dang_Xu_Ly;
        try {
            return StatusOrderEnum.valueOf(s);
        } catch (IllegalArgumentException ex) {
            return StatusOrderEnum.Dang_Xu_Ly;
        }
    }

    @GetMapping
    public String history(@RequestParam(name = "status", required = false) String status,
                          @AuthenticationPrincipal UserDetails principal,
                          Model model) {
        if (principal == null) {
            return "redirect:/login";
        }
        StatusOrderEnum st = parseStatus(status);
        Long userId = userService.findIdByUsername(principal.getUsername());

        OrderHistoryRequest req = new OrderHistoryRequest(st);
        List<OrderItemRowResponse> rows = historyService.historyByStatus(userId, req);

        model.addAttribute("rows", rows);
        model.addAttribute("status", st.name()); // dùng name() cho nav-pills
        return "user/history"; // -> templates/user/history.html
    }

    @PostMapping("/cancel")
    public String requestCancel(@RequestParam Long orderId,
                                @AuthenticationPrincipal UserDetails principal,
                                RedirectAttributes ra) {
        if (principal == null) {
            return "redirect:/login";
        }
        Long userId = userService.findIdByUsername(principal.getUsername());
        historyService.requestCancel(userId, new CancelOrderRequest(orderId));
        ra.addFlashAttribute("message", "Đã gửi yêu cầu hủy đơn #" + orderId);
        return "redirect:/user/history?status=Dang_Xu_Ly";
    }

    @GetMapping("/{id}")
    public String viewOrder(@PathVariable("id") Long orderId,
                            @AuthenticationPrincipal UserDetails principal,
                            Model model,
                            RedirectAttributes ra) {
        if (principal == null) {
            return "redirect:/login";
        }
        Long userId = userService.findIdByUsername(principal.getUsername());
        OrderResponse order = orderService.getOrderById(orderId);
        if (order == null || order.getUser() == null || !order.getUser().getId().equals(userId)) {
            ra.addFlashAttribute("message", "Không tìm thấy đơn hàng hoặc bạn không có quyền xem.");
            return "redirect:/user/history?status=Dang_Xu_Ly";
        }

        model.addAttribute("order", order);
        model.addAttribute("items", order.getItems()); // List<OrderDetailEntity> hoặc DTO tương ứng
        return "order/view"; // -> resources/templates/order/view.html
    }
}
