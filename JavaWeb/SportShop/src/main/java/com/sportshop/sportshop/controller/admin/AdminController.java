package com.sportshop.sportshop.controller.admin;

import com.sportshop.sportshop.service.OrderService;
import com.sportshop.sportshop.service.ProductService;
import com.sportshop.sportshop.service.UserService;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    private UserService userService;

    @Autowired
    private ProductService productService;

    @Autowired
    private OrderService orderService;

    @GetMapping
    public ModelAndView homeAdmin() {
        NumberFormat numberFormat = NumberFormat.getInstance(Locale.US); // Chọn định dạng cho thị trường Mỹ
        String increase = numberFormat.format(orderService.getIncrease());

        return new ModelAndView("/admin/admin")
                .addObject("userQuantity", userService.countUsers())
                .addObject("productQuantity", productService.countProduct())
                .addObject("orderQuantity", orderService.getCount())
                .addObject("increase", increase)
                .addObject("revenues", orderService.getRevenueByDay(LocalDate.now().minusMonths(1), LocalDate.now()))
                .addObject("productSales", productService.getProductSale());
    }
}
