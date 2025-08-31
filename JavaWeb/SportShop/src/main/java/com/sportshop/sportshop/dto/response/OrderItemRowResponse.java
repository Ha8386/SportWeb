package com.sportshop.sportshop.dto.response;

import java.util.Date; // <-- OrderEntity.date là java.util.Date
import com.sportshop.sportshop.enums.StatusOrderEnum;

public class OrderItemRowResponse {
    private Long orderId;
    private Date orderDate;          // Date (không phải LocalDate/LocalDateTime)
    private String productName;
    private Long quantity;           // Long (không phải Integer)
    private Long price;              // Long
    private Long total;              // Long
    private StatusOrderEnum status;  // Enum

    // Constructor PHẢI trùng thứ tự & kiểu với JPQL
    public OrderItemRowResponse(Long orderId, Date orderDate, String productName,
                                Long quantity, Long price, Long total, StatusOrderEnum status) {
        this.orderId = orderId;
        this.orderDate = orderDate;
        this.productName = productName;
        this.quantity = quantity;
        this.price = price;
        this.total = total;
        this.status = status;
    }

    public Long getOrderId() { return orderId; }
    public Date getOrderDate() { return orderDate; }
    public String getProductName() { return productName; }
    public Long getQuantity() { return quantity; }
    public Long getPrice() { return price; }
    public Long getTotal() { return total; }
    public StatusOrderEnum getStatus() { return status; }
}
