package com.sportshop.sportshop.mapper;

import com.sportshop.sportshop.dto.response.OrderDetailResponse;
import com.sportshop.sportshop.entity.OrderDetailEntity;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-09-11T09:45:01+0700",
    comments = "version: 1.6.0, compiler: javac, environment: Java 23.0.1 (Oracle Corporation)"
)
@Component
public class OrderDetailMapperImpl implements OrderDetailMapper {

    @Override
    public OrderDetailResponse toOrderDetailResponse(OrderDetailEntity order) {
        if ( order == null ) {
            return null;
        }

        OrderDetailResponse.OrderDetailResponseBuilder orderDetailResponse = OrderDetailResponse.builder();

        orderDetailResponse.id( order.getId() );
        orderDetailResponse.quantity( order.getQuantity() );
        orderDetailResponse.price( order.getPrice() );
        orderDetailResponse.discount( order.getDiscount() );
        orderDetailResponse.total( order.getTotal() );
        orderDetailResponse.order( order.getOrder() );
        orderDetailResponse.product( order.getProduct() );

        return orderDetailResponse.build();
    }
}
