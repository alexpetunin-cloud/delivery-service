package com.petunincloud.delivery.service.payments;

import com.petunincloud.delivery.service.orders.order.OrderEntity;
import com.petunincloud.delivery.service.orders.order.OrderStatus;
import com.petunincloud.delivery.service.restaurants.restaurant.RestaurantEntity;
import com.petunincloud.delivery.service.users.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@DataJpaTest
@ActiveProfiles("test")
public class PaymentRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private PaymentRepository paymentRepository;

    private RestaurantEntity restaurant;
    private OrderEntity order1;

    @BeforeEach
    void setUp() {
        UserEntity user1 = createUser(
                "user1@gmail.com",
                "+79990000001",
                "Алексей",
                "ул. Ленина, 102",
                "password123"
        );

        UserEntity user2 = createUser(
                "user2@gmail.com",
                "+79230000002",
                "Жекич",
                "ул. Красная, 12",
                "password1234"
        );

        UserEntity user3 = createUser(
                "user3@gmail.com",
                "+79232305022",
                "Роман",
                "ул. Димитрова, 13",
                "password12345"
        );

        restaurant = new RestaurantEntity();

        restaurant.setName("Додо Пицца");
        restaurant.setAddress("пр. Калинина 8");

        entityManager.persist(restaurant);

        order1 = createOrder(user1, restaurant, OrderStatus.READY, BigDecimal.valueOf(100));
        OrderEntity order2 = createOrder(user2, restaurant, OrderStatus.DELIVERING, BigDecimal.valueOf(200));
        OrderEntity order3 = createOrder(user3, restaurant, OrderStatus.DELIVERED, BigDecimal.valueOf(300));

        createPayment(
                order1,
                user1,
                BigDecimal.valueOf(100),
                "CARD",
                UUID.randomUUID().toString(),
                PaymentStatus.SUCCESS,
                LocalDateTime.now().withNano(0).minusHours(2),
                LocalDateTime.now().withNano(0).minusHours(1).minusMinutes(59)
        );

        createPayment(
                order2,
                user2,
                BigDecimal.valueOf(200),
                "CARD",
                UUID.randomUUID().toString(),
                PaymentStatus.SUCCESS,
                LocalDateTime.now().withNano(0).minusHours(1).minusMinutes(40),
                LocalDateTime.now().withNano(0).minusHours(1).minusMinutes(39)
        );

        createPayment(
                order3,
                user3,
                BigDecimal.valueOf(300),
                "CARD",
                UUID.randomUUID().toString(),
                PaymentStatus.PENDING,
                LocalDateTime.now().withNano(0).minusHours(1),
                LocalDateTime.now().withNano(0).minusMinutes(59)
        );

        entityManager.flush();
        entityManager.clear();
    }

    private UserEntity createUser(
            String email,
            String phone,
            String name,
            String address,
            String password
    ) {
        UserEntity user = new UserEntity();

        user.setEmail(email);
        user.setPhone(phone);
        user.setName(name);
        user.setAddress(address);
        user.setPassword(password);

        entityManager.persist(user);

        return user;
    }

    private OrderEntity createOrder(
            UserEntity user,
            RestaurantEntity restaurant,
            OrderStatus status,
            BigDecimal total
    ) {
        OrderEntity order = new OrderEntity();

        order.setUser(user);
        order.setRestaurant(restaurant);
        order.setStatus(status);
        order.setDateTime(LocalDateTime.now());
        order.setTotalPrice(total);

        entityManager.persist(order);

        return order;
    }

    private void createPayment(
            OrderEntity order,
            UserEntity user,
            BigDecimal amount,
            String paymentMethod,
            String transactionId,
            PaymentStatus status,
            LocalDateTime createdAt,
            LocalDateTime completedAt
    ) {
        PaymentEntity payment = new PaymentEntity();

        payment.setOrder(order);
        payment.setUser(user);
        payment.setAmount(amount);
        payment.setPaymentMethod(paymentMethod);
        payment.setTransactionId(transactionId);
        payment.setStatus(status);
        payment.setCreatedAt(createdAt);
        payment.setCompletedAt(completedAt);

        entityManager.persist(payment);
    }

    @Test
    void searchAllByFilter_ShouldReturnPaymentByEmail() {
        Pageable pageable = PageRequest.of(0, 5);

        List<PaymentEntity> payments = paymentRepository.searchAllByFilter(
                "user1@gmail.com",
                null,
                null,
                null,
                null,
                pageable
        );

        assertThat(payments).hasSize(1);
        assertThat(payments.get(0).getUser().getEmail())
                .isEqualTo("user1@gmail.com");
    }

    @Test
    void searchAllByFilter_ShouldReturnPaymentByOrderId() {
        Pageable pageable = PageRequest.of(0, 5);

        Long orderId = order1.getId();

        List<PaymentEntity> payments = paymentRepository.searchAllByFilter(
                null,
                orderId,
                null,
                null,
                null,
                pageable
        );

        assertThat(payments).hasSize(1);
        assertThat(payments.get(0).getOrder().getId())
                .isEqualTo(orderId);
    }

    @Test
    void searchAllByFilter_ShouldReturnPaymentByStatus() {
        Pageable pageable = PageRequest.of(0, 5);

        List<PaymentEntity> payments = paymentRepository.searchAllByFilter(
                null,
                null,
                PaymentStatus.SUCCESS,
                null,
                null,
                pageable
        );

        assertThat(payments).hasSize(2);
        assertThat(payments).allMatch(
                payment -> payment.getStatus().equals(PaymentStatus.SUCCESS));
    }

    @Test
    void searchAllByFilter_ShouldReturnPaymentByFromDate() {
        Pageable pageable = PageRequest.of(0, 5);

        List<PaymentEntity> payments = paymentRepository.searchAllByFilter(
                null,
                null,
                null,
                LocalDateTime.now().withNano(0).minusHours(1).minusMinutes(30),
                null,
                pageable
        );

        assertThat(payments).hasSize(1);
    }

    @Test
    void searchAllByFilter_ShouldReturnPaymentByToDate() {
        Pageable pageable = PageRequest.of(0, 5);

        List<PaymentEntity> payments = paymentRepository.searchAllByFilter(
                null,
                null,
                null,
                null,
                LocalDateTime.now().withNano(0).minusHours(1),
                pageable
        );

        assertThat(payments).hasSize(2);
    }

    @Test
    void searchAllByFilter_ShouldApplyPagination() {
        Pageable pageable = PageRequest.of(0, 2);

        List<PaymentEntity> payments = paymentRepository.searchAllByFilter(
                null,
                null,
                null,
                null,
                null,
                pageable
        );

        assertThat(payments).hasSize(2);
    }
}
