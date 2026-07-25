package com.petunincloud.delivery.service.restaurants.restaurant;

import com.petunincloud.delivery.service.TestSecurityConfig;
import com.petunincloud.delivery.service.orders.order.*;
import com.petunincloud.delivery.service.orders.order.dto.OrderResponse;
import com.petunincloud.delivery.service.restaurants.dish.DishEntity;
import com.petunincloud.delivery.service.restaurants.dish.DishMapper;
import com.petunincloud.delivery.service.restaurants.dish.DishRepository;
import com.petunincloud.delivery.service.restaurants.dish.dto.DishRequest;
import com.petunincloud.delivery.service.restaurants.dish.dto.DishResponse;
import com.petunincloud.delivery.service.restaurants.restaurant.dto.RestaurantRequest;
import com.petunincloud.delivery.service.restaurants.restaurant.dto.RestaurantResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Import;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Import(TestSecurityConfig.class)
public class RestaurantServiceTest {

    @Mock
    private RestaurantRepository restaurantRepository;

    @Mock
    private RestaurantMapper restaurantMapper;

    @Mock
    private DishRepository dishRepository;

    @Mock
    private DishMapper dishMapper;

    @Mock
    private OrderService orderService;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderMapper orderMapper;

    @InjectMocks
    private RestaurantService restaurantService;

    @Test
    void createRestaurant_ShouldCreateRestaurant() {
        RestaurantRequest request = new RestaurantRequest(
                "Додо",
                "ул. Пушинка, 25"
        );

        RestaurantEntity restaurant = new RestaurantEntity();

        RestaurantResponse restaurantResponse = new RestaurantResponse(
                1L,
                "Додо",
                "ул. Пушинка, 25",
                List.of()
        );

        when(restaurantMapper.toEntity(request))
                .thenReturn(restaurant);
        when(restaurantRepository.save(any(RestaurantEntity.class)))
                .thenReturn(restaurant);
        when(restaurantMapper.toResponse(restaurant))
                .thenReturn(restaurantResponse);

        RestaurantResponse result = restaurantService.createRestaurant(request);

        assertNotNull(result);

        verify(restaurantMapper, times(1))
                .toEntity(request);
        verify(restaurantRepository, times(1))
                .save(any(RestaurantEntity.class));
        verify(restaurantMapper, times(1))
                .toResponse(restaurant);
    }

    @Test
    void addDishToRestaurant_ShouldAddDish() {
        Long restaurantId = 1L;
        DishRequest dishRequest = new DishRequest(
                "Маргарита",
                BigDecimal.valueOf(300)
        );

        RestaurantEntity restaurant = new RestaurantEntity();
        restaurant.setId(restaurantId);

        DishEntity dish = new DishEntity(
                2L,
                "Маргарита",
                BigDecimal.valueOf(300),
                restaurant
        );

        DishEntity dishExample = new DishEntity(
                1L,
                "Гавайская",
                BigDecimal.valueOf(400),
                restaurant
        );

        DishResponse dishResponse = new DishResponse(
                2L,
                "Маргарита",
                BigDecimal.valueOf(300)
        );

        List<DishEntity> dishEntities = new ArrayList<>(List.of(dishExample));
        restaurant.setMenu(dishEntities);

        when(restaurantRepository.findById(restaurantId))
                .thenReturn(Optional.of(restaurant));
        when(dishRepository.save(any(DishEntity.class)))
                .thenReturn(dish);
        when(restaurantRepository.save(any(RestaurantEntity.class)))
                .thenReturn(restaurant);
        when(dishMapper.toResponse(dish))
                .thenReturn(dishResponse);

        DishResponse result = restaurantService
                .addDishToRestaurant(restaurantId, dishRequest);

        assertNotNull(result);
        assertEquals(dishRequest.name(), result.name());
        assertEquals(dishRequest.price(), result.price());
        assertEquals(RestaurantEntity.class, dish.getRestaurant().getClass());
        assertEquals(dish, restaurant.getMenu().getLast());

        verify(restaurantRepository, times(1))
                .findById(restaurantId);
        verify(dishRepository, times(1))
                .save(any(DishEntity.class));
        verify(restaurantRepository, times(1))
                .save(any(RestaurantEntity.class));
        verify(dishMapper, times(1))
                .toResponse(dish);
    }

