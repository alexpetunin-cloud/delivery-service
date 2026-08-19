package com.petunincloud.delivery.service.orders.orderItem;

import com.petunincloud.delivery.service.orders.order.OrderEntity;
import com.petunincloud.delivery.service.orders.order.OrderRepository;
import com.petunincloud.delivery.service.orders.order.OrderStatus;
import com.petunincloud.delivery.service.orders.orderItem.dto.OrderItemRequest;
import com.petunincloud.delivery.service.orders.orderItem.dto.OrderItemResponse;
import com.petunincloud.delivery.service.restaurants.dish.DishService;
import com.petunincloud.delivery.service.restaurants.dish.dto.DishResponse;
import com.petunincloud.delivery.service.security.SecurityUtils;
import com.petunincloud.delivery.service.users.UserEntity;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private final SecurityUtils securityUtils;
    private final static Logger log = LoggerFactory.getLogger(OrderItemService.class);

    public OrderItemService(
            OrderItemMapper orderItemMapper,
            OrderRepository orderRepository,
            DishService dishService,
            OrderItemRepository orderItemRepository,
            SecurityUtils securityUtils
    ) {
        this.orderItemMapper = orderItemMapper;
        this.orderRepository = orderRepository;
        this.dishService = dishService;
        this.orderItemRepository = orderItemRepository;
        this.securityUtils = securityUtils;
    }

    public List<OrderItemResponse> getOrderItems(Long orderId) {
        log.info("Get items of order: {}", orderId);
        long startTime = System.currentTimeMillis();

        try {
            OrderEntity order = orderRepository.findByIdWithItems(orderId)
                    .orElseThrow(() -> {
                        log.warn("Order not found: {}", orderId);
                        return new IllegalArgumentException("Order not found");
                    });

            long duration = System.currentTimeMillis() - startTime;
            log.info("Success get items for order: {}, duration={}ms", orderId, duration);

            return order.getItems().stream()
                    .map(orderItemMapper::toResponse)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            log.error("Failed to get items for order: {}. Error: {}", orderId, e.getMessage());
            throw e;
        }
    }

    @Transactional
    public OrderItemResponse addItemToOrder(
            Long orderId,
            @Valid OrderItemRequest request
    ) {
        log.info("Add item: {} to order: {}", request, orderId);
        long startTime = System.currentTimeMillis();

        try {
            OrderEntity order = orderRepository.findById(orderId)
                    .orElseThrow(() -> {
                        log.warn("Order not found: {}", orderId);
                        return new IllegalArgumentException("Order not found");
                    });

            if (order.getStatus() == OrderStatus.DELIVERED || order.getStatus() == OrderStatus.CANCELED) {
                log.warn("Cannot modify order: {} in status: {}", orderId, order.getStatus());
                throw new IllegalStateException("Cannot modify order in status: " + order.getStatus());
            }

            UserEntity currentUser = securityUtils.getCurrentUser();

            if (!order.getUser().getId().equals(currentUser.getId())) {
                log.warn("Access denied: user {} tried to access order {}", currentUser.getEmail(), orderId);
                throw new IllegalArgumentException("You can only modify your own orders");
            }

            DishResponse dish = dishService.getDishById(request.dishId());

            OrderItemEntity entity = orderItemMapper.toEntity(request, order, dish);

            OrderItemEntity saved = orderItemRepository.save(entity);

            order.getItems().add(saved);

            BigDecimal newTotal = order.getTotalPrice()
                    .add(dish.price()
                            .multiply(BigDecimal.valueOf(request.quantity())));
            order.setTotalPrice(newTotal);

            orderRepository.save(order);

            long duration = System.currentTimeMillis() - startTime;
            log.info("Success add item: {} to order: {}, duration={}ms", request, orderId, duration);

            return orderItemMapper.toResponse(saved);

        } catch (Exception e) {
            log.error("Failed add item: {} to order: {}. Error: {}", request, orderId, e.getMessage());
            throw e;
        }
    }

    @Transactional
    public void removeItemFromOrder(
            Long orderId,
            Long itemId
    ) {
        log.info("Remove item: {} from order: {}", itemId, orderId);
        long startTime = System.currentTimeMillis();

        try {
            OrderEntity order = orderRepository.findById(orderId)
                    .orElseThrow(() -> {
                        log.info("Order not found: {}", orderId);
                        return new IllegalArgumentException("Order not found");
                    });

            if (order.getStatus() == OrderStatus.DELIVERED || order.getStatus() == OrderStatus.CANCELED) {
                log.warn("Cannot modify order: {} in status: {}", orderId, order.getStatus());
                throw new IllegalStateException("Cannot modify order in status: " + order.getStatus());
            }

            UserEntity currentUser = securityUtils.getCurrentUser();

            if (!order.getUser().getId().equals(currentUser.getId())) {
                log.warn("Access denied: user {} tried to access order {}", currentUser.getEmail(), orderId);
                throw new IllegalArgumentException("You can only modify your own orders");
            }

            OrderItemEntity item = orderItemRepository.findById(itemId)
                    .orElseThrow(() -> {
                        log.warn("Item not found: {}", itemId);
                        return new IllegalArgumentException("Item not found");
                    });

            if (!item.getOrder().getId().equals(orderId)) {
                log.warn("Cannot remove the item: {}, doesn't belong to this order: {}", itemId, orderId);
                throw new IllegalArgumentException("Item does not belong to this order");
            }

            BigDecimal itemTotal = item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
            order.setTotalPrice(order.getTotalPrice().subtract(itemTotal));

            order.getItems().remove(item);

            orderRepository.save(order);

            long duration = System.currentTimeMillis() - startTime;
            log.info("Success remove item: {} from order: {}, duration={}ms", itemId, orderId, duration);

        } catch (Exception e) {
            log.error("Failed remove item: {} from order: {}. Error: {}", itemId, orderId, e.getMessage());
            throw e;
        }
    }
}
