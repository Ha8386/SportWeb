package com.sportshop.sportshop.dto.response;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CartItemResponsePrint {
    private String productName;
    private int quantity;
    private double price;
    private String image;
    private Long productId;
}
