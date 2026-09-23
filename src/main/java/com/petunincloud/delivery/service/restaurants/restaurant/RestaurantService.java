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
import com.petunincloud.delivery.service.restaurants.restaurant.dto.CreateRestaurantRequest;
import com.petunincloud.delivery.service.restaurants.restaurant.dto.RestaurantResponse;
import com.petunincloud.delivery.service.security.SecurityUtils;
import com.petunincloud.delivery.service.users.RoleEntity;
import com.petunincloud.delivery.service.users.RoleRepository;
import com.petunincloud.delivery.service.users.UserEntity;
import com.petunincloud.delivery.service.users.UserRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

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
    private final SecurityUtils securityUtils;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public RestaurantService(
            RestaurantRepository restaurantRepository,
            RestaurantMapper restaurantMapper,
            DishRepository dishRepository,
            DishMapper dishMapper,
            OrderService orderService,
            OrderRepository orderRepository,
            OrderMapper orderMapper,
            SecurityUtils securityUtils,
            UserRepository userRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.restaurantRepository = restaurantRepository;
        this.restaurantMapper = restaurantMapper;
        this.dishRepository = dishRepository;
        this.dishMapper = dishMapper;
        this.orderService = orderService;
        this.orderRepository = orderRepository;
        this.orderMapper = orderMapper;
        this.securityUtils = securityUtils;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    protected List<RestaurantEntity> findWithFilter(
            RestaurantSearchFilter filter,
            Pageable pageable
    ) {
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
    public RestaurantResponse createRestaurant(CreateRestaurantRequest request) {

        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new IllegalArgumentException(
                    "User with this email already exists"
            );
        }

        if (restaurantRepository.findByName(request.name()).isPresent()) {
            throw new IllegalArgumentException(
                    "Restaurant with this name already exists"
            );
        }

        RoleEntity restaurantRole = roleRepository.findByName("ROLE_RESTAURANT")
                .orElseThrow(() ->
                        new IllegalStateException("ROLE_RESTAURANT not found"));

        UserEntity user = new UserEntity();
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setPhone(request.phone());
        user.setName(request.name());
        user.setAddress(request.address());
        user.setRoles(Set.of(restaurantRole));

        UserEntity savedUser = userRepository.save(user);

        RestaurantEntity restaurant = new RestaurantEntity();
        restaurant.setUser(savedUser);
        restaurant.setName(request.name());
        restaurant.setAddress(request.address());

        RestaurantEntity savedRestaurant =
                restaurantRepository.save(restaurant);

        return restaurantMapper.toResponse(savedRestaurant);
    }

    @Transactional
    public DishResponse addDishToRestaurant(
            Long restaurantId,
            DishRequest request
    ) {
        RestaurantEntity restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Restaurant not found"));

        UserEntity currentUser = securityUtils.getCurrentUser();

        if (!restaurant.getUser().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException(
                    "You can only modify your own restaurant"
            );
        }

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
        log.info("Start cooking for order: {}", orderId);
        long startTime = System.currentTimeMillis();

        try {
            OrderEntity order = orderService.getOrderById(orderId);
            UserEntity user = securityUtils.getCurrentUser();

            if (!order.getRestaurant().getUser().getId().equals(user.getId())) {
                throw new AccessDeniedException("You can only manage orders of your own restaurant");
            }

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
            log.error("Failed start cooking for order: {}. Error: {}", orderId, e.getMessage());
            throw e;
        }
    }

    @Transactional
    public OrderResponse markAsReady(Long orderId) {
        log.info("Mark as ready for order: {}", orderId);
        long startTime = System.currentTimeMillis();

        try {
            OrderEntity order = orderService.getOrderById(orderId);
            UserEntity user = securityUtils.getCurrentUser();

            if (!order.getRestaurant().getUser().getId().equals(user.getId())) {
                throw new AccessDeniedException("You can only manage orders of your own restaurant");
            }

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
            log.error("Failed mark as ready for order: {}. Error: {}", orderId, e.getMessage());
            throw e;
        }
    }

    public RestaurantResponse getRestaurantById(Long restaurantId) {
        RestaurantEntity restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new IllegalArgumentException("Restaurant not found"));

        return restaurantMapper.toResponse(restaurant);
    }
}
