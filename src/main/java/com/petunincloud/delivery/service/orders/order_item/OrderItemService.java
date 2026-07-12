package com.petunincloud.delivery.service.orders.order_item;

import com.petunincloud.delivery.service.orders.order.OrderEntity;
import com.petunincloud.delivery.service.orders.order.OrderRepository;
import com.petunincloud.delivery.service.orders.order.OrderStatus;
import com.petunincloud.delivery.service.orders.order_item.dto.OrderItemRequest;
import com.petunincloud.delivery.service.orders.order_item.dto.OrderItemResponse;
import com.petunincloud.delivery.service.restaurants.dish.DishService;
import com.petunincloud.delivery.service.restaurants.dish.dto.DishResponse;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderItemService {

    private final OrderItemMapper orderItemMapper;
    private final OrderItemRepository orderItemRepository;
    private final OrderRepository orderRepository;
    private final DishService dishService;

    public OrderItemService(
            OrderItemMapper orderItemMapper,
            OrderRepository orderRepository,
            DishService dishService,
            OrderItemRepository orderItemRepository
    ) {
        this.orderItemMapper = orderItemMapper;
        this.orderRepository = orderRepository;
        this.dishService = dishService;
        this.orderItemRepository = orderItemRepository;
    }

    public List<OrderItemResponse> getOrderItems(Long orderId) {
        OrderEntity order = orderRepository.findByIdWithItems(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        return order.getItems().stream()
                .map(orderItemMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public OrderItemResponse addItemToOrder(Long orderId, @Valid OrderItemRequest request) {
        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        if (order.getStatus() == OrderStatus.DELIVERED || order.getStatus() == OrderStatus.CANCELED) {
            throw new IllegalStateException("Cannot modify order in status: " + order.getStatus());
        }

        DishResponse dish = dishService.getDishById(request.dishId());

        OrderItemEntity entity = orderItemMapper.toEntity(request, order);

        entity.setDishName(dish.name());
        entity.setPrice(dish.price());

        OrderItemEntity saved = orderItemRepository.save(entity);

        order.getItems().add(saved);

        BigDecimal newTotal = order.getTotalPrice()
                .add(dish.price()
                        .multiply(BigDecimal.valueOf(request.quantity())));

        order.setTotalPrice(newTotal);
        orderRepository.save(order);

        return orderItemMapper.toResponse(saved);
    }

    public void removeItemFromOrder(Long orderId, Long itemId) {
        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        if (order.getStatus() == OrderStatus.DELIVERED || order.getStatus() == OrderStatus.CANCELED) {
            throw new IllegalStateException("Cannot modify order in status: " + order.getStatus());
        }

        OrderItemEntity item = orderItemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Item not found"));

        if (!item.getOrder().getId().equals(orderId)) {
            throw new IllegalArgumentException("Item does not belong to this order");
        }

        BigDecimal itemTotal = item.getPrice()
                .multiply(BigDecimal.valueOf(item.getQuantity()));

        order.setTotalPrice(
                order.getTotalPrice()
                        .subtract(itemTotal));

        orderItemRepository.delete(item);
        orderRepository.save(order);
    }
}
