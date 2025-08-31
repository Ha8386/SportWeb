package com.sportshop.sportshop.service;

import com.sportshop.sportshop.dto.request.CancelOrderRequest;
import com.sportshop.sportshop.dto.request.OrderHistoryRequest;
import com.sportshop.sportshop.dto.response.OrderItemRowResponse;

import java.util.List;

public interface OrderHistoryService {
    List<OrderItemRowResponse> historyByStatus(Long userId, OrderHistoryRequest request);
    void cancelOrder(Long userId, CancelOrderRequest request);
}
