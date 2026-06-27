package com.petunincloud.delivery.service.orders;

import com.petunincloud.delivery.service.common.BaseController;
import com.petunincloud.delivery.service.orders.dto.CreateOrderRequest;
import com.petunincloud.delivery.service.orders.dto.OrderResponse;
import com.petunincloud.delivery.service.orders.entity.OrderEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
public class OrderController extends BaseController<OrderService, OrderEntity, OrderResponse, OrderSearchFilter> {
    private final static Logger log = LoggerFactory.getLogger(OrderController.class);

    public OrderController(OrderService service) {
        super(service);
    }

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder (
            CreateOrderRequest request
    ) {
        log.info("Called createOrder with parametres = {}", request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.create(request));
    }

}
