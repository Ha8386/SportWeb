package com.sportshop.sportshop.mapper;

import com.sportshop.sportshop.dto.response.OrderResponse;
import com.sportshop.sportshop.entity.OrderDetailEntity;
import com.sportshop.sportshop.entity.OrderEntity;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-08-30T13:38:42+0700",
    comments = "version: 1.6.0, compiler: javac, environment: Java 23.0.1 (Oracle Corporation)"
)
@Component
public class OrderMapperImpl implements OrderMapper {

    @Override
    public OrderResponse toOrderResponse(OrderEntity order) {
        if ( order == null ) {
            return null;
        }

        OrderResponse.OrderResponseBuilder orderResponse = OrderResponse.builder();

        orderResponse.id( order.getId() );
        orderResponse.date( order.getDate() );
        orderResponse.total( order.getTotal() );
        orderResponse.quantity( order.getQuantity() );
        orderResponse.phone( order.getPhone() );
        orderResponse.address( order.getAddress() );
        orderResponse.status( order.getStatus() );
        orderResponse.user( order.getUser() );
        List<OrderDetailEntity> list = order.getItems();
        if ( list != null ) {
            orderResponse.items( new ArrayList<OrderDetailEntity>( list ) );
        }

        return orderResponse.build();
    }
}
