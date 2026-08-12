package com.petunincloud.delivery.service.orders.order;

import com.petunincloud.delivery.service.common.BaseService;
import com.petunincloud.delivery.service.orders.order.dto.OrderRequest;
import com.petunincloud.delivery.service.orders.orderItem.dto.OrderItemRequest;
import com.petunincloud.delivery.service.orders.order.dto.OrderResponse;
import com.petunincloud.delivery.service.orders.orderItem.OrderItemEntity;
import com.petunincloud.delivery.service.restaurants.dish.DishService;
import com.petunincloud.delivery.service.restaurants.dish.dto.DishResponse;
import com.petunincloud.delivery.service.restaurants.restaurant.RestaurantEntity;
import com.petunincloud.delivery.service.restaurants.restaurant.RestaurantRepository;
import com.petunincloud.delivery.service.security.SecurityUtils;
import com.petunincloud.delivery.service.users.UserEntity;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService extends BaseService<OrderEntity, OrderResponse, OrderSearchFilter> {

    private static final Logger log = LoggerFactory.getLogger(OrderService.class);
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final DishService dishService;
    private final RestaurantRepository restaurantRepository;
    private final SecurityUtils securityUtils;

    public OrderService(
            OrderRepository orderRepository,
            OrderMapper orderMapper,
            DishService dishService,
            RestaurantRepository restaurantRepository,
            SecurityUtils securityUtils
    ) {
        this.orderRepository = orderRepository;
        this.orderMapper = orderMapper;
        this.dishService = dishService;
        this.restaurantRepository = restaurantRepository;
        this.securityUtils = securityUtils;
    }

    @Override
    protected List<OrderEntity> findWithFilter(OrderSearchFilter filter, Pageable pageable) {
        log.debug("Searching orders with filter: {}, pageable: {}", filter, pageable);

        return orderRepository.searchAllByFilter(
                filter.userId(),
                filter.restaurantId(),
                pageable
        );
    }

    @Override
    protected OrderMapper getMapper() {
        return orderMapper;
    }

    @Transactional
    public OrderResponse createOrder(OrderRequest request) {
        log.info("Creating new order for user: {}, restaurant: {}, items count: {}",
                request.email(),
                request.restaurantId(),
                request.items().size());

        long startTime = System.currentTimeMillis();

        try {
            UserEntity currentUser = securityUtils.getCurrentUser();
            log.debug("Authenticated user: {}", currentUser.getEmail());

            RestaurantEntity restaurant = restaurantRepository.findById(request.restaurantId())
                    .orElseThrow(() -> {
                        log.warn("Restaurant not found: {}", request.restaurantId());
                        return new IllegalArgumentException("Restaurant not found");
                    });

            log.debug("Restaurant found: id={}, name={}", restaurant.getId(), restaurant.getName());

            OrderEntity entity = orderMapper.toEntity(currentUser);
            entity.setRestaurant(restaurant);
            entity.setDateTime(LocalDateTime.now().withNano(0));
            entity.setStatus(OrderStatus.PENDING);

            log.debug("Order entity created with status PENDING");

            List<OrderItemEntity> items = new ArrayList<>();
            BigDecimal totalPrice = BigDecimal.ZERO;

            for (OrderItemRequest itemReq : request.items()) {
                DishResponse dish = dishService.getDishById(itemReq.dishId());
                log.debug("Adding dish: id={}, name={}, quantity={}, price={}",
                        dish.id(), dish.name(), itemReq.quantity(), dish.price());

                OrderItemEntity item = new OrderItemEntity();
                item.setDishId(dish.id());
                item.setDishName(dish.name());
                item.setQuantity(itemReq.quantity());
                item.setPrice(dish.price());
                item.setOrder(entity);

                items.add(item);

                BigDecimal itemTotal = dish.price().multiply(BigDecimal.valueOf(itemReq.quantity()));
                totalPrice = totalPrice.add(itemTotal);

                log.trace("Item total: {}, running total: {}", itemTotal, totalPrice);
            }

            entity.setItems(items);
            entity.setTotalPrice(totalPrice);

            log.info("Order total price calculated: {} for {} items", totalPrice, items.size());

            OrderEntity saved = orderRepository.save(entity);
            OrderResponse response = orderMapper.toResponse(saved);

            long duration = System.currentTimeMillis() - startTime;
            log.info("Order created successfully: id={}, total={}, user={}, duration={}ms",
                    saved.getId(), saved.getTotalPrice(), saved.getUser().getEmail(), duration);

            return response;

        } catch (Exception e) {
            log.error("Failed to create order for user: {}, restaurant: {}. Error: {}",
                    request.email(), request.restaurantId(), e.getMessage(), e);
            throw e;
        }
    }

    @Transactional
    public OrderResponse cancelOrder(Long orderId) {
        log.info("Cancelling order: {}", orderId);

        long startTime = System.currentTimeMillis();

        try {
            OrderEntity order = getOrderByIdForUser(orderId);
            log.debug("Order found: id={}, status={}, total={}",
                    order.getId(), order.getStatus(), order.getTotalPrice());

            if (order.getStatus() == OrderStatus.DELIVERED || order.getStatus() == OrderStatus.CANCELED) {
                log.warn("Cannot cancel order: id={}, current status={}", orderId, order.getStatus());
                throw new IllegalStateException("Cannot cancel DELIVERED or already CANCELED order");
            }

            order.setStatus(OrderStatus.CANCELED);
            OrderEntity saved = orderRepository.save(order);
            OrderResponse response = orderMapper.toResponse(saved);

            long duration = System.currentTimeMillis() - startTime;
            log.info("Order cancelled successfully: id={}, previous status={}, duration={}ms",
                    saved.getId(), order.getStatus(), duration);

            return response;

        } catch (Exception e) {
            log.error("Unexpected error cancelling order: {}. Error: {}", orderId, e.getMessage());
            throw e;
        }
    }

    // Для пользователей (с проверкой владельца)
    public OrderEntity getOrderByIdForUser(Long orderId) {
        log.debug("Fetching order by id for user: {}", orderId);

        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> {
                    log.warn("Order not found: {}", orderId);
                    return new IllegalArgumentException("Order not found: " + orderId);
                });

        UserEntity currentUser = securityUtils.getCurrentUser();
        if (!order.getUser().getId().equals(currentUser.getId())) {
            log.warn("Access denied: user {} tried to access order {}", currentUser.getEmail(), orderId);
            throw new IllegalArgumentException("You can only access your own orders");
        }

        log.debug("Order fetched: id={}, status={}, user={}",
                order.getId(), order.getStatus(), order.getUser().getEmail());
        return order;
    }

    // Для системы (без проверки владельца)
    public OrderEntity getOrderById(Long orderId) {
        log.debug("Fetching order by id (system): {}", orderId);

        return orderRepository.findById(orderId)
                .orElseThrow(() -> {
                    log.warn("Order not found (system call): {}", orderId);
                    return new IllegalArgumentException("Order not found: " + orderId);
                });
    }

    public OrderResponse getOrderResponseById(Long orderId) {
        log.info("Getting order response by id: {}", orderId);
        OrderEntity entity = getOrderByIdForUser(orderId);
        return orderMapper.toResponse(entity);
    }
}