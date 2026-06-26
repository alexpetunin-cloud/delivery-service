package com.petunincloud.delivery.service.orders;

import com.petunincloud.delivery.service.common.BaseController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/orders")
public class OrderController extends BaseController<OrderService, OrderEntity, OrderDto, OrderSearchFilter> {

    public OrderController(OrderService service) {
        super(service);
    }

}
