package com.petunincloud.delivery.service.payments;

import com.petunincloud.delivery.service.TestSecurityConfig;
import com.petunincloud.delivery.service.orders.order.OrderEntity;
import com.petunincloud.delivery.service.orders.order.OrderRepository;
import com.petunincloud.delivery.service.orders.order.OrderService;
import com.petunincloud.delivery.service.orders.order.OrderStatus;
import com.petunincloud.delivery.service.payments.dto.PaymentRequest;
import com.petunincloud.delivery.service.payments.dto.PaymentResponse;
import com.petunincloud.delivery.service.users.UserEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Import;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Import(TestSecurityConfig.class)
public class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private PaymentMapper paymentMapper;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderService orderService;

    @InjectMocks
    private PaymentService paymentService;

    @Test
    void initiatePayment_ShouldInitiatePayment() {
        Long orderId = 1L;
        Long userId = 1L;

        OrderEntity order = new OrderEntity();
        order.setTotalPrice(BigDecimal.valueOf(300));

        UserEntity user = new UserEntity();
        PaymentEntity payment = new PaymentEntity();

        PaymentRequest paymentRequest = new PaymentRequest(
                orderId,
                "user@gmail.com"
        );

        PaymentResponse paymentResponse = new PaymentResponse(
                1L,
                orderId,
                userId,
                BigDecimal.valueOf(300),
                "CARD",
                PaymentStatus.PENDING,
                null,
                null,
                LocalDateTime.now().withNano(0)
        );

        when(orderRepository.findById(paymentRequest.orderId()))
                .thenReturn(Optional.of(order));
        when(paymentMapper.toEntity(user, order))
                .thenReturn(payment);
        when(paymentRepository.save(any(PaymentEntity.class)))
                .thenReturn(payment);
        when(paymentMapper.toResponse(payment))
                .thenReturn(paymentResponse);

        PaymentResponse result = paymentService.initiatePayment(paymentRequest, user);

        assertNotNull(result);
        assertEquals("CARD", result.paymentMethod());
        assertEquals(order.getTotalPrice(), result.amount());
        assertEquals(PaymentStatus.PENDING, result.status());
        assertEquals(LocalDateTime.class, result.createdAt().getClass());

        verify(orderRepository, times(1))
                .findById(orderId);
        verify(paymentMapper, times(1))
                .toEntity(user, order);
        verify(paymentRepository, times(1))
                .save(any(PaymentEntity.class));
        verify(paymentMapper, times(1))
                .toResponse(payment);
    }

    @Test
    void initiatePayment_ShouldThrowException_WhenOrderNotFound() {
        Long orderId = 1L;

        PaymentRequest paymentRequest = new PaymentRequest(
                1L,
                "user@gmail.com"
        );
        UserEntity user = new UserEntity();

        when(orderRepository.findById(orderId))
                .thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> paymentService.initiatePayment(paymentRequest, user));

        verify(paymentRepository, never())
                .save(any(PaymentEntity.class));
    }

    @Test
    void processPayment_ShouldConfirmPayment() {
        Long orderId = 1L;
        Long userId = 1L;

        OrderEntity order = new OrderEntity();
        order.setId(orderId);
        order.setStatus(OrderStatus.PENDING);

        PaymentEntity payment = new PaymentEntity();
        payment.setStatus(PaymentStatus.PENDING);
        payment.setId(1L);
        payment.setOrder(order);

        PaymentResponse paymentResponse = new PaymentResponse(
                1L,
                orderId,
                userId,
                BigDecimal.valueOf(300),
                "CARD",
                PaymentStatus.SUCCESS,
                UUID.randomUUID().toString(),
                LocalDateTime.now().withNano(0),
                LocalDateTime.now().withNano(0)
        );

        when(paymentRepository.findById(payment.getId()))
                .thenReturn(Optional.of(payment));
        when(orderService.getOrderById(orderId))
                .thenReturn(order);
        when(orderRepository.save(any(OrderEntity.class)))
                .thenReturn(order);
        when(paymentRepository.save(any(PaymentEntity.class)))
                .thenReturn(payment);
        when(paymentMapper.toResponse(payment))
                .thenReturn(paymentResponse);

        PaymentResponse result = paymentService.processPayment(payment.getId());

        assertNotNull(result);
        assertNotNull(result.transactionId());
        assertEquals(PaymentStatus.SUCCESS, result.status());
        assertEquals(LocalDateTime.class, result.completedAt().getClass());
        assertEquals(OrderStatus.CONFIRMED, order.getStatus());

        verify(paymentRepository, times(1))
                .findById(orderId);
        verify(orderService, times(1))
                .getOrderById(orderId);
        verify(orderRepository, times(1))
                .save(any(OrderEntity.class));
        verify(paymentRepository, times(1))
                .save(any(PaymentEntity.class));
        verify(paymentMapper, times(1))
                .toResponse(payment);
    }

    @Test
    void processPayment_ShouldThrowException_WhenPaymentNotFound() {
        Long paymentId = 1L;

        when(paymentRepository.findById(paymentId))
                .thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> paymentService.processPayment(paymentId));

        verify(orderRepository, never())
                .save(any(OrderEntity.class));
        verify(paymentRepository, never())
                .save(any(PaymentEntity.class));
    }

    @Test
    void processPayment_ShouldThrowException_WhenPaymentAlreadyProcessed() {
        Long paymentId = 1L;

        PaymentEntity payment = new PaymentEntity();
        payment.setStatus(PaymentStatus.FAILED);

        when(paymentRepository.findById(paymentId))
                .thenReturn(Optional.of(payment));

        assertThrows(IllegalStateException.class,
                () -> paymentService.processPayment(paymentId));

        verify(orderRepository, never())
                .save(any(OrderEntity.class));
        verify(paymentRepository, never())
                .save(any(PaymentEntity.class));
    }
}