    @Test
    void addDishToRestaurant_ShouldThrowException_WhenRestaurantNotFound() {
        Long restaurantId = 1L;
        DishRequest dishRequest = new DishRequest(
                "Маргарита",
                BigDecimal.valueOf(300)
        );

        when(restaurantRepository.findById(restaurantId))
                .thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> restaurantService.addDishToRestaurant(restaurantId, dishRequest));

        verify(dishRepository, never())
                .save(any(DishEntity.class));
        verify(restaurantRepository, never())
                .save(any(RestaurantEntity.class));
    }

    @Test
    void startCooking_ShouldStartCooking() {
        Long orderId = 1L;
        OrderEntity order = new OrderEntity();
        order.setId(orderId);
        order.setStatus(OrderStatus.CONFIRMED);

        OrderResponse orderResponse = new OrderResponse(
                orderId,
                1L,
                1L,
                "Додо",
                LocalDateTime.now().withNano(0),
                OrderStatus.COOKING,
                BigDecimal.valueOf(300),
                List.of()
        );

        when(orderService.getOrderById(orderId))
                .thenReturn(order);
        when(orderRepository.save(any(OrderEntity.class)))
                .thenReturn(order);
        when(orderMapper.toResponse(order))
                .thenReturn(orderResponse);

        OrderResponse result = restaurantService.startCooking(orderId);

        assertNotNull(result);
        assertEquals(OrderStatus.COOKING, result.status());

        verify(orderService, times(1))
                .getOrderById(orderId);
        verify(orderRepository, times(1))
                .save(any(OrderEntity.class));
        verify(orderMapper, times(1))
                .toResponse(order);
    }

    @Test
    void startCooking_ShouldThrowException_WhenOrderNotFound() {
        Long orderId = 1L;

        when(orderService.getOrderById(orderId))
                .thenThrow(new IllegalArgumentException("Order not found: " + orderId));

        assertThrows(IllegalArgumentException.class,
                () -> restaurantService.startCooking(orderId));

        verify(orderRepository, never())
                .save(any(OrderEntity.class));
    }

    @Test
    void startCooking_ShouldThrowException_WhenStatusNotConfirmed() {
        Long orderId = 1L;
        OrderEntity order = new OrderEntity();
        order.setStatus(OrderStatus.PENDING);

        when(orderService.getOrderById(orderId))
                .thenReturn(order);

        assertThrows(IllegalStateException.class,
                () -> restaurantService.startCooking(orderId));

        verify(orderRepository, never())
                .save(any(OrderEntity.class));
    }

    @Test
    void markAsReady_ShouldMarkAsReady() {
        Long orderId = 1L;
        OrderEntity order = new OrderEntity();
        order.setId(orderId);
        order.setStatus(OrderStatus.COOKING);

        OrderResponse orderResponse = new OrderResponse(
                orderId,
                1L,
                1L,
                "Додо",
                LocalDateTime.now().withNano(0),
                OrderStatus.READY,
                BigDecimal.valueOf(300),
                List.of()
        );

        when(orderService.getOrderById(orderId))
                .thenReturn(order);
        when(orderRepository.save(any(OrderEntity.class)))
                .thenReturn(order);
        when(orderMapper.toResponse(order))
                .thenReturn(orderResponse);

        OrderResponse result = restaurantService.markAsReady(orderId);

        assertNotNull(result);
        assertEquals(OrderStatus.READY, result.status());

        verify(orderService, times(1))
                .getOrderById(orderId);
        verify(orderRepository, times(1))
                .save(any(OrderEntity.class));
        verify(orderMapper, times(1))
                .toResponse(order);
    }

    @Test
    void markAsReady_ShouldThrowException_WhenOrderNotFound() {
        Long orderId = 1L;

        when(orderService.getOrderById(orderId))
                .thenThrow(new IllegalArgumentException("Order not found: " + orderId));

        assertThrows(IllegalArgumentException.class,
                () -> restaurantService.markAsReady(orderId));

        verify(orderRepository, never())
                .save(any(OrderEntity.class));
    }

    @Test
    void markAsReady_ShouldThrowException_WhenStatusNotCooking() {
        Long orderId = 1L;
        OrderEntity order = new OrderEntity();
        order.setStatus(OrderStatus.CONFIRMED);

        when(orderService.getOrderById(orderId))
                .thenReturn(order);

        assertThrows(IllegalStateException.class,
                () -> restaurantService.markAsReady(orderId));

        verify(orderRepository, never())
                .save(any(OrderEntity.class));
    }
}
