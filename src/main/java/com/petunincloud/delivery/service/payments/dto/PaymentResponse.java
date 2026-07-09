package com.petunincloud.delivery.service.payments.dto;

import com.petunincloud.delivery.service.payments.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PaymentResponse(
        Long id,
        Long orderId,
        Long userId,
        BigDecimal amount,
        String paymentMethod,
        PaymentStatus status,
        String transactionId,
        LocalDateTime completedAt,
        LocalDateTime createdAt
) {}
