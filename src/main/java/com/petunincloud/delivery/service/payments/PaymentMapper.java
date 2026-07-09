package com.petunincloud.delivery.service.payments;

import com.petunincloud.delivery.service.common.BaseMapper;
import com.petunincloud.delivery.service.orders.entity.OrderEntity;
import com.petunincloud.delivery.service.payments.dto.PaymentResponse;
import org.springframework.stereotype.Component;

@Component
public class PaymentMapper implements BaseMapper<PaymentEntity, PaymentResponse> {

    public PaymentEntity toEntity(Long userId, OrderEntity order) {
        PaymentEntity payment = new PaymentEntity();
        payment.setOrder(order);
        payment.setUserId(userId);
        return payment;
    }

    @Override
    public PaymentResponse toResponse(PaymentEntity entity) {
        return new PaymentResponse(
                entity.getId(),
                entity.getOrder().getId(),
                entity.getUserId(),
                entity.getAmount(),
                entity.getPaymentMethod(),
                entity.getStatus(),
                entity.getTransactionId(),
                entity.getCompletedAt(),
                entity.getCreatedAt()
        );
    }

    @Override
    public PaymentEntity toEntity(PaymentResponse dto) {
        throw new UnsupportedOperationException("Not implemented");
    }
}
