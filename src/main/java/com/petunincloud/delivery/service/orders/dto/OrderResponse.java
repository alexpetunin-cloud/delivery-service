package com.petunincloud.delivery.service.orders.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.petunincloud.delivery.service.orders.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderResponse(
        Long id,
        Long userId,
        LocalDateTime dateTime,
        OrderStatus status,
        BigDecimal totalPrice,
        List<OrderItemResponse> items
) {}