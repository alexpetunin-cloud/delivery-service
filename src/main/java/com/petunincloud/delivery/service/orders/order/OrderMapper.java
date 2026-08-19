package com.petunincloud.delivery.service.orders.order;

import com.petunincloud.delivery.service.common.BaseMapper;
import com.petunincloud.delivery.service.orders.order.dto.OrderResponse;
import com.petunincloud.delivery.service.orders.orderItem.OrderItemEntity;
import com.petunincloud.delivery.service.orders.orderItem.dto.OrderItemResponse;
import com.petunincloud.delivery.service.restaurants.restaurant.RestaurantEntity;
import com.petunincloud.delivery.service.users.UserEntity;
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
                entity.getUser().getId(),
                entity.getRestaurant().getId(),
                entity.getRestaurant().getName(),
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

    public OrderEntity toEntity(
            UserEntity user,
            RestaurantEntity restaurant
    ) {
        OrderEntity order = new OrderEntity();

        order.setUser(user);
        order.setRestaurant(restaurant);
        order.setDateTime(LocalDateTime.now().withNano(0));
        order.setStatus(OrderStatus.PENDING);

        return order;
    }

    @Override
    public OrderEntity toEntity(OrderResponse dto) {
        throw new UnsupportedOperationException("Not implemented");
    }
}