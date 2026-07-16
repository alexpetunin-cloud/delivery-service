package com.petunincloud.delivery.service.orders.order.dto;

import com.petunincloud.delivery.service.orders.orderItem.dto.OrderItemResponse;
import com.petunincloud.delivery.service.orders.order.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderResponse(
        Long id,
        Long userId,
        Long restaurantId,
        String restaurantName,
        LocalDateTime dateTime,
        OrderStatus status,
        BigDecimal totalPrice,
        List<OrderItemResponse> items
) {}