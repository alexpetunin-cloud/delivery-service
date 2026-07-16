package com.petunincloud.delivery.service.orders.orderItem;

import com.petunincloud.delivery.service.orders.order.OrderEntity;
import com.petunincloud.delivery.service.orders.orderItem.dto.OrderItemRequest;
import com.petunincloud.delivery.service.orders.orderItem.dto.OrderItemResponse;
import org.springframework.stereotype.Component;

@Component
public class OrderItemMapper {
    public OrderItemEntity toEntity(OrderItemRequest request, OrderEntity order) {
        OrderItemEntity entity = new OrderItemEntity();
        entity.setOrder(order);
        entity.setDishId(request.dishId());
        entity.setQuantity(request.quantity());

        return entity;
    }

    public OrderItemResponse toResponse(OrderItemEntity entity) {
        return new OrderItemResponse(
                entity.getDishId(),
                entity.getDishName(),
                entity.getQuantity(),
                entity.getPrice()
        );
    }
}
