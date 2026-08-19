package com.petunincloud.delivery.service.payments;

import com.petunincloud.delivery.service.common.BaseService;
import com.petunincloud.delivery.service.orders.order.OrderRepository;
import com.petunincloud.delivery.service.orders.order.OrderService;
import com.petunincloud.delivery.service.orders.order.OrderStatus;
import com.petunincloud.delivery.service.orders.order.OrderEntity;
import com.petunincloud.delivery.service.payments.dto.PaymentRequest;
import com.petunincloud.delivery.service.payments.dto.PaymentResponse;
import com.petunincloud.delivery.service.users.UserEntity;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class PaymentService extends BaseService<PaymentEntity, PaymentResponse, PaymentSearchFilter> {

    private final static Logger log = LoggerFactory.getLogger(PaymentService.class);
    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;
    private final OrderRepository orderRepository;
    private final OrderService orderService;

    public PaymentService(
            PaymentRepository paymentRepository,
            PaymentMapper paymentMapper,
            OrderRepository orderRepository,
            OrderService orderService
    ) {
        this.paymentRepository = paymentRepository;
        this.paymentMapper = paymentMapper;
        this.orderRepository = orderRepository;
        this.orderService = orderService;
    }

    @Override
    protected List<PaymentEntity> findWithFilter(
            PaymentSearchFilter filter,
            Pageable pageable
    ) {
        return paymentRepository.searchAllByFilter(
                filter.email(),
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
    public PaymentResponse initiatePayment(
            PaymentRequest request,
            UserEntity user
    ) {
        log.info("Initiate payment: {} for user: {}", request, user.getEmail());
        long startTime = System.currentTimeMillis();

        try {
            OrderEntity order = orderRepository.findById(request.orderId())
                    .orElseThrow(() -> {
                        log.warn("Order not found: {}", request.orderId());
                        return new IllegalArgumentException("Order not found");
                    });

            PaymentEntity payment = paymentMapper.toEntity(user, order);

            PaymentEntity saved = paymentRepository.save(payment);

            long duration = System.currentTimeMillis() - startTime;
            log.info("Success initiate payment: {} for user: {}, duration={}ms",
                    request, user.getEmail(), duration);

            return paymentMapper.toResponse(saved);

        } catch (Exception e) {
            log.error("Failed initiate payment: {} for user: {}. Error: {}",
                    request, user.getEmail(), e.getMessage());
            throw e;
        }
    }

    @Transactional
    public PaymentResponse processPayment(Long paymentId) {
        log.info("Process payment: {}", paymentId);
        long startTime = System.currentTimeMillis();

        try {
            PaymentEntity payment = paymentRepository.findById(paymentId)
                    .orElseThrow(() -> {
                        log.warn("Payment not found: {}", paymentId);
                        return new IllegalArgumentException("Payment not found");
                    });

            if (payment.getStatus() != PaymentStatus.PENDING) {
                log.warn("Payment already processed: {} (status: {})", payment.getId(), payment.getStatus());
                throw new IllegalStateException("Payment already processed");
            }

            payment.setStatus(PaymentStatus.SUCCESS);
            log.info("Set status of SUCCESS for payment: {}", payment.getId());

            payment.setTransactionId(UUID.randomUUID().toString());
            payment.setCompletedAt(LocalDateTime.now().withNano(0));
            confirmOrder(payment.getOrder().getId());

            PaymentEntity saved = paymentRepository.save(payment);

            long duration = System.currentTimeMillis() - startTime;
            log.info("Success process payment: {}, duration={}ms", paymentId, duration);

            return paymentMapper.toResponse(saved);

        } catch (Exception e) {
            log.error("Failed process payment: {}. Error: {}", paymentId, e.getMessage());
            throw e;
        }
    }

    @Transactional
    private void confirmOrder(Long orderId) {
        log.info("Confirm order: {}", orderId);
        long startTime = System.currentTimeMillis();

        try {
            OrderEntity order = orderService.getOrderById(orderId);

            if (order.getStatus() != OrderStatus.PENDING) {
                log.warn("Status order: {} not equals of PENDING (status: {})", order.getId(), order.getStatus());
                throw new IllegalStateException("Only PENDING orders can be confirmed");
            }

            order.setStatus(OrderStatus.CONFIRMED);
            log.info("Set status of CONFIRMED for order: {}", order.getId());

            orderRepository.save(order);

            long duration = System.currentTimeMillis() - startTime;
            log.info("Success confirm order: {}, duration={}ms", orderId, duration);

        } catch (Exception e) {
            log.error("Failed confirm order: {}. Error: {}", orderId, e.getMessage());
            throw e;
        }
    }
}
