package com.petunincloud.delivery.service.restaurants.restaurant;
import com.petunincloud.delivery.service.common.BaseController;
import com.petunincloud.delivery.service.restaurants.restaurant.dto.RestaurantResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/restaurants")
public class RestaurantController extends BaseController<RestaurantService, RestaurantEntity, RestaurantResponse, RestaurantSearchFilter> {
    private final static Logger log = LoggerFactory.getLogger(RestaurantController.class);

    public RestaurantController(RestaurantService service) {
        super(service);
    }
}
