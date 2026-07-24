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
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService extends BaseService<OrderEntity, OrderResponse, OrderSearchFilter> {

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
        UserEntity currentUser = securityUtils.getCurrentUser();

        RestaurantEntity restaurant = restaurantRepository.findById(request.restaurantId())
                .orElseThrow(() -> new IllegalArgumentException("Restaurant not found"));

        OrderEntity entity = orderMapper.toEntity(currentUser);
        entity.setRestaurant(restaurant);
        entity.setDateTime(LocalDateTime.now().withNano(0));
        entity.setStatus(OrderStatus.PENDING);

        List<OrderItemEntity> items = new ArrayList<>();
        BigDecimal totalPrice = BigDecimal.ZERO;

        for (OrderItemRequest itemReq : request.items()) {
            DishResponse dish = dishService.getDishById(itemReq.dishId());

            OrderItemEntity item = new OrderItemEntity();
            item.setDishId(dish.id());
            item.setDishName(dish.name());
            item.setQuantity(itemReq.quantity());
            item.setPrice(dish.price());
            item.setOrder(entity);

            items.add(item);

            totalPrice = totalPrice.add(
                    dish.price()
                    .multiply(BigDecimal.valueOf(itemReq.quantity()))
            );
        }

        entity.setItems(items);
        entity.setTotalPrice(totalPrice);

        OrderEntity saved = orderRepository.save(entity);
        return orderMapper.toResponse(saved);
    }

    @Transactional
    public OrderResponse cancelOrder(Long orderId) {
        OrderEntity order = getOrderByIdForUser(orderId);

        if (order.getStatus() == OrderStatus.DELIVERED || order.getStatus() == OrderStatus.CANCELED) {
            throw new IllegalStateException("Cannot cancel DELIVERED or already CANCELED order");
        }

        order.setStatus(OrderStatus.CANCELED);
        return orderMapper.toResponse(orderRepository.save(order));
    }

    // Для пользователей (с проверкой владельца)
    public OrderEntity getOrderByIdForUser(Long orderId) {
        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderId));

        UserEntity currentUser = securityUtils.getCurrentUser();
        if (!order.getUser().getId().equals(currentUser.getId())) {
            throw new IllegalArgumentException("You can only access your own orders");
        }
        return order;
    }

    // Для системы (без проверки владельца)
    public OrderEntity getOrderById(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderId));
    }

    public OrderResponse getOrderResponseById(Long orderId) {
        OrderEntity entity = getOrderByIdForUser(orderId);
        return orderMapper.toResponse(entity);
    }
}