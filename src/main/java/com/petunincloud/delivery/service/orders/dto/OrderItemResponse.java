package com.petunincloud.delivery.service.orders.dto;

import java.math.BigDecimal;

public record OrderItemResponse(
        Long dishId,
        String dishName,
        int quantity,
        BigDecimal price
) {}
