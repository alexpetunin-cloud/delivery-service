package com.petunincloud.delivery.service.orders.order;

import com.petunincloud.delivery.service.common.BaseController;
import com.petunincloud.delivery.service.orders.order.dto.OrderRequest;
import com.petunincloud.delivery.service.orders.order.dto.OrderResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
public class OrderController extends BaseController<OrderService, OrderEntity, OrderResponse, OrderSearchFilter> {
    private final static Logger log = LoggerFactory.getLogger(OrderController.class);

    public OrderController(OrderService service) {
        super(service);
    }

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder (
            @RequestBody OrderRequest request
    ) {
        log.info("Called createOrder with parameters = {}", request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.createOrder(request));
    }

    @PatchMapping("/{orderId}/cancel")
    public ResponseEntity<OrderResponse> cancelOrder (
            @PathVariable("orderId") Long orderId
    ) {
        return ResponseEntity.ok(
                service.cancelOrder(orderId)
        );
    }
}
