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
        log.debug("Searching restaurants with filter: {}, pageable: {}", filter, pageable);
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
        log.info("Create restaurant with request: {}", request);
        long startTime = System.currentTimeMillis();

        try {
            RestaurantEntity entity = restaurantMapper.toEntity(request);
            RestaurantEntity saved = restaurantRepository.save(entity);

            long duration = System.currentTimeMillis() - startTime;
            log.info("Success create restaurant with request: {}, duration={}ms", request, duration);

            return restaurantMapper.toResponse(saved);

        } catch (Exception e) {
            log.error("Failed create restaurant with request: {}. Error: {}", request, e.getMessage());
            throw e;
        }
    }

    @Transactional
    public DishResponse addDishToRestaurant(Long restaurantId, DishRequest request) {
        log.info("Add dish: {} to restaurant: {}", request, restaurantId);
        long startTime = System.currentTimeMillis();

        try {
            RestaurantEntity restaurant = restaurantRepository.findById(restaurantId)
                    .orElseThrow(() -> {
                        log.warn("Restaurant not found: {}", restaurantId);
                        return new IllegalArgumentException("Restaurant not found: " + restaurantId);
                    });

            DishEntity dish = new DishEntity();
            dish.setName(request.name());
            dish.setPrice(request.price());
            dish.setRestaurant(restaurant);

            DishEntity savedDish = dishRepository.save(dish);

            restaurant.getMenu().add(savedDish);
            restaurantRepository.save(restaurant);

            long duration = System.currentTimeMillis() - startTime;
            log.info("Success add dish: {} to restaurant: {}, duration={}ms",
                    request, restaurantId, duration);

            return dishMapper.toResponse(savedDish);

        } catch (Exception e) {
            log.error("Failed add dish: {} to restaurant: {}. Error: {}",
                    request, restaurantId, e.getMessage());
            throw e;
        }
    }

    @Transactional
    public OrderResponse startCooking(Long orderId) {
        log.info("Start cooking for order: {}", orderId);
        long startTime = System.currentTimeMillis();

        try {
            OrderEntity order = orderService.getOrderById(orderId);

            if (order.getStatus() != OrderStatus.CONFIRMED) {
                log.warn("Сan`t start cooking until the order is not CONFIRMED (status: {})", order.getStatus());
                throw new IllegalStateException("Only CONFIRMED orders can start cooking");
            }

            order.setStatus(OrderStatus.COOKING);
            log.info("Set status of COOKING for order: {}", orderId);

            OrderEntity saved = orderRepository.save(order);

            long duration = System.currentTimeMillis() - startTime;
            log.info("Success start cooking for order: {}, duration={}ms", orderId, duration);

            return orderMapper.toResponse(saved);

        } catch (Exception e) {
            log.error("Failed start cooking for order: {}. Error: {}",
                    orderId, e.getMessage());
            throw e;
        }
    }

    @Transactional
    public OrderResponse markAsReady(Long orderId) {
        log.info("Mark as ready for order: {}", orderId);
        long startTime = System.currentTimeMillis();

        try {
            OrderEntity order = orderService.getOrderById(orderId);

            if (order.getStatus() != OrderStatus.COOKING) {
                log.warn("Сan`t start cooking until the order is not COOKING (status: {})", order.getStatus());
                throw new IllegalStateException("Only COOKING orders can be marked as ready");
            }

            order.setStatus(OrderStatus.READY);
            log.info("Set status of READY for order: {}", orderId);

            OrderEntity saved = orderRepository.save(order);

            long duration = System.currentTimeMillis() - startTime;
            log.info("Success mark as ready for order: {}, duration={}ms", orderId, duration);

            return orderMapper.toResponse(saved);

        } catch (Exception e) {
            log.error("Failed mark as ready for order: {}. Error: {}",
                    orderId, e.getMessage());
            throw e;
        }
    }
}
