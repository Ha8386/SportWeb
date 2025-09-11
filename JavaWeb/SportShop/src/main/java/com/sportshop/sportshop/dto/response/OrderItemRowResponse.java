package com.sportshop.sportshop.dto.response;

import com.sportshop.sportshop.enums.StatusOrderEnum;

import java.util.Date;

public class OrderItemRowResponse {

    private Long orderId;            // orders.id
    private Date orderDate;          // ngày đặt hàng (OrderEntity.date hoặc createdAt)
    private String productName;      // product.name
    private Long quantity;           // order_detail.quantity
    private Long price;              // order_detail.price
    private Long total;              // order_detail.total (có thể null, sẽ tự tính)
    private StatusOrderEnum status;  // tình trạng đơn

    // tiện cho UI
    private Long productId;          // p.id
    private Long orderItemId;        // od.id
    private Date reviewDate;         // ngày đánh giá (CommentEntity.createDate) – có thể null

    // ===== Constructor 7 tham số (khớp query cơ bản) =====
    public OrderItemRowResponse(Long orderId,
                                Date orderDate,
                                String productName,
                                Long quantity,
                                Long price,
                                Long total,
                                StatusOrderEnum status) {
        this.orderId = orderId;
        this.orderDate = orderDate;
        this.productName = productName;
        this.quantity = quantity;
        this.price = price;
        this.total = (total != null ? total : calcTotal(price, quantity));
        this.status = status;
    }

    // ===== Constructor 9 tham số (thêm productId, orderItemId) =====
    public OrderItemRowResponse(Long orderId,
                                Date orderDate,
                                String productName,
                                Long quantity,
                                Long price,
                                Long total,
                                StatusOrderEnum status,
                                Long productId,
                                Long orderItemId) {
        this(orderId, orderDate, productName, quantity, price, total, status);
        this.productId = productId;
        this.orderItemId = orderItemId;
    }

    // ===== Constructor 10 tham số (thêm reviewDate) =====
    public OrderItemRowResponse(Long orderId,
                                Date orderDate,
                                String productName,
                                Long quantity,
                                Long price,
                                Long total,
                                StatusOrderEnum status,
                                Long productId,
                                Long orderItemId,
                                Date reviewDate) {
        this(orderId, orderDate, productName, quantity, price, total, status, productId, orderItemId);
        this.reviewDate = reviewDate;
    }

    private Long calcTotal(Long price, Long quantity) {
        if (price == null || quantity == null) return null;
        return price * quantity;
    }

    // ===== Getters (Thymeleaf/JPA projection cần) =====
    public Long getOrderId() { return orderId; }
    public Date getOrderDate() { return orderDate; }
    public String getProductName() { return productName; }
    public Long getQuantity() { return quantity; }
    public Long getPrice() { return price; }
    public Long getTotal() { return total; }
    public StatusOrderEnum getStatus() { return status; }
    public Long getProductId() { return productId; }
    public Long getOrderItemId() { return orderItemId; }
    public Date getReviewDate() { return reviewDate; }
}
