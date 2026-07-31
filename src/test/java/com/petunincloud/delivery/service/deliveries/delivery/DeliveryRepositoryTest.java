package com.petunincloud.delivery.service.deliveries.delivery;

import com.petunincloud.delivery.service.deliveries.courier.CourierEntity;
import com.petunincloud.delivery.service.deliveries.courier.CourierStatus;
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
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
public class DeliveryRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private DeliveryRepository deliveryRepository;

    private RestaurantEntity restaurant;
    private OrderEntity order1;
    private CourierEntity courier1;
    private DeliveryEntity delivery;

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
        OrderEntity order4 = createOrder(user2, restaurant, OrderStatus.DELIVERING, BigDecimal.valueOf(400));

        courier1 = createCourier("Михаил", "+79001234567", CourierStatus.AVAILABLE);
        CourierEntity courier2 = createCourier("Леонид", "+79231454567", CourierStatus.AVAILABLE);
        CourierEntity courier3 = createCourier("Андрей", "+79131634923", CourierStatus.AVAILABLE);
        CourierEntity courier4 = createCourier("Дмитрий", "+79921234923", CourierStatus.BUSY);

        delivery = createDeliveryWithReturn(
                order1,
                courier1,
                DeliveryStatus.ASSIGNED,
                LocalDateTime.now().withNano(0).minusHours(2).minusMinutes(20),
                LocalDateTime.now().withNano(0).minusHours(2),
                restaurant.getAddress(),
                user1.getAddress()
        );
        createDelivery(
                order2,
                courier2,
                DeliveryStatus.IN_PROGRESS,
                LocalDateTime.now().withNano(0).minusHours(1).minusMinutes(20),
                LocalDateTime.now().withNano(0).minusMinutes(50),
                restaurant.getAddress(),
                user2.getAddress()
        );
        createDelivery(
                order3,
                courier3,
                DeliveryStatus.DELIVERED,
                LocalDateTime.now().withNano(0).minusMinutes(50),
                LocalDateTime.now().withNano(0).minusMinutes(40),
                restaurant.getAddress(),
                user3.getAddress()
        );
        createDelivery(
                order4,
                courier4,
                DeliveryStatus.IN_PROGRESS,
                LocalDateTime.now().withNano(0).minusMinutes(30),
                LocalDateTime.now().withNano(0),
                restaurant.getAddress(),
                user2.getAddress()
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

    private CourierEntity createCourier(
            String name,
            String phone,
            CourierStatus status
    ) {
        CourierEntity courier = new CourierEntity();
        courier.setName(name);
        courier.setPhone(phone);
        courier.setStatus(status);

        entityManager.persist(courier);

        return courier;
    }

    private void createDelivery(
            OrderEntity order,
            CourierEntity courier,
            DeliveryStatus status,
            LocalDateTime assignedAt,
            LocalDateTime deliveredAt,
            String pickupAddress,
            String deliveryAddress
    ) {
        DeliveryEntity delivery = new DeliveryEntity();
        delivery.setOrder(order);
        delivery.setCourier(courier);
        delivery.setStatus(status);
        delivery.setAssignedAt(assignedAt);
        delivery.setDeliveredAt(deliveredAt);
        delivery.setPickupAddress(pickupAddress);
        delivery.setDeliveryAddress(deliveryAddress);

        entityManager.persist(delivery);
    }

    private DeliveryEntity createDeliveryWithReturn(
            OrderEntity order,
            CourierEntity courier,
            DeliveryStatus status,
            LocalDateTime assignedAt,
            LocalDateTime deliveredAt,
            String pickupAddress,
            String deliveryAddress
    ) {
        DeliveryEntity delivery = new DeliveryEntity();
        delivery.setOrder(order);
        delivery.setCourier(courier);
        delivery.setStatus(status);
        delivery.setAssignedAt(assignedAt);
        delivery.setDeliveredAt(deliveredAt);
        delivery.setPickupAddress(pickupAddress);
        delivery.setDeliveryAddress(deliveryAddress);

        entityManager.persist(delivery);

        return delivery;
    }

    @Test
    void searchAllByFilter_ShouldReturnDeliveryByOrderId() {
        Pageable pageable = PageRequest.of(0, 5);
        Long orderId = order1.getId();

        List<DeliveryEntity> deliveries = deliveryRepository.searchAllByFilter(
                orderId,
                null,
                null,
                null,
                null,
                pageable
        );

        assertThat(deliveries).hasSize(1);
        assertThat(deliveries).allMatch(
                delivery -> delivery.getOrder().getId().equals(orderId));
    }

    @Test
    void searchAllByFilter_ShouldReturnDeliveryByCourierId() {
        Pageable pageable = PageRequest.of(0, 5);
        Long courierId = courier1.getId();

        List<DeliveryEntity> deliveries = deliveryRepository.searchAllByFilter(
                null,
                courierId,
                null,
                null,
                null,
                pageable
        );

        assertThat(deliveries).hasSize(1);
        assertThat(deliveries).allMatch(
                delivery -> delivery.getCourier().getId().equals(courierId));
    }

    @Test
    void searchAllByFilter_ShouldReturnDeliveryByStatus() {
        Pageable pageable = PageRequest.of(0, 5);

        List<DeliveryEntity> deliveries = deliveryRepository.searchAllByFilter(
                null,
                null,
                DeliveryStatus.IN_PROGRESS,
                null,
                null,
                pageable
        );

        assertThat(deliveries).hasSize(2);
        assertThat(deliveries).allMatch(
                delivery -> delivery.getStatus().equals(DeliveryStatus.IN_PROGRESS));
    }

    @Test
    void searchAllByFilter_ShouldReturnDeliveryByAssignedAt() {
        Pageable pageable = PageRequest.of(0, 5);
        LocalDateTime assignedAt = LocalDateTime.now().withNano(0).minusMinutes(40);

        List<DeliveryEntity> deliveries = deliveryRepository.searchAllByFilter(
                null,
                null,
                null,
                assignedAt,
                null,
                pageable
        );
        assertThat(deliveries).hasSize(1);
    }

    @Test
    void searchAllByFilter_ShouldReturnDeliveryByDeliveredAt() {
        Pageable pageable = PageRequest.of(0, 5);
        LocalDateTime deliveredAt = LocalDateTime.now().withNano(0).minusMinutes(30);

        List<DeliveryEntity> deliveries = deliveryRepository.searchAllByFilter(
                null,
                null,
                null,
                null,
                deliveredAt,
                pageable
        );
        assertThat(deliveries).hasSize(3);
    }

    @Test
    void searchAllByFilter_ShouldApplyPagination() {
        Pageable pageable = PageRequest.of(0, 3);

        List<DeliveryEntity> deliveries = deliveryRepository.searchAllByFilter(
                null,
                null,
                null,
                null,
                null,
                pageable
        );
        assertThat(deliveries).hasSize(3);
    }

    @Test
    void findByIdWithOrderAndCourier_ShouldReturnDelivery() {
        Long id = delivery.getId();
        Long orderId = order1.getId();
        Long courierId = courier1.getId();

        Optional<DeliveryEntity> optionalDelivery = deliveryRepository.findByIdWithOrderAndCourier(
                id
        );

        assertThat(optionalDelivery).isPresent();
        assertThat(optionalDelivery.get().getOrder().getId()).isEqualTo(orderId);
        assertThat(optionalDelivery.get().getCourier().getId()).isEqualTo(courierId);
    }

    @Test
    void findByIdWithOrderAndCourier_ShouldReturnEmpty() {
        Long id = 9999L;

        Optional<DeliveryEntity> optionalDelivery = deliveryRepository.findByIdWithOrderAndCourier(
                id
        );

        assertThat(optionalDelivery).isEmpty();
    }

    @Test
    void existsByOrderId_ShouldReturnTrue() {
        Long orderId = order1.getId();

        boolean result = deliveryRepository.existsByOrderId(orderId);

        assertThat(result).isEqualTo(true);
    }

    @Test
    void existsByOrderId_ShouldReturnFalse() {
        Long orderId = 99999L;

        boolean result = deliveryRepository.existsByOrderId(orderId);

        assertThat(result).isEqualTo(false);
    }
}
