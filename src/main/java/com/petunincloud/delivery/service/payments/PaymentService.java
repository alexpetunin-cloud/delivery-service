package com.petunincloud.delivery.service.payments;

import com.petunincloud.delivery.service.common.BaseService;
import com.petunincloud.delivery.service.orders.OrderRepository;
import com.petunincloud.delivery.service.orders.OrderService;
import com.petunincloud.delivery.service.orders.entity.OrderEntity;
import com.petunincloud.delivery.service.payments.dto.PaymentRequest;
import com.petunincloud.delivery.service.payments.dto.PaymentResponse;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class PaymentService extends BaseService<PaymentEntity, PaymentResponse, PaymentSearchFilter> {

    private final PaymentRepository paymentRepository;
    private final PaymentMapper mapper;
    private final OrderRepository orderRepository;
    private final OrderService orderService;

    public PaymentService(
            PaymentRepository paymentRepository,
            PaymentMapper mapper,
            OrderRepository orderRepository,
            OrderService orderService
    ) {
        this.paymentRepository = paymentRepository;
        this.mapper = mapper;
        this.orderRepository = orderRepository;
        this.orderService = orderService;
    }

    @Override
    protected List<PaymentEntity> findWithFilter(PaymentSearchFilter filter, Pageable pageable) {
        return paymentRepository.searchAllByFilter(
                filter.userId(),
                filter.orderId(),
                filter.status(),
                filter.fromDate(),
                filter.toDate(),
                filter.minAmount(),
                filter.maxAmount(),
                pageable
        );
    }

    @Override
    protected PaymentMapper getMapper() {
        return mapper;
    }

    @Transactional
    public PaymentResponse initiatePayment(PaymentRequest request) {
        OrderEntity order = orderRepository.findById(request.orderId())
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + request.orderId()));

        PaymentEntity payment = mapper.toEntity(request.userId(), order);

        payment.setAmount(order.getTotalPrice());
        payment.setPaymentMethod("CARD");
        payment.setStatus(PaymentStatus.PENDING);
        payment.setCreatedAt(LocalDateTime.now().withNano(0));

        PaymentEntity saved = paymentRepository.save(payment);
        return mapper.toResponse(saved);
    }

    @Transactional
    public PaymentResponse processPayment(Long paymentId) {
        PaymentEntity payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found"));

        if (payment.getStatus() != PaymentStatus.PENDING) {
            throw new IllegalStateException("Payment already processed");
        }

        // Рандомный успех обработки платежа
        boolean isSuccess = Math.random() < 0.95;

        if (isSuccess) {
            payment.setStatus(PaymentStatus.SUCCESS);
            payment.setTransactionId(UUID.randomUUID().toString());
            payment.setCompletedAt(LocalDateTime.now().withNano(0));

            PaymentEntity savedPayment = paymentRepository.save(payment);

            orderService.confirmOrder(payment.getOrder());

            return mapper.toResponse(savedPayment);

        } else {
            payment.setStatus(PaymentStatus.FAILED);
            payment.setCompletedAt(LocalDateTime.now().withNano(0));

            PaymentEntity savedPayment = paymentRepository.save(payment);

            return mapper.toResponse(savedPayment);
        }
    }
}
