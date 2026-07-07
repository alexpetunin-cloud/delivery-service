package com.petunincloud.delivery.service.restaurants.dish;

import com.petunincloud.delivery.service.common.BaseController;
import com.petunincloud.delivery.service.restaurants.dish.dto.DishResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dishes")
public class DishController extends BaseController<DishService, DishEntity, DishResponse, DishSearchFilter> {
    private final static Logger log = LoggerFactory.getLogger(DishController.class);

    public DishController(DishService service) {
        super(service);
    }
}
