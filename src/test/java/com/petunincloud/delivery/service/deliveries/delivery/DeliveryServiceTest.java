package com.petunincloud.delivery.service.deliveries.delivery;

import com.petunincloud.delivery.service.TestSecurityConfig;
import com.petunincloud.delivery.service.deliveries.courier.CourierEntity;
import com.petunincloud.delivery.service.deliveries.courier.CourierRepository;
import com.petunincloud.delivery.service.deliveries.courier.CourierStatus;
import com.petunincloud.delivery.service.deliveries.delivery.dto.DeliveryResponse;
import com.petunincloud.delivery.service.orders.order.OrderEntity;
import com.petunincloud.delivery.service.orders.order.OrderRepository;
import com.petunincloud.delivery.service.orders.order.OrderService;
import com.petunincloud.delivery.service.orders.order.OrderStatus;
import com.petunincloud.delivery.service.restaurants.restaurant.RestaurantEntity;
import com.petunincloud.delivery.service.users.UserEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Import;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@Import(TestSecurityConfig.class)
@ExtendWith(MockitoExtension.class)
public class DeliveryServiceTest {

    @Mock
    private DeliveryRepository deliveryRepository;

    @Mock
    private CourierRepository courierRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderService orderService;

    @Mock
    private DeliveryMapper deliveryMapper;

    @InjectMocks
    private DeliveryService deliveryService;

    @Test
    void assignCourierToOrder_ShouldAssignCourierToOrder() {
        Long orderId = 1L;

        UserEntity user = new UserEntity();
        RestaurantEntity restaurant = new RestaurantEntity();
        OrderEntity order = new OrderEntity();
        CourierEntity courier = new CourierEntity();
        DeliveryEntity delivery = new DeliveryEntity();

        user.setId(1L);
        user.setAddress("ул. Пушкина, 25");

        restaurant.setId(1L);
        restaurant.setAddress("ул. Ленина, д. 1");

        order.setId(orderId);
        order.setStatus(OrderStatus.READY);
        order.setUser(user);
        order.setRestaurant(restaurant);

        courier.setId(1L);
        courier.setName("Алексей");
        courier.setPhone("+70002341234");
        courier.setStatus(CourierStatus.AVAILABLE);

        delivery.setOrder(order);
        delivery.setCourier(courier);
        delivery.setStatus(DeliveryStatus.ASSIGNED);
        delivery.setAssignedAt(LocalDateTime.now().withNano(0));
        delivery.setPickupAddress(order.getRestaurant().getAddress());
        delivery.setDeliveryAddress(order.getUser().getAddress());

        DeliveryResponse deliveryResponse = new DeliveryResponse(
                1L,
                orderId,
                courier.getId(),
                courier.getName(),
                DeliveryStatus.ASSIGNED,
                order.getUser().getAddress(),
                order.getRestaurant().getAddress(),
                LocalDateTime.now().withNano(0),
                null
        );

        when(orderService.getOrderById(orderId))
                .thenReturn(order);
        when(deliveryRepository.existsByOrderId(orderId))
                .thenReturn(false);
        when(courierRepository.findTopByStatus(CourierStatus.AVAILABLE))
                .thenReturn(Optional.of(courier));
        when(courierRepository.save(any(CourierEntity.class)))
                .thenReturn(courier);
        when(orderRepository.save(any(OrderEntity.class)))
                .thenReturn(order);
        when(deliveryRepository.save(any(DeliveryEntity.class)))
                .thenReturn(delivery);
        when(deliveryMapper.toResponse(delivery))
                .thenReturn(deliveryResponse);

        DeliveryResponse result = deliveryService.assignCourierToOrder(orderId);

        assertNotNull(result);
        assertEquals(DeliveryStatus.ASSIGNED, result.status());
        assertEquals(OrderStatus.DELIVERING, order.getStatus());
        assertEquals(CourierStatus.BUSY, courier.getStatus());
        assertEquals(order.getRestaurant().getAddress(), delivery.getPickupAddress());
        assertEquals(order.getUser().getAddress(), delivery.getDeliveryAddress());

        verify(orderService, times(1))
                .getOrderById(orderId);
        verify(deliveryRepository, times(1))
                .existsByOrderId(orderId);
        verify(courierRepository, times(1))
                .findTopByStatus(CourierStatus.AVAILABLE);
        verify(courierRepository, times(1))
                .save(any(CourierEntity.class));
        verify(orderRepository, times(1))
                .save(any(OrderEntity.class));
        verify(deliveryRepository, times(1))
                .save(any(DeliveryEntity.class));
        verify(deliveryMapper, times(1))
                .toResponse(delivery);
    }

    @Test
    void assignCourierToOrder_ShouldThrowException_WhenStatusNotReady() {
        Long orderId = 1L;

        OrderEntity order = new OrderEntity();

        order.setId(orderId);
        order.setStatus(OrderStatus.PENDING);

        when(orderService.getOrderById(orderId))
                .thenReturn(order);

        assertThrows(IllegalStateException.class,
                () -> deliveryService.assignCourierToOrder(orderId));

        verify(courierRepository, never())
                .save(any(CourierEntity.class));
        verify(deliveryRepository, never())
                .save(any(DeliveryEntity.class));
        verify(orderRepository, never())
                .save(any(OrderEntity.class));
    }

