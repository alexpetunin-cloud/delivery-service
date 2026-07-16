package com.petunincloud.delivery.service.orders.orderItem;

import com.petunincloud.delivery.service.orders.order.OrderEntity;
import com.petunincloud.delivery.service.orders.order.OrderRepository;
import com.petunincloud.delivery.service.orders.order.OrderStatus;
import com.petunincloud.delivery.service.orders.orderItem.dto.OrderItemRequest;
import com.petunincloud.delivery.service.orders.orderItem.dto.OrderItemResponse;
import com.petunincloud.delivery.service.restaurants.dish.DishService;
import com.petunincloud.delivery.service.restaurants.dish.dto.DishResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderItemServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private DishService dishService;

    @Mock
    private OrderItemMapper orderItemMapper;

    @InjectMocks
    private OrderItemService orderItemService;

    @Test
    void addItemToOrder_ShouldAddItemAndUpdateTotalPrice() {
        Long orderId = 1L;
        Long dishId = 100L;
        int quantity = 2;
        BigDecimal dishPrice = BigDecimal.valueOf(150);
        BigDecimal existingTotal = BigDecimal.valueOf(200);

        OrderEntity order = new OrderEntity();
        order.setId(orderId);
        order.setStatus(OrderStatus.PENDING);
        order.setTotalPrice(existingTotal);
        order.setItems(new ArrayList<>());

        OrderItemRequest request = new OrderItemRequest(dishId, quantity);

        DishResponse dishResponse = new DishResponse(dishId, "Пицца", dishPrice);

        OrderItemEntity entity = new OrderItemEntity();
        entity.setId(1L);
        entity.setDishId(dishId);
        entity.setQuantity(quantity);
        entity.setPrice(dishPrice);

        OrderItemEntity savedEntity = new OrderItemEntity();
        savedEntity.setId(1L);

        OrderItemResponse response = new OrderItemResponse(
                dishId,
                "Пицца",
                quantity,
                dishPrice
        );

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(dishService.getDishById(dishId)).thenReturn(dishResponse);
        when(orderItemMapper.toEntity(request, order)).thenReturn(entity);
        when(orderItemRepository.save(entity)).thenReturn(savedEntity);
        when(orderItemMapper.toResponse(savedEntity)).thenReturn(response);

        OrderItemResponse result = orderItemService.addItemToOrder(orderId, request);

        assertNotNull(result);
        assertEquals(dishId, result.dishId());
        assertEquals(quantity, result.quantity());

        assertEquals(0, BigDecimal.valueOf(500).compareTo(order.getTotalPrice()));
        assertEquals(1, order.getItems().size());

        verify(orderRepository, times(1)).findById(orderId);
        verify(dishService, times(1)).getDishById(dishId);
        verify(orderItemRepository, times(1)).save(entity);
        verify(orderRepository, times(1)).save(order);
    }

    @Test
    void addItemToOrder_ShouldThrowException_WhenOrderNotFound() {
        Long orderId = 999L;
        OrderItemRequest request = new OrderItemRequest(1L, 1);

        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () ->
                orderItemService.addItemToOrder(orderId, request));

        verify(orderRepository, times(1)).findById(orderId);
        verifyNoInteractions(dishService);
        verifyNoInteractions(orderItemRepository);
    }

    @Test
    void addItemToOrder_ShouldThrowException_WhenOrderDelivered() {
        Long orderId = 1L;
        OrderItemRequest request = new OrderItemRequest(1L, 1);

        OrderEntity order = new OrderEntity();
        order.setId(orderId);
        order.setStatus(OrderStatus.DELIVERED);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        assertThrows(IllegalStateException.class, () ->
                orderItemService.addItemToOrder(orderId, request)
        );

        verify(orderRepository, times(1)).findById(orderId);
        verifyNoInteractions(dishService);
        verifyNoInteractions(orderItemRepository);
    }

    @Test
    void addItemToOrder_ShouldThrowException_WhenOrderCanceled() {
        Long orderId = 1L;
        OrderItemRequest request = new OrderItemRequest(1L, 1);

        OrderEntity order = new OrderEntity();
        order.setId(orderId);
        order.setStatus(OrderStatus.CANCELED);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        assertThrows(IllegalStateException.class, () ->
                orderItemService.addItemToOrder(orderId, request)
        );
    }

    @Test
    void getOrderItems_ShouldGetOrderItems() {
        Long orderId = 1L;

        OrderEntity order = new OrderEntity();
        order.setId(orderId);
        order.setStatus(OrderStatus.PENDING);
        order.setTotalPrice(BigDecimal.valueOf(300));

        OrderItemEntity item1 = new OrderItemEntity();
        item1.setId(1L);
        item1.setDishId(100L);
        item1.setDishName("Пицца");
        item1.setQuantity(2);
        item1.setPrice(BigDecimal.valueOf(150));
        item1.setOrder(order);

        OrderItemEntity item2 = new OrderItemEntity();
        item2.setId(2L);
        item2.setDishId(101L);
        item2.setDishName("Паста");
        item2.setQuantity(1);
        item2.setPrice(BigDecimal.valueOf(200));
        item2.setOrder(order);

        order.setItems(List.of(item1, item2));

        when(orderRepository.findByIdWithItems(orderId)).thenReturn(Optional.of(order));

        OrderItemResponse response1 = new OrderItemResponse(
                100L,
                "Пицца",
                2,
                BigDecimal.valueOf(150)
        );
        OrderItemResponse response2 = new OrderItemResponse(
                101L,
                "Паста",
                1,
                BigDecimal.valueOf(200)
        );

        when(orderItemMapper.toResponse(item1)).thenReturn(response1);
        when(orderItemMapper.toResponse(item2)).thenReturn(response2);

        List<OrderItemResponse> result = orderItemService.getOrderItems(orderId);

        assertEquals(2, result.size());
        assertEquals("Пицца", result.get(0).dishName());
        assertEquals(2, result.get(0).quantity());
        assertEquals("Паста", result.get(1).dishName());

        verify(orderRepository, times(1)).findByIdWithItems(orderId);
        verify(orderItemMapper, times(2)).toResponse(any(OrderItemEntity.class));
    }

    @Test
    void getOrderItems_ShouldThrowException_WhenOrderNotFound() {
        Long orderId = 999L;

        when(orderRepository.findByIdWithItems(orderId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () ->
                orderItemService.getOrderItems(orderId));

        verify(orderRepository, times(1)).findByIdWithItems(orderId);
        verifyNoInteractions(orderItemMapper);
    }

    @Test
    void removeItemFromOrder_ShouldRemoveItemAndUpdateTotalPrice() {
        Long orderId = 1L;
        Long itemId = 1L;

        OrderEntity order = new OrderEntity();
        order.setId(orderId);
        order.setStatus(OrderStatus.PENDING);
        order.setTotalPrice(BigDecimal.valueOf(500));
        order.setItems(new ArrayList<>());

        OrderItemEntity item = new OrderItemEntity();
        item.setId(itemId);
        item.setDishId(100L);
        item.setDishName("Пицца");
        item.setQuantity(2);
        item.setPrice(BigDecimal.valueOf(150));
        item.setOrder(order);

        order.getItems().add(item);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(orderItemRepository.findById(itemId)).thenReturn(Optional.of(item));

        orderItemService.removeItemFromOrder(orderId, itemId);

        assertEquals(0, BigDecimal.valueOf(200).compareTo(order.getTotalPrice()));

        verify(orderRepository, times(1)).findById(orderId);
        verify(orderItemRepository, times(1)).findById(itemId);
        verify(orderItemRepository, times(1)).delete(item);
        verify(orderRepository, times(1)).save(order);
    }

    @Test
    void removeItemFromOrder_ShouldThrowException_WhenOrderNotFound() {
        Long orderId = 999L;
        Long itemId = 1L;

        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () ->
                orderItemService.removeItemFromOrder(orderId, itemId)
        );

        verify(orderRepository, times(1)).findById(orderId);
        verifyNoInteractions(orderItemRepository);
    }

    @Test
    void removeItemFromOrder_ShouldThrowException_WhenItemNotFound() {
        Long orderId = 1L;
        Long itemId = 999L;

        OrderEntity order = new OrderEntity();
        order.setId(orderId);
        order.setStatus(OrderStatus.PENDING);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(orderItemRepository.findById(itemId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () ->
                orderItemService.removeItemFromOrder(orderId, itemId)
        );

        verify(orderRepository, times(1)).findById(orderId);
        verify(orderItemRepository, times(1)).findById(itemId);
        verify(orderItemRepository, never()).delete(any());
        verify(orderRepository, never()).save(any());
    }

    @Test
    void removeItemFromOrder_ShouldThrowException_WhenOrderDelivered() {
        Long orderId = 1L;
        Long itemId = 1L;

        OrderEntity order = new OrderEntity();
        order.setId(orderId);
        order.setStatus(OrderStatus.DELIVERED);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        assertThrows(IllegalStateException.class, () ->
                orderItemService.removeItemFromOrder(orderId, itemId)
        );

        verify(orderRepository, times(1)).findById(orderId);
        verifyNoInteractions(orderItemRepository);
    }

    @Test
    void removeItemFromOrder_ShouldThrowException_WhenOrderCanceled() {
        Long orderId = 1L;
        Long itemId = 1L;

        OrderEntity order = new OrderEntity();
        order.setId(orderId);
        order.setStatus(OrderStatus.CANCELED);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        assertThrows(IllegalStateException.class, () ->
                orderItemService.removeItemFromOrder(orderId, itemId)
        );

        verify(orderRepository, times(1)).findById(orderId);
        verifyNoInteractions(orderItemRepository);
    }

    @Test
    void removeItemFromOrder_ShouldThrowException_WhenItemDoesNotBelongToOrder() {
        Long orderId = 1L;
        Long itemId = 2L;

        OrderEntity order = new OrderEntity();
        order.setId(orderId);
        order.setStatus(OrderStatus.PENDING);

        OrderEntity anotherOrder = new OrderEntity();
        anotherOrder.setId(999L);

        OrderItemEntity item = new OrderItemEntity();
        item.setId(itemId);
        item.setOrder(anotherOrder);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(orderItemRepository.findById(itemId)).thenReturn(Optional.of(item));

        assertThrows(IllegalArgumentException.class, () ->
                orderItemService.removeItemFromOrder(orderId, itemId)
        );

        verify(orderRepository, times(1)).findById(orderId);
        verify(orderItemRepository, times(1)).findById(itemId);
        verify(orderItemRepository, never()).delete(any());
        verify(orderRepository, never()).save(any());
    }
}
