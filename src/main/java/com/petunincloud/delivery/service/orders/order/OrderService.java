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
    protected List<OrderEntity> findWithFilter(
            OrderSearchFilter filter,
            Pageable pageable
    ) {
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
        log.info("Create order with request: {}", request);
        long startTime = System.currentTimeMillis();

        try {
            UserEntity currentUser = securityUtils.getCurrentUser();

            RestaurantEntity restaurant = restaurantRepository.findById(request.restaurantId())
                    .orElseThrow(() -> {
                        log.warn("Restaurant not found: {}", request.restaurantId());
                        return new IllegalArgumentException("Restaurant not found");
                    });

            OrderEntity order = orderMapper.toEntity(currentUser, restaurant);

            List<OrderItemEntity> items = new ArrayList<>();
            BigDecimal totalPrice = BigDecimal.ZERO;

            for (OrderItemRequest itemReq : request.items()) {
                DishResponse dish = dishService.getDishById(itemReq.dishId());

                OrderItemEntity item = new OrderItemEntity();

                item.setDishId(dish.id());
                item.setDishName(dish.name());
                item.setQuantity(itemReq.quantity());
                item.setPrice(dish.price());
                item.setOrder(order);
                items.add(item);

                BigDecimal itemTotal = dish.price().multiply(BigDecimal.valueOf(itemReq.quantity()));
                totalPrice = totalPrice.add(itemTotal);
            }

            order.setItems(items);
            order.setTotalPrice(totalPrice);

            OrderEntity saved = orderRepository.save(order);

            long duration = System.currentTimeMillis() - startTime;
            log.info("Success create order with request: {}, duration={}ms", request, duration);

            return orderMapper.toResponse(saved);

        } catch (Exception e) {
            log.error("Failed to create order with request: {}. Error: {}", request, e.getMessage());
            throw e;
        }
    }

    @Transactional
    public OrderResponse cancelOrder(Long orderId) {
        log.info("Cancel order: {}", orderId);
        long startTime = System.currentTimeMillis();

        try {
            OrderEntity order = getOrderByIdForUser(orderId);

            if (order.getStatus() == OrderStatus.DELIVERED || order.getStatus() == OrderStatus.CANCELED) {
                log.warn("Cannot cancel order: {} (status: {})", orderId, order.getStatus());
                throw new IllegalStateException("Cannot cancel DELIVERED or already CANCELED order");
            }

            order.setStatus(OrderStatus.CANCELED);
            log.info("Set status of CANCELED for order: {}", orderId);

            OrderEntity saved = orderRepository.save(order);

            long duration = System.currentTimeMillis() - startTime;
            log.info("Success cancel order: {}, duration={}ms", orderId, duration);

            return orderMapper.toResponse(saved);

        } catch (Exception e) {
            log.error("Failed cancel order: {}. Error: {}", orderId, e.getMessage());
            throw e;
        }
    }

    // Для пользователей (с проверкой владельца)
    public OrderEntity getOrderByIdForUser(Long orderId) {
        log.info("Get order by id: {} for user", orderId);
        long startTime = System.currentTimeMillis();

        try {
            OrderEntity order = orderRepository.findById(orderId)
                    .orElseThrow(() -> {
                        log.warn("Order not found: {}", orderId);
                        return new IllegalArgumentException("Order not found");
                    });

            UserEntity currentUser = securityUtils.getCurrentUser();

            if (!order.getUser().getId().equals(currentUser.getId())) {
                log.warn("Access denied: user {} tried to access order {}", currentUser.getEmail(), orderId);
                throw new IllegalArgumentException("You can only access your own orders");
            }

            long duration = System.currentTimeMillis() - startTime;
            log.info("Success get order by id: {} for user, duration={}ms", orderId, duration);

            return order;

        } catch (Exception e) {
            log.error("Failed get order by id: {} for user. Error: {}", orderId, e.getMessage());
            throw e;
        }
    }

    // Для системы (без проверки владельца)
    public OrderEntity getOrderById(Long orderId) {
        log.info("Get order by id (system call): {}", orderId);
        long startTime = System.currentTimeMillis();

        try {
            OrderEntity order = orderRepository.findById(orderId)
                    .orElseThrow(() -> {
                        log.warn("Order not found (system call): {}", orderId);
                        return new IllegalArgumentException("Order not found");
                    });

            long duration = System.currentTimeMillis() - startTime;
            log.info("Success get order by id (system call): {}, duration={}ms", orderId, duration);

            return order;

        } catch (Exception e) {
            log.error("Failed get order by id (system call): {}. Error: {}", orderId, e.getMessage());
            throw e;
        }
    }

    public OrderResponse getOrderResponseById(Long orderId) {
        OrderEntity entity = getOrderByIdForUser(orderId);

        return orderMapper.toResponse(entity);
    }
}