package com.sportshop.sportshop.controller.user;

import com.sportshop.sportshop.dto.request.CancelOrderRequest;
import com.sportshop.sportshop.dto.request.OrderHistoryRequest;
import com.sportshop.sportshop.dto.response.OrderItemRowResponse;
import com.sportshop.sportshop.enums.StatusOrderEnum;
import com.sportshop.sportshop.service.OrderHistoryService;
import com.sportshop.sportshop.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
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

    @GetMapping
    public String history(@RequestParam(name = "status", required = false) StatusOrderEnum status,
                          @AuthenticationPrincipal User principal,
                          Model model) {
        if (status == null) {
            return "redirect:/user/history?status=Dang_Xu_Ly";
        }

        Long userId = userService.findIdByUsername(principal.getUsername());

        OrderHistoryRequest req = new OrderHistoryRequest(status);
        List<OrderItemRowResponse> rows = historyService.historyByStatus(userId, req);

        model.addAttribute("rows", rows);
        model.addAttribute("status", status); // giữ để bật active cho nav-pills
        return "user/history"; // đảm bảo template là user/history.html
    }

    @PostMapping("/cancel")
    public String cancel(@RequestParam Long orderId,
                         @AuthenticationPrincipal User principal,
                         RedirectAttributes ra) {
        Long userId = userService.findIdByUsername(principal.getUsername());
        historyService.cancelOrder(userId, new CancelOrderRequest(orderId));
        ra.addFlashAttribute("message", "Đã hủy đơn #" + orderId);
        return "redirect:/user/history?status=Dang_Xu_Ly";
    }
}
