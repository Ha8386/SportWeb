package com.sportshop.sportshop.controller.user;

import com.sportshop.sportshop.service.QRCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.ui.Model;
import org.springframework.web.servlet.ModelAndView;

import com.sportshop.sportshop.dto.request.AddressRequest;
import com.sportshop.sportshop.dto.request.CreateUserRequest;
import com.sportshop.sportshop.dto.request.UpdateUserRequest;
import com.sportshop.sportshop.entity.UserEntity;
import com.sportshop.sportshop.enums.GenderEnum;
import com.sportshop.sportshop.service.CartService;
import com.sportshop.sportshop.service.OrderService;
import com.sportshop.sportshop.service.UserService;
import com.sportshop.sportshop.utils.GetUserAuthentication;

@Controller
@RequestMapping("/user")
public class AccountController {   
    @Autowired
    private GetUserAuthentication getUserAuthentication;

    @Autowired
    private UserService userService;

    @Autowired
    private CartService cartService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private QRCodeService qrCodeService;
    @GetMapping("/info")
    public ModelAndView viewUser(@RequestParam(required = false) Long id) {
//        UserEntity user = getUserAuthentication.getUser();
//
//        String userData = "Họ tên: " + user.getFullName() + "\n" +
//                "Email: " + user.getEmail() + "\n" +
//                "SĐT: " + user.getPhone() + "\n" +
//                "Giới tính: " + user.getGender();
//
//        String qrCode = qrCodeService.generateQRCodeBase64(userData, 200, 200);


        UserEntity user = getUserAuthentication.getUser();
        String qrLink = "http://localhost:8080/user/qr-info/" + user.getUsername();
        String qrCode = qrCodeService.generateQRCodeBase64(qrLink, 200, 200);


        ModelAndView mav = new ModelAndView("/user/information")
                .addObject("genders", GenderEnum.values())
                .addObject("updateUser", new CreateUserRequest())
                .addObject("newAddress", new AddressRequest())
                .addObject("qrCode", qrCode);

        if (user != null) {
            mav.addObject("user", user);
        }

        return mav;
    }
    @GetMapping("/qr-info/{username}")
    public ModelAndView viewUserInfoFromQR(@PathVariable String username) {
        UserEntity user = userService.findByUserName(username);
        if (user == null) {
            return new ModelAndView("error").addObject("message", "Người dùng không tồn tại.");
        }

        return new ModelAndView("/user/qr-info").addObject("user", user);
    }

    @PostMapping("/update")
    public String updateUser(@ModelAttribute("updateUser") UpdateUserRequest request,
                             @RequestParam("file") MultipartFile file){
        
        UserEntity user = getUserAuthentication.getUser();

        userService.updateUser(request, user.getId(), file);

        return "redirect:/user/info";
    }

    @PostMapping("/add-address")
    public String updateUser(@ModelAttribute("newAddress") AddressRequest request){
        
        UserEntity user = getUserAuthentication.getUser();

        userService.addAddress(user.getId(), request);

        return "redirect:/user/info";
    }

    @GetMapping("/delete-address/{addressId}")
    public String deleteAddress(@PathVariable Long addressId){
        userService.deleteAddress(addressId);

        return "redirect:/user/info";
    }

    @GetMapping("/checkout/{addressId}")
    public String checkout(@PathVariable Long addressId){
        UserEntity user = getUserAuthentication.getUser();
        
        // Tạo đơn hàng mới
        Long orderId = orderService.AddOrder(user);
        
        // Chuyển đến trang thanh toán
        return "redirect:/payment?orderId=" + orderId + "&addressId=" + addressId;
    }

    @GetMapping("/history")
    public ModelAndView historyBuyProduct(){
        UserEntity user = getUserAuthentication.getUser();
        
        return new ModelAndView("/user/history")
                .addObject("orders", orderService.historyBuy(user.getId()));
    }
}
