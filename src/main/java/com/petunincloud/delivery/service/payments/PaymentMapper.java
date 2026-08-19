package com.petunincloud.delivery.service.payments;

import com.petunincloud.delivery.service.common.BaseMapper;
import com.petunincloud.delivery.service.orders.order.OrderEntity;
import com.petunincloud.delivery.service.payments.dto.PaymentResponse;
import com.petunincloud.delivery.service.users.UserEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class PaymentMapper implements BaseMapper<PaymentEntity, PaymentResponse> {

    public PaymentEntity toEntity(
            UserEntity user,
            OrderEntity order
    ) {
        PaymentEntity payment = new PaymentEntity();

        payment.setUser(user);
        payment.setOrder(order);
        payment.setAmount(order.getTotalPrice());
        payment.setPaymentMethod("CARD");
        payment.setStatus(PaymentStatus.PENDING);
        payment.setCreatedAt(LocalDateTime.now().withNano(0));

        return payment;
    }

    public PaymentResponse toResponse(PaymentEntity entity) {
        return new PaymentResponse(
                entity.getId(),
                entity.getOrder().getId(),
                entity.getUser().getId(),
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