    @Test
    void assignCourierToOrder_ShouldThrowException_WhenStatusDelivering() {
        Long orderId = 1L;

        OrderEntity order = new OrderEntity();

        order.setId(orderId);
        order.setStatus(OrderStatus.DELIVERING);

        when(orderService.getOrderById(orderId))
                .thenReturn(order);

        assertThrows(IllegalStateException.class,
                () -> deliveryService.assignCourierToOrder(orderId));

        verify(courierRepository, never())
                .save(any(CourierEntity.class));
        verify(deliveryRepository, never())
                .save(any(DeliveryEntity.class));
        verify(orderRepository, never())
                .save(any(OrderEntity.class));
    }

    @Test
    void assignCourierToOrder_ShouldThrowException_WhenStatusDelivered() {
        Long orderId = 1L;

        OrderEntity order = new OrderEntity();

        order.setId(orderId);
        order.setStatus(OrderStatus.DELIVERED);

        when(orderService.getOrderById(orderId))
                .thenReturn(order);

        assertThrows(IllegalStateException.class,
                () -> deliveryService.assignCourierToOrder(orderId));

        verify(courierRepository, never())
                .save(any(CourierEntity.class));
        verify(deliveryRepository, never())
                .save(any(DeliveryEntity.class));
        verify(orderRepository, never())
                .save(any(OrderEntity.class));
    }

    @Test
    void assignCourierToOrder_ShouldThrowException_WhenExistsByOrderId() {
        Long orderId = 1L;

        OrderEntity order = new OrderEntity();

        order.setId(orderId);
        order.setStatus(OrderStatus.READY);

        when(orderService.getOrderById(orderId))
                .thenReturn(order);
        when(deliveryRepository.existsByOrderId(orderId))
                .thenReturn(true);

        assertThrows(IllegalStateException.class,
                () -> deliveryService.assignCourierToOrder(orderId));

        verify(courierRepository, never())
                .save(any(CourierEntity.class));
        verify(deliveryRepository, never())
                .save(any(DeliveryEntity.class));
        verify(orderRepository, never())
                .save(any(OrderEntity.class));
    }

    @Test
    void assignCourierToOrder_ShouldThrowException_WhenNotAvailableCouriers() {
        Long orderId = 1L;

        OrderEntity order = new OrderEntity();

        order.setId(orderId);
        order.setStatus(OrderStatus.READY);

        when(orderService.getOrderById(orderId))
                .thenReturn(order);
        when(courierRepository.findTopByStatus(CourierStatus.AVAILABLE))
                .thenReturn(Optional.empty());

        assertThrows(IllegalStateException.class,
                () -> deliveryService.assignCourierToOrder(orderId));

        verify(courierRepository, never())
                .save(any(CourierEntity.class));
        verify(deliveryRepository, never())
                .save(any(DeliveryEntity.class));
        verify(orderRepository, never())
                .save(any(OrderEntity.class));
    }

    @Test
    void getDeliveryById_ShouldReturnDeliveryById() {
        Long deliveryId = 1L;
        Long orderId = 1L;

        UserEntity user = new UserEntity();
        OrderEntity order = new OrderEntity();
        RestaurantEntity restaurant = new RestaurantEntity();
        CourierEntity courier = new CourierEntity();
        DeliveryEntity delivery = new DeliveryEntity();

        user.setAddress("ул. Пушкина, 25");

        restaurant.setAddress("ул. Ленина, д. 1");

        order.setUser(user);
        order.setRestaurant(restaurant);

        courier.setId(1L);
        courier.setName("Михаил");

        delivery.setId(deliveryId);
        delivery.setOrder(order);
        delivery.setCourier(courier);
        delivery.setStatus(DeliveryStatus.ASSIGNED);
        delivery.setAssignedAt(LocalDateTime.now().withNano(0));
        delivery.setPickupAddress(order.getRestaurant().getAddress());
        delivery.setDeliveryAddress(order.getUser().getAddress());

        DeliveryResponse deliveryResponse = new DeliveryResponse(
                deliveryId,
                orderId,
                courier.getId(),
                courier.getName(),
                DeliveryStatus.ASSIGNED,
                order.getUser().getAddress(),
                order.getRestaurant().getAddress(),
                LocalDateTime.now().withNano(0),
                null
        );

        when(deliveryRepository.findByIdWithOrderAndCourier(deliveryId))
                .thenReturn(Optional.of(delivery));
        when(deliveryMapper.toResponse(delivery))
                .thenReturn(deliveryResponse);

        DeliveryResponse result = deliveryService.getDeliveryById(deliveryId);

        assertNotNull(result);
        assertEquals(DeliveryStatus.ASSIGNED, result.status());
        assertEquals(order.getRestaurant().getAddress(), delivery.getPickupAddress());
        assertEquals(order.getUser().getAddress(), delivery.getDeliveryAddress());
        assertEquals(deliveryId, result.id());

        verify(deliveryRepository, times(1))
                .findByIdWithOrderAndCourier(deliveryId);
        verify(deliveryMapper, times(1))
                .toResponse(delivery);
    }

