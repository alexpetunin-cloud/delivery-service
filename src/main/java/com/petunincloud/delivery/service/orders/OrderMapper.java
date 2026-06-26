package com.petunincloud.delivery.service.orders;

import com.petunincloud.delivery.service.common.BaseMapper;
import org.springframework.stereotype.Component;

@Component
public class OrderMapper implements BaseMapper<OrderEntity, OrderDto> {

    @Override
    public OrderDto toDto(OrderEntity entity) {
        return new OrderDto(
                entity.getId(),
                entity.getOrderId(),
                entity.getUserId(),
                entity.getDateTime(),
                entity.getStatus()
        );
    }

    @Override
    public OrderEntity toEntity(OrderDto dto) {
        return new OrderEntity(
                dto.orderId(),
                dto.userId(),
                dto.dateTime(),
                dto.status()
        );
    }
}