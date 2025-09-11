package com.sportshop.sportshop.service;

import com.sportshop.sportshop.dto.request.CancelOrderRequest;
import com.sportshop.sportshop.dto.request.OrderHistoryRequest;
import com.sportshop.sportshop.dto.response.OrderItemRowResponse;
import com.sportshop.sportshop.enums.StatusOrderEnum;

import java.util.List;

public interface OrderHistoryService {
    List<OrderItemRowResponse> historyByStatus(Long userId, OrderHistoryRequest request);

    /** User yêu cầu hủy  */
    void requestCancel(Long userId, CancelOrderRequest request);

}
