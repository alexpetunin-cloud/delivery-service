package com.petunincloud.delivery.service.orders;

import java.time.LocalDateTime;

public record OrderDto(
        Long id,
        Long orderId,
        Long userId,
        LocalDateTime dateTime,
        // Список заказа
        OrderStatus status
) {
}
