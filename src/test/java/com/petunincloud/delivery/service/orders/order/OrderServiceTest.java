package com.petunincloud.delivery.service.orders.order;

import com.petunincloud.delivery.service.orders.order.dto.OrderRequest;
import com.petunincloud.delivery.service.orders.orderItem.dto.OrderItemRequest;
import com.petunincloud.delivery.service.orders.order.dto.OrderResponse;
import com.petunincloud.delivery.service.restaurants.dish.DishService;
import com.petunincloud.delivery.service.restaurants.dish.dto.DishResponse;
import com.petunincloud.delivery.service.restaurants.restaurant.RestaurantEntity;
import com.petunincloud.delivery.service.restaurants.restaurant.RestaurantRepository;
import com.petunincloud.delivery.service.users.UserEntity;
import com.petunincloud.delivery.service.users.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderMapper orderMapper;

    @Mock
    private DishService dishService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RestaurantRepository restaurantRepository;

    @InjectMocks
    private OrderService orderService;

    @Test
    void createOrder_ShouldCreateOrderWithItems_WhenValidRequest() {
        Long userId = 1L;
        Long restaurantId = 10L;
        Long dishId = 100L;

        UserEntity user = new UserEntity();
        user.setId(userId);

        RestaurantEntity restaurant = new RestaurantEntity();
        restaurant.setId(restaurantId);
        restaurant.setAddress("ул. Ленина, д. 1");

        OrderItemRequest itemRequest = new OrderItemRequest(dishId, 2);
        OrderRequest request = new OrderRequest(userId, restaurantId, List.of(itemRequest));

        OrderEntity entity = new OrderEntity();
        entity.setId(1L);
        entity.setUser(user);
        entity.setRestaurant(restaurant);
        entity.setStatus(OrderStatus.PENDING);
        entity.setTotalPrice(BigDecimal.valueOf(300));

        OrderResponse response = new OrderResponse(
                1L,
                userId,
                restaurantId,
                "Ресторан",
                LocalDateTime.now(),
                OrderStatus.PENDING,
                BigDecimal.valueOf(300),
                List.of()
        );

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.of(restaurant));

        DishResponse dishMock = new DishResponse(dishId, "Пицца", BigDecimal.valueOf(150));
        when(dishService.getDishById(dishId)).thenReturn(dishMock);

        when(orderMapper.toEntity(request, user)).thenReturn(entity);
        when(orderRepository.save(any(OrderEntity.class))).thenReturn(entity);
        when(orderMapper.toResponse(entity)).thenReturn(response);

        OrderResponse result = orderService.createOrder(request);

        assertNotNull(result);
        assertEquals(OrderStatus.PENDING, result.status());
        assertEquals(0, BigDecimal.valueOf(300).compareTo(result.totalPrice()));

        verify(orderRepository, times(1)).save(any(OrderEntity.class));
        verify(orderMapper, times(1)).toEntity(request, user);
        verify(orderMapper, times(1)).toResponse(entity);
    }

    @Test
    void createOrder_ShouldThrowException_WhenUserNotFound() {
        Long userId = 999L;
        OrderRequest request = new OrderRequest(userId, 1L, List.of());

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> orderService.createOrder(request));
    }

    @Test
    void createOrder_ShouldThrowException_WhenRestaurantNotFound() {
        Long userId = 1L;
        Long restaurantId = 999L;
        OrderRequest request = new OrderRequest(userId, restaurantId, List.of());

        when(userRepository.findById(userId)).thenReturn(Optional.of(new UserEntity()));
        when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> orderService.createOrder(request));
    }

    @Test
    void cancelOrder_ShouldCancelPendingOrder() {
        Long orderId = 1L;
        OrderEntity order = new OrderEntity();
        order.setId(orderId);
        order.setStatus(OrderStatus.PENDING);

        OrderResponse response = new OrderResponse(
                orderId,
                1L,
                1L,
                "Ресторан",
                LocalDateTime.now(),
                OrderStatus.CANCELED,
                BigDecimal.ZERO,
                List.of()
        );

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(orderRepository.save(order)).thenReturn(order);
        when(orderMapper.toResponse(order)).thenReturn(response);

        OrderResponse result = orderService.cancelOrder(orderId);

        assertEquals(OrderStatus.CANCELED, result.status());
        verify(orderRepository, times(1)).save(order);
    }

    @Test
    void cancelOrder_ShouldThrowException_WhenOrderDelivered() {
        Long orderId = 1L;
        OrderEntity order = new OrderEntity();
        order.setId(orderId);
        order.setStatus(OrderStatus.DELIVERED);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        assertThrows(IllegalStateException.class, () -> orderService.cancelOrder(orderId));
    }
}