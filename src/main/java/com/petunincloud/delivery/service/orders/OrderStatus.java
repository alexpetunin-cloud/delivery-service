package com.petunincloud.delivery.service.orders;

public enum OrderStatus {
    PENDING,      // Создан, ожидает подтверждения
    CONFIRMED,    // Ресторан подтвердил
    COOKING,      // Готовится
    READY,        // Готов к выдаче
    DELIVERING,   // Курьер везет
    DELIVERED,    // Доставлен
    CANCELED      // Отменен
}
