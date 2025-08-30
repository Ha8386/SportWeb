package com.sportshop.sportshop.controller.user;

import com.sportshop.sportshop.dto.response.CartItemResponse;
import com.sportshop.sportshop.dto.response.CartItemResponsePrint;
import com.sportshop.sportshop.entity.CartEntity;
import com.sportshop.sportshop.entity.UserEntity;
import com.sportshop.sportshop.service.CartService;
import com.sportshop.sportshop.service.OrderService;
import com.sportshop.sportshop.service.QRCodeService;
import com.sportshop.sportshop.utils.GetUserAuthentication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("user/cart")
public class CartController {
    @Autowired
    private CartService cartService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private GetUserAuthentication getUserAuthentication;
    @Autowired
    private QRCodeService qrCodeService;

    @GetMapping("/{userId}")
    public ModelAndView getCart(@PathVariable Long userId){
        UserEntity user = getUserAuthentication.getUser();

        ModelAndView mav = new ModelAndView("/user/cart")
                .addObject("carts", cartService.getCart(userId))
                .addObject("user", user);

        return mav;
    }



    @PostMapping("/{productId}")
    public ModelAndView addCart(@PathVariable Long productId, Model model){
        UserEntity user = getUserAuthentication.getUser();
        
        ModelAndView mav = new ModelAndView("/user/cart")
                        .addObject("user", user);
        if(cartService.addCart(user.getId(), productId)){
            model.addAttribute("notification", "Success");
            model.addAttribute("message", "Thêm Sản Phẩm Thành Công ");
        } else {
            model.addAttribute("notification", "Fail");
            model.addAttribute("message", "Sản Phẩm Này Đã Có Trong Giỏ Hàng Rồi");
        }    
        mav.addObject("carts", cartService.getCart(user.getId()));
        return mav;
    }

    @GetMapping("/{productId}/{quantity}")
    public ModelAndView replaceQuantityProduct(Model model,@PathVariable("productId") Long productId,
                                        @PathVariable("quantity") Long quantity){
                                            
        UserEntity user = getUserAuthentication.getUser();

        ModelAndView mav = new ModelAndView("/user/cart")
                            .addObject("carts", cartService.getCart(user.getId()))
                            .addObject("user", user);
            
        if(cartService.replaceQuantityProduct(user.getId(), productId, quantity)){
            model.addAttribute("notification", "Success");
            model.addAttribute("message", "Thêm thành công");
        } else {
            model.addAttribute("notification", "Fail");
            model.addAttribute("message", "Không thể mua thêm vì không đủ hàng");
        }
        return mav;
    }

    @GetMapping("/delete/{productId}")
    public String deleteCart(@PathVariable Long productId){
        UserEntity user = getUserAuthentication.getUser();
        cartService.removeProductToCart(user.getId(), productId);
        return "redirect:/user/cart/" + user.getId();
    }
    @GetMapping("/print")
    @ResponseBody
    public List<CartItemResponsePrint> printCart() {
        UserEntity user = getUserAuthentication.getUser();
        List<CartEntity> carts = cartService.getCart(user.getId());

        return carts.stream()
                .map(cart -> new CartItemResponsePrint(
                        cart.getProduct().getName(),
                        cart.getQuantity().intValue(),
                        cart.getProduct().getPrice().doubleValue(),
                        (cart.getProduct().getImages() != null && !cart.getProduct().getImages().isEmpty())
                                ? cart.getProduct().getImages().get(0).getImageLink()
                                : "/images/default.jpg",
                        cart.getProduct().getId()
                ))
                .collect(Collectors.toList());
    }



    @GetMapping("/print-view")
    public ModelAndView printCartView() {
        UserEntity user = getUserAuthentication.getUser();
        List<CartEntity> carts = cartService.getCart(user.getId());

        // Tạo nội dung cho QR từ cart
        StringBuilder qrContent = new StringBuilder("Giỏ hàng:\n");
        for (CartEntity cart : carts) {
            qrContent.append("- ").append(cart.getProduct().getName())
                    .append(" x").append(cart.getQuantity())
                    .append(" - ").append(cart.getProduct().getPrice())
                    .append("đ\n");
        }

        // Sinh mã QR từ backend (Base64)
        String qrCode = qrCodeService.generateQRCodeBase64(qrContent.toString(), 200, 200);

        // Trả về view modal với danh sách giỏ và QR
        ModelAndView mav = new ModelAndView("/user/cart_print_modal");
        mav.addObject("cartItems", carts);
        mav.addObject("qrCode", qrCode);
        return mav;
    }

    @GetMapping("/qr")
    @ResponseBody
    public Map<String, String> getCartQRCode() {
        UserEntity user = getUserAuthentication.getUser();

        // Tạo URL dẫn đến view hiển thị giỏ hàng qua QR
        String qrLink = "http://localhost:8080/user/cart/qr-view?uid=" + user.getId();

        // Sinh mã QR từ link
        String qrCodeBase64 = qrCodeService.generateQRCodeBase64(qrLink, 150, 150);

        Map<String, String> result = new HashMap<>();
        result.put("qrCode", qrCodeBase64);
        return result;
    }


    @GetMapping("/qr-view")
    public ModelAndView viewCartFromQR(@RequestParam("uid") Long userId) {
        List<CartEntity> carts = cartService.getCart(userId);

        List<CartItemResponse> cartItems = carts.stream().map(cart -> new CartItemResponse(
                cart.getProduct().getName(),
                cart.getQuantity().intValue(),
                cart.getProduct().getPrice(),
                (cart.getProduct().getImages() != null && !cart.getProduct().getImages().isEmpty())
                        ? cart.getProduct().getImages().get(0).getImageLink()
                        : "/images/default.jpg"
        )).collect(Collectors.toList());

        String qrLink = "http://localhost:8080/user/cart/qr-view?uid=" + userId;
        String qrCode = qrCodeService.generateQRCodeBase64(qrLink, 150, 150);

        ModelAndView mav = new ModelAndView("user/cart_qr_view");
        double totalAmount = cartItems.stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();

        mav.addObject("totalAmount", totalAmount);  // thêm dòng này

        mav.addObject("cartItems", cartItems);
        mav.addObject("qrCode", qrCode);
        return mav;
    }

}
