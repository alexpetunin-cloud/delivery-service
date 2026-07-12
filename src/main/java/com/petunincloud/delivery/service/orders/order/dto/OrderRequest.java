package com.petunincloud.delivery.service.orders.order.dto;

import com.petunincloud.delivery.service.orders.order_item.dto.OrderItemRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record OrderRequest(
        @NotNull(message = "userId is required")
        Long userId,

        @NotNull(message = "restaurantId is required")
        Long restaurantId,

        @NotEmpty(message = "Order must have at least one item")
        @Valid List<OrderItemRequest> items
) {}