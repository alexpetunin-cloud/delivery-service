package com.petunincloud.delivery.service.restaurants.restaurant;

import com.petunincloud.delivery.service.common.BaseService;
import com.petunincloud.delivery.service.orders.order.OrderMapper;
import com.petunincloud.delivery.service.orders.order.OrderRepository;
import com.petunincloud.delivery.service.orders.order.OrderService;
import com.petunincloud.delivery.service.orders.order.OrderStatus;
import com.petunincloud.delivery.service.orders.order.dto.OrderResponse;
import com.petunincloud.delivery.service.orders.order.OrderEntity;
import com.petunincloud.delivery.service.restaurants.dish.DishEntity;
import com.petunincloud.delivery.service.restaurants.dish.DishMapper;
import com.petunincloud.delivery.service.restaurants.dish.DishRepository;
import com.petunincloud.delivery.service.restaurants.dish.dto.DishRequest;
import com.petunincloud.delivery.service.restaurants.dish.dto.DishResponse;
import com.petunincloud.delivery.service.restaurants.restaurant.dto.RestaurantRequest;
import com.petunincloud.delivery.service.restaurants.restaurant.dto.RestaurantResponse;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RestaurantService extends BaseService<RestaurantEntity, RestaurantResponse, RestaurantSearchFilter> {
    private final RestaurantRepository restaurantRepository;
    private final RestaurantMapper restaurantMapper;
    private final DishRepository dishRepository;
    private final DishMapper dishMapper;
    private final OrderService orderService;
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final static Logger log = LoggerFactory.getLogger(RestaurantService.class);

    public RestaurantService(
            RestaurantRepository restaurantRepository,
            RestaurantMapper restaurantMapper,
            DishRepository dishRepository,
            DishMapper dishMapper,
            OrderService orderService,
            OrderRepository orderRepository,
            OrderMapper orderMapper
    ) {
        this.restaurantRepository = restaurantRepository;
        this.restaurantMapper = restaurantMapper;
        this.dishRepository = dishRepository;
        this.dishMapper = dishMapper;
        this.orderService = orderService;
        this.orderRepository = orderRepository;
        this.orderMapper = orderMapper;
    }

    @Override
    protected List<RestaurantEntity> findWithFilter(RestaurantSearchFilter filter, Pageable pageable) {
        return restaurantRepository.searchAllByFilter(
                filter.name(),
                pageable
        );
    }

    @Override
    protected RestaurantMapper getMapper() {
        return restaurantMapper;
    }

    @Transactional
    public RestaurantResponse createRestaurant(RestaurantRequest request) {
        RestaurantEntity entity = restaurantMapper.toEntity(request);
        RestaurantEntity saved = restaurantRepository.save(entity);
        return restaurantMapper.toResponse(saved);
    }

    @Transactional
    public DishResponse addDishToRestaurant(Long restaurantId, DishRequest request) {
        RestaurantEntity restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new IllegalArgumentException("Restaurant not found: " + restaurantId));

        DishEntity dish = new DishEntity();
        dish.setName(request.name());
        dish.setPrice(request.price());
        dish.setRestaurant(restaurant);

        DishEntity savedDish = dishRepository.save(dish);

        restaurant.getMenu().add(savedDish);
        restaurantRepository.save(restaurant);

        return dishMapper.toResponse(savedDish);
    }

    @Transactional
    public OrderResponse startCooking(Long orderId) {
        OrderEntity order = orderService.getOrderById(orderId);

        if (order.getStatus() != OrderStatus.CONFIRMED) {
            throw new IllegalStateException("Only CONFIRMED orders can start cooking");
        }

        order.setStatus(OrderStatus.COOKING);
        OrderEntity saved = orderRepository.save(order);

        log.info("Order {} started cooking", orderId);

        return orderMapper.toResponse(saved);
    }

    @Transactional
    public OrderResponse markAsReady(Long orderId) {
        OrderEntity order = orderService.getOrderById(orderId);

        if (order.getStatus() != OrderStatus.COOKING) {
            throw new IllegalStateException("Only COOKING orders can be marked as ready");
        }

        order.setStatus(OrderStatus.READY);
        OrderEntity saved = orderRepository.save(order);

        log.info("Order {} is ready for delivery", orderId);

        return orderMapper.toResponse(saved);
    }
}
