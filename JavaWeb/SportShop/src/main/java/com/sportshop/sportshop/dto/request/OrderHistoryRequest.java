package com.sportshop.sportshop.dto.request;

import com.sportshop.sportshop.enums.StatusOrderEnum;

public class OrderHistoryRequest {
    // Mặc định tab Đang xử lý
    private StatusOrderEnum status = StatusOrderEnum.Dang_Xu_Ly;

    public OrderHistoryRequest() {}
    public OrderHistoryRequest(StatusOrderEnum status) { this.status = status; }

    public StatusOrderEnum getStatus() { return status; }
    public void setStatus(StatusOrderEnum status) { this.status = status; }
}
