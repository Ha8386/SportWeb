package com.sportshop.sportshop.service.impl;

import com.sportshop.sportshop.dto.request.CancelOrderRequest;
import com.sportshop.sportshop.dto.request.OrderHistoryRequest;
import com.sportshop.sportshop.dto.response.OrderItemRowResponse;
import com.sportshop.sportshop.entity.OrderEntity;
import com.sportshop.sportshop.enums.StatusOrderEnum;
import com.sportshop.sportshop.repository.OrderDetailRepository;
import com.sportshop.sportshop.repository.OrderRepository;
import com.sportshop.sportshop.service.OrderHistoryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OrderHistoryServiceImpl implements OrderHistoryService {

    private final OrderDetailRepository detailRepo;
    private final OrderRepository orderRepo;

    public OrderHistoryServiceImpl(OrderDetailRepository detailRepo, OrderRepository orderRepo) {
        this.detailRepo = detailRepo;
        this.orderRepo = orderRepo;
    }

    @Override
    public List<OrderItemRowResponse> historyByStatus(Long userId, OrderHistoryRequest request) {
        StatusOrderEnum status = request.getStatus() == null
                ? StatusOrderEnum.Dang_Xu_Ly
                : request.getStatus();
        return detailRepo.findUserOrderItemsByStatusDto(userId, status);
    }

    @Transactional
    @Override
    public void cancelOrder(Long userId, CancelOrderRequest request) {
        OrderEntity order = orderRepo.findByIdAndUserId(request.getOrderId(), userId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy đơn hàng của bạn"));
        if (order.getStatus() != StatusOrderEnum.Dang_Xu_Ly) {
            throw new IllegalStateException("Chỉ hủy được đơn đang xử lý");
        }
        order.setStatus(StatusOrderEnum.Da_Huy);
        orderRepo.save(order);
        // TODO: nếu cần hoàn kho/hoàn mã giảm/ghi log... xử lý thêm ở đây
    }
}
