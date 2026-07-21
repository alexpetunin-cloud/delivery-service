package com.petunincloud.delivery.service.orders.order.dto;

import com.petunincloud.delivery.service.orders.orderItem.dto.OrderItemRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record OrderRequest(
        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        String email,

        @NotNull(message = "restaurantId is required")
        Long restaurantId,

        @NotEmpty(message = "Order must have at least one item")
        @Valid List<OrderItemRequest> items
) {}