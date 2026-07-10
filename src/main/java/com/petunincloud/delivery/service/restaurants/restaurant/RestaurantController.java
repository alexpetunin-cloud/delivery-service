package com.petunincloud.delivery.service.restaurants.restaurant;
import com.petunincloud.delivery.service.common.BaseController;
import com.petunincloud.delivery.service.orders.OrderService;
import com.petunincloud.delivery.service.orders.dto.OrderResponse;
import com.petunincloud.delivery.service.restaurants.dish.dto.DishRequest;
import com.petunincloud.delivery.service.restaurants.dish.dto.DishResponse;
import com.petunincloud.delivery.service.restaurants.restaurant.dto.RestaurantRequest;
import com.petunincloud.delivery.service.restaurants.restaurant.dto.RestaurantResponse;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/restaurants")
public class RestaurantController extends BaseController<RestaurantService, RestaurantEntity, RestaurantResponse, RestaurantSearchFilter> {
    private final static Logger log = LoggerFactory.getLogger(RestaurantController.class);

    public RestaurantController(
            RestaurantService service
    ) {
        super(service);
    }

    @PostMapping
    public ResponseEntity<RestaurantResponse> createRestaurant (
            @RequestBody @Valid RestaurantRequest request
    ) {
        log.info("Called createRestaurant with parameters = {}", request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.createRestaurant(request));
    }

    @PostMapping("/{restaurantId}/dishes")
    public ResponseEntity<DishResponse> addDishToRestaurant(
            @PathVariable Long restaurantId,
            @RequestBody @Valid DishRequest request
    ) {
        log.info("Called addDishToRestaurant for id = {} with parameters = {}", restaurantId, request);

        DishResponse response = service.addDishToRestaurant(restaurantId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(response);
    }

    @PatchMapping("/{orderId}/cook")
    public ResponseEntity<OrderResponse> startCooking(
            @PathVariable Long orderId
    ) {
        log.info("Called startCooking for id = {}", orderId);

        return ResponseEntity.ok()
                .body(service.startCooking(orderId));
    }

    @PatchMapping("/{orderId}/ready")
    public ResponseEntity<OrderResponse> markAsReady(
            @PathVariable Long orderId
    ) {
        log.info("Called markAsReady for id = {}", orderId);

        return ResponseEntity.ok()
                .body(service.markAsReady(orderId));
    }
}
