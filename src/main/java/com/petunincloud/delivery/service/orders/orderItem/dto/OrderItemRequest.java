package com.petunincloud.delivery.service.orders.orderItem.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record OrderItemRequest(
        @NotNull(message = "dishId is required")
        Long dishId,

        @Min(value = 1, message = "Quantity must be at least 1")
        int quantity
) {}
