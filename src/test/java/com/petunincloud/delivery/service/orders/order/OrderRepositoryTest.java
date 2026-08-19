package com.petunincloud.delivery.service.orders.order;

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

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest // Поднимает только JPA-слой
@ActiveProfiles("test") // Подключает профиль test, который использует H2 in-memory
class OrderRepositoryTest {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private TestEntityManager entityManager; // Помогает сохранять данные в тестовую БД и управлять сессией

    private UserEntity user1;
    private UserEntity user2;
    private RestaurantEntity restaurant;

    @BeforeEach
    void setUp() {
        user1 = new UserEntity();

        user1.setEmail("user1@test.com");
        user1.setPhone("+79990000001");
        user1.setName("User One");
        user1.setAddress("Address 1");
        user1.setPassword("password123");

        entityManager.persist(user1); // Сохраняет объект в БД

        user2 = new UserEntity();

        user2.setEmail("user2@test.com");
        user2.setPhone("+79990000002");
        user2.setName("User Two");
        user2.setAddress("Address 2");
        user2.setPassword("password123");

        entityManager.persist(user2);

        restaurant = new RestaurantEntity();

        restaurant.setName("Додо Пицца");
        restaurant.setAddress("пр. Калинина 8");

        entityManager.persist(restaurant);

        createOrder(user1, restaurant, OrderStatus.PENDING, BigDecimal.valueOf(100));
        createOrder(user1, restaurant, OrderStatus.CONFIRMED, BigDecimal.valueOf(200));
        createOrder(user2, restaurant, OrderStatus.PENDING, BigDecimal.valueOf(300));

        entityManager.flush(); // Принудительно синхронизирует с БД
        entityManager.clear(); // Очищает кэш Hibernate
    }

    private void createOrder(
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
    }

    @Test
    void searchAllByFilter_ShouldReturnOrdersByUserId() {
        Pageable pageable = PageRequest.of(0, 5);

        List<OrderEntity> orders = orderRepository.searchAllByFilter(
                user1.getId(),
                null,
                pageable
        );

        // Проверяем, что вернулось ровно 2 заказа
        assertThat(orders).hasSize(2);

        // Проверяем, что все заказы принадлежат user1
        assertThat(orders).allMatch(
                order -> order.getUser().getId().equals(user1.getId()));
    }

    @Test
    void searchAllByFilter_ShouldReturnOrdersByRestaurantId() {
        Pageable pageable = PageRequest.of(0, 5);

        List<OrderEntity> orders = orderRepository.searchAllByFilter(
                null,
                restaurant.getId(),
                pageable
        );

        assertThat(orders).hasSize(3);
    }

    @Test
    void searchAllByFilter_ShouldApplyPagination() {
        Pageable pageable = PageRequest.of(0, 2);

        List<OrderEntity> orders = orderRepository.searchAllByFilter(null, null, pageable);

        assertThat(orders).hasSize(2);
    }
}