    @Test
    void getDeliveryById_ShouldThrowException_WhenNotExistDelivery() {
        Long deliveryId = 1L;

        when(deliveryRepository.findByIdWithOrderAndCourier(deliveryId))
                .thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> deliveryService.getDeliveryById(deliveryId));

        verify(deliveryMapper, never())
                .toResponse(any(DeliveryEntity.class));
    }

    @Test
    void completeDelivery_ShouldCompleteDelivery() {
        Long deliveryId = 1L;
        Long orderId = 1L;

        UserEntity user = new UserEntity();
        OrderEntity order = new OrderEntity();
        CourierEntity courier = new CourierEntity();
        RestaurantEntity restaurant = new RestaurantEntity();
        DeliveryEntity delivery = new DeliveryEntity();

        user.setAddress("ул. Пушкина, 25");

        restaurant.setAddress("ул. Ленина, д. 1");

        order.setUser(user);
        order.setRestaurant(restaurant);
        order.setStatus(OrderStatus.DELIVERING);

        courier.setId(1L);
        courier.setName("Михаил");

        delivery.setId(deliveryId);
        delivery.setOrder(order);
        delivery.setCourier(courier);
        delivery.setStatus(DeliveryStatus.ASSIGNED);
        delivery.setAssignedAt(LocalDateTime.now().withNano(0));
        delivery.setPickupAddress(order.getRestaurant().getAddress());
        delivery.setDeliveryAddress(order.getUser().getAddress());

        DeliveryResponse deliveryResponse = new DeliveryResponse(
                deliveryId,
                orderId,
                courier.getId(),
                courier.getName(),
                DeliveryStatus.DELIVERED,
                order.getUser().getAddress(),
                order.getRestaurant().getAddress(),
                LocalDateTime.now().withNano(0),
                LocalDateTime.now().withNano(0)
        );

        when(deliveryRepository.findByIdWithOrderAndCourier(deliveryId))
                .thenReturn(Optional.of(delivery));
        when(orderRepository.save(any(OrderEntity.class)))
                .thenReturn(order);
        when(courierRepository.save(any(CourierEntity.class)))
                .thenReturn(courier);
        when(deliveryRepository.save(any(DeliveryEntity.class)))
                .thenReturn(delivery);
        when(deliveryMapper.toResponse(delivery))
                .thenReturn(deliveryResponse);

        DeliveryResponse result = deliveryService.completeDelivery(deliveryId);

        assertNotNull(result);
        assertEquals(OrderStatus.DELIVERED, order.getStatus());
        assertEquals(DeliveryStatus.DELIVERED, result.status());
        assertEquals(LocalDateTime.class, result.deliveredAt().getClass());
        assertEquals(CourierStatus.AVAILABLE, courier.getStatus());

        verify(deliveryRepository, times(1))
                .findByIdWithOrderAndCourier(deliveryId);
        verify(orderRepository, times(1))
                .save(any(OrderEntity.class));
        verify(courierRepository, times(1))
                .save(any(CourierEntity.class));
        verify(deliveryRepository, times(1))
                .save(any(DeliveryEntity.class));
        verify(deliveryMapper, times(1))
                .toResponse(any(DeliveryEntity.class));
    }

    @Test
    void completeDelivery_ShouldThrowException_WhenNotFoundDelivery() {
        Long deliveryId = 1L;

        when(deliveryRepository.findByIdWithOrderAndCourier(deliveryId))
                .thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> deliveryService.completeDelivery(deliveryId));

        verify(orderRepository, never())
                .save(any(OrderEntity.class));
        verify(courierRepository, never())
                .save(any(CourierEntity.class));
        verify(deliveryRepository, never())
                .save(any(DeliveryEntity.class));
    }

    @Test
    void completeDelivery_ShouldThrowException_WhenStatusNotDelivering() {
        Long deliveryId = 1L;

        OrderEntity order = new OrderEntity();
        CourierEntity courier = new CourierEntity();
        DeliveryEntity delivery = new DeliveryEntity();

        order.setStatus(OrderStatus.READY);

        delivery.setCourier(courier);
        delivery.setOrder(order);

        when(deliveryRepository.findByIdWithOrderAndCourier(deliveryId))
                .thenReturn(Optional.of(delivery));

        assertThrows(IllegalStateException.class,
                () -> deliveryService.completeDelivery(deliveryId));

        verify(orderRepository, never())
                .save(any(OrderEntity.class));
        verify(courierRepository, never())
                .save(any(CourierEntity.class));
        verify(deliveryRepository, never())
                .save(any(DeliveryEntity.class));
    }
}
