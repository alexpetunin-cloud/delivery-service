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
import com.petunincloud.delivery.service.users.UserEntity;
import com.petunincloud.delivery.service.users.UserRepository;
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
    private final UserRepository userRepository;
    private final RestaurantRepository restaurantRepository;

    public OrderService(
            OrderRepository orderRepository,
            OrderMapper orderMapper,
            DishService dishService,
            UserRepository userRepository,
            RestaurantRepository restaurantRepository
    ) {
        this.orderRepository = orderRepository;
        this.orderMapper = orderMapper;
        this.dishService = dishService;
        this.userRepository = userRepository;
        this.restaurantRepository = restaurantRepository;
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
        UserEntity user = userRepository.findById(request.userId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        RestaurantEntity restaurant = restaurantRepository.findById(request.restaurantId())
                .orElseThrow(() -> new IllegalArgumentException("Restaurant not found"));

        OrderEntity entity = orderMapper.toEntity(request, user);
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
        OrderEntity order = getOrderById(orderId);

        if (order.getStatus() == OrderStatus.DELIVERED || order.getStatus() == OrderStatus.CANCELED) {
            throw new IllegalStateException("Cannot cancel DELIVERED or already CANCELED order");
        }

        order.setStatus(OrderStatus.CANCELED);
        return orderMapper.toResponse(orderRepository.save(order));
    }

    public OrderEntity getOrderById(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderId));
    }
}