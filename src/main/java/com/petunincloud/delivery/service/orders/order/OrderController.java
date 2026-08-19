package com.petunincloud.delivery.service.orders.order;

import com.petunincloud.delivery.service.common.BaseController;
import com.petunincloud.delivery.service.orders.order.dto.OrderRequest;
import com.petunincloud.delivery.service.orders.order.dto.OrderResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@SecurityRequirement(name = "bearerAuth")
public class OrderController extends BaseController<OrderService, OrderEntity, OrderResponse, OrderSearchFilter> {

    private final static Logger log = LoggerFactory.getLogger(OrderController.class);

    public OrderController(OrderService service) {
        super(service);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrderById(
            @PathVariable("id") Long id
    ) {
        log.info("GET /api/orders/{}", id);
        long startTime = System.currentTimeMillis();

        try {
            OrderResponse order = service.getOrderResponseById(id);

            long duration = System.currentTimeMillis() - startTime;
            log.info("GET /api/orders/{} completed: {}, duration={}ms", id, HttpStatus.OK, duration);

            return ResponseEntity.ok(order);

        } catch (IllegalArgumentException | IllegalStateException e) {
            log.warn("GET /api/orders/{} failed: {}", id, e.getMessage());
            throw e;

        } catch (Exception e) {
            log.error("GET /api/orders/{} unexpected error", id, e);
            throw e;
        }
    }

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(
            @RequestBody OrderRequest request
    ) {
        log.info("POST /api/orders with request: {}", request);
        long startTime = System.currentTimeMillis();

        try {
            OrderResponse response = service.createOrder(request);

            long duration = System.currentTimeMillis() - startTime;
            log.info("POST /api/orders with request: {} completed: {}, duration={}ms",
                    request, HttpStatus.CREATED, duration);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(response);

        } catch (IllegalArgumentException | IllegalStateException e) {
            log.warn("POST /api/orders with request: {} failed: {}", request, e.getMessage());
            throw e;

        } catch (Exception e) {
            log.error("POST /api/orders with request: {} unexpected error", request, e);
            throw e;
        }
    }

    @PatchMapping("/{orderId}/cancel")
    public ResponseEntity<OrderResponse> cancelOrder(
            @PathVariable("orderId") Long orderId
    ) {
        log.info("PATCH /api/orders/{}/cancel", orderId);
        long startTime = System.currentTimeMillis();

        try {
            OrderResponse response = service.cancelOrder(orderId);

            long duration = System.currentTimeMillis() - startTime;
            log.info("PATCH /api/orders/{}/cancel completed: {}, duration={}ms",
                    orderId, HttpStatus.OK, duration);

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException | IllegalStateException e) {
            log.warn("PATCH /api/orders/{}/cancel failed: {}", orderId, e.getMessage());
            throw e;

        } catch (Exception e) {
            log.error("PATCH /api/orders/{}/cancel unexpected error", orderId, e);
            throw e;
        }
    }
}
