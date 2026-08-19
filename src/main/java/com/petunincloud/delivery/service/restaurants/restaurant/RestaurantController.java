package com.petunincloud.delivery.service.restaurants.restaurant;

import com.petunincloud.delivery.service.common.BaseController;
import com.petunincloud.delivery.service.orders.order.dto.OrderResponse;
import com.petunincloud.delivery.service.restaurants.dish.dto.DishRequest;
import com.petunincloud.delivery.service.restaurants.dish.dto.DishResponse;
import com.petunincloud.delivery.service.restaurants.restaurant.dto.RestaurantRequest;
import com.petunincloud.delivery.service.restaurants.restaurant.dto.RestaurantResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/restaurants")
@SecurityRequirement(name = "bearerAuth")
public class RestaurantController extends BaseController<RestaurantService, RestaurantEntity, RestaurantResponse, RestaurantSearchFilter> {

    private final static Logger log = LoggerFactory.getLogger(RestaurantController.class);

    public RestaurantController(RestaurantService service) {
        super(service);
    }

    @PostMapping
    public ResponseEntity<RestaurantResponse> createRestaurant (
            @RequestBody @Valid RestaurantRequest request
    ) {
        log.info("POST /api/restaurants with request: {}", request);
        long startTime = System.currentTimeMillis();

        try {
            RestaurantResponse response = service.createRestaurant(request);

            long duration = System.currentTimeMillis() - startTime;
            log.info("POST /api/restaurants with request: {} completed: {}, duration={}ms",
                    request, HttpStatus.CREATED, duration);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(response);

        } catch (Exception e) {
            log.error("POST /api/restaurants with request: {} unexpected error", request, e);
            throw e;
        }
    }

    @PostMapping("/{restaurantId}/dishes")
    public ResponseEntity<DishResponse> addDishToRestaurant(
            @PathVariable("restaurantId") Long restaurantId,
            @RequestBody @Valid DishRequest request
    ) {
        log.info("POST /api/restaurants/{}/dishes with request: {}", restaurantId, request);
        long startTime = System.currentTimeMillis();

        try {
            DishResponse response = service.addDishToRestaurant(restaurantId, request);

            long duration = System.currentTimeMillis() - startTime;
            log.info("POST /api/restaurants/{}/dishes with request: {} completed: {}, duration={}ms",
                    restaurantId, request, HttpStatus.CREATED, duration);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(response);

        } catch (IllegalArgumentException e) {
            log.warn("POST /api/restaurants/{}/dishes with request: {} failed: {}",
                    restaurantId, request, e.getMessage());
            throw e;

        } catch (Exception e) {
            log.error("POST /api/restaurants/{}/dishes with request: {} unexpected error",
                    restaurantId, request, e);
            throw e;
        }
    }

    @PatchMapping("/orders/{orderId}/cook")
    public ResponseEntity<OrderResponse> startCooking(
            @PathVariable("orderId") Long orderId
    ) {
        log.info("PATCH /api/restaurants/orders/{}/cook", orderId);
        long startTime = System.currentTimeMillis();

        try {
            OrderResponse response = service.startCooking(orderId);

            long duration = System.currentTimeMillis() - startTime;
            log.info("PATCH /api/restaurants/orders/{}/cook completed: {}, duration={}ms",
                    orderId, HttpStatus.OK, duration);

            return ResponseEntity.ok(response);

        } catch (IllegalStateException e) {
            log.warn("PATCH /api/restaurants/orders/{}/cook failed: {}", orderId, e.getMessage());
            throw e;

        } catch (Exception e) {
            log.error("PATCH /api/restaurants/orders/{}/cook unexpected error", orderId, e);
            throw e;
        }
    }

    @PatchMapping("/orders/{orderId}/ready")
    public ResponseEntity<OrderResponse> markAsReady(
            @PathVariable("orderId") Long orderId
    ) {
        log.info("PATCH /api/restaurants/orders/{}/ready", orderId);
        long startTime = System.currentTimeMillis();

        try {
            OrderResponse response = service.markAsReady(orderId);

            long duration = System.currentTimeMillis() - startTime;
            log.info("PATCH /api/restaurants/orders/{}/ready completed: {}, duration={}ms",
                    orderId, HttpStatus.OK, duration);

            return ResponseEntity.ok(response);

        } catch (IllegalStateException e) {
            log.warn("PATCH /api/restaurants/orders/{}/ready failed: {}", orderId, e.getMessage());
            throw e;

        } catch (Exception e) {
            log.error("PATCH /api/restaurants/orders/{}/ready unexpected error", orderId, e);
            throw e;
        }
    }
}