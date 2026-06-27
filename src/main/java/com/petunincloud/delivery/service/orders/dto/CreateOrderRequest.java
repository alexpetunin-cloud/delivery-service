package com.petunincloud.delivery.service.orders.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record CreateOrderRequest(
        @NotNull(message = "userId is required")
        Long userId,

        @NotEmpty(message = "Order must have at least one item")
        @Valid List<OrderItemRequest> items
) {}