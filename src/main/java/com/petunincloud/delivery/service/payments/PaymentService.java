package com.petunincloud.delivery.service.payments;

import com.petunincloud.delivery.service.common.BaseService;
import com.petunincloud.delivery.service.orders.OrderRepository;
import com.petunincloud.delivery.service.orders.OrderService;
import com.petunincloud.delivery.service.orders.OrderStatus;
import com.petunincloud.delivery.service.orders.entity.OrderEntity;
import com.petunincloud.delivery.service.payments.dto.PaymentRequest;
import com.petunincloud.delivery.service.payments.dto.PaymentResponse;
import com.petunincloud.delivery.service.users.UserEntity;
import com.petunincloud.delivery.service.users.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class PaymentService extends BaseService<PaymentEntity, PaymentResponse, PaymentSearchFilter> {

    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;
    private final OrderRepository orderRepository;
    private final OrderService orderService;
    private final UserRepository userRepository;

    public PaymentService(
            PaymentRepository paymentRepository,
            PaymentMapper paymentMapper,
            OrderRepository orderRepository,
            OrderService orderService,
            UserRepository userRepository
    ) {
        this.paymentRepository = paymentRepository;
        this.paymentMapper = paymentMapper;
        this.orderRepository = orderRepository;
        this.orderService = orderService;
        this.userRepository = userRepository;
    }

    @Override
    protected List<PaymentEntity> findWithFilter(PaymentSearchFilter filter, Pageable pageable) {
        return paymentRepository.searchAllByFilter(
                filter.userId(),
                filter.orderId(),
                filter.status(),
                filter.fromDate(),
                filter.toDate(),
                pageable
        );
    }

    @Override
    protected PaymentMapper getMapper() {
        return paymentMapper;
    }

    @Transactional
    public PaymentResponse initiatePayment(PaymentRequest request) {
        OrderEntity order = orderRepository.findById(request.orderId())
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + request.orderId()));

        UserEntity user = userRepository.findById(request.userId())
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + request.userId()));

        PaymentEntity payment = paymentMapper.toEntity(user, order);

        payment.setAmount(order.getTotalPrice());
        payment.setPaymentMethod("CARD");
        payment.setStatus(PaymentStatus.PENDING);
        payment.setCreatedAt(LocalDateTime.now().withNano(0));

        PaymentEntity saved = paymentRepository.save(payment);
        return paymentMapper.toResponse(saved);
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

            confirmOrder(payment.getOrder().getId());

            return paymentMapper.toResponse(savedPayment);

        } else {
            payment.setStatus(PaymentStatus.FAILED);
            payment.setCompletedAt(LocalDateTime.now().withNano(0));

            PaymentEntity savedPayment = paymentRepository.save(payment);

            return paymentMapper.toResponse(savedPayment);
        }
    }

    @Transactional
    private void confirmOrder(Long orderId) {
        OrderEntity order = orderService.getOrderById(orderId);

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new IllegalStateException("Only PENDING orders can be confirmed");
        }

        order.setStatus(OrderStatus.CONFIRMED);
        orderRepository.save(order);
    }
}
