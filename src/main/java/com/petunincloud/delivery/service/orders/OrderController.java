package com.petunincloud.delivery.service.orders;

import com.petunincloud.delivery.service.common.BaseController;
import com.petunincloud.delivery.service.orders.dto.OrderRequest;
import com.petunincloud.delivery.service.orders.dto.OrderResponse;
import com.petunincloud.delivery.service.orders.entity.OrderEntity;
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
                .body(service.create(request));
    }

    @PatchMapping("/{orderId}/confirm")
    public ResponseEntity<OrderResponse> confirmOrder (
            @PathVariable("orderId") Long orderId
    ) {
        return ResponseEntity.ok(
                service.confirmOrder(orderId)
        );
    }

    @PatchMapping("/{orderId}/cook")
    public ResponseEntity<OrderResponse> startCooking (
            @PathVariable("orderId") Long orderId
    ) {
        return ResponseEntity.ok(
                service.startCooking(orderId)
        );
    }

    @PatchMapping("/{orderId}/ready")
    public ResponseEntity<OrderResponse> markAsReady (
            @PathVariable("orderId") Long orderId
    ) {
        return ResponseEntity.ok(
                service.markAsReady(orderId)
        );
    }

    @PatchMapping("/{orderId}/deliver")
    public ResponseEntity<OrderResponse> startDelivery (
            @PathVariable("orderId") Long orderId
    ) {
        return ResponseEntity.ok(
                service.startDelivery(orderId)
        );
    }

    @PatchMapping("/{orderId}/complete")
    public ResponseEntity<OrderResponse> completeDelivery (
            @PathVariable("orderId") Long orderId
    ) {
        return ResponseEntity.ok(
                service.completeDelivery(orderId)
        );
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
