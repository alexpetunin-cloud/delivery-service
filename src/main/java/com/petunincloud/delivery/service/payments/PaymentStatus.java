package com.petunincloud.delivery.service.payments;

public enum PaymentStatus {
    PENDING,     // Ожидает оплаты
    SUCCESS,     // Успешно оплачен
    FAILED,      // Отказ платежа (недостаточно средств, ошибка карты)
    CANCELED     // Отменен пользователем
}
