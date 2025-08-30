package com.sportshop.sportshop.dto.response;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CartItemResponse {
    String productName;
    int quantity;
    long price;
    String imageLink;


}
