package com.petunincloud.delivery.service.orders;

import com.petunincloud.delivery.service.common.BaseMapper;
import com.petunincloud.delivery.service.orders.dto.*;
import com.petunincloud.delivery.service.orders.entity.OrderEntity;
import com.petunincloud.delivery.service.orders.entity.OrderItemEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class OrderMapper implements BaseMapper<OrderEntity, OrderResponse> {

    @Override
    public OrderResponse toResponse(OrderEntity entity) {
        if (entity == null) return null;

        List<OrderItemResponse> itemResponses = entity.getItems().stream()
                .map(this::toItemResponse)
                .collect(Collectors.toList());

        return new OrderResponse(
                entity.getId(),
                entity.getUserId(),
                entity.getDateTime(),
                entity.getStatus(),
                entity.getTotalPrice(),
                itemResponses
        );
    }

    private OrderItemResponse toItemResponse(OrderItemEntity item) {
        return new OrderItemResponse(
                item.getDishId(),
                item.getDishName(),
                item.getQuantity(),
                item.getPrice()
        );
    }

    @Override
    public OrderEntity toEntity(OrderResponse dto) {
        throw new UnsupportedOperationException("Not implemented");
    }

    public OrderEntity toEntity(OrderRequest request) {
        OrderEntity order = new OrderEntity();
        order.setUserId(request.userId());
        return order;
    }
}