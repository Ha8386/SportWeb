package com.sportshop.sportshop.dto.request;

public class CancelOrderRequest {
    private Long orderId;

    public CancelOrderRequest() {}
    public CancelOrderRequest(Long orderId) { this.orderId = orderId; }

    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }
}
