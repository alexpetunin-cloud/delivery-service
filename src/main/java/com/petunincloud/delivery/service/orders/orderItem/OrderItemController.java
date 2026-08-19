package com.petunincloud.delivery.service.orders.orderItem;

import com.petunincloud.delivery.service.orders.orderItem.dto.OrderItemRequest;
import com.petunincloud.delivery.service.orders.orderItem.dto.OrderItemResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders/{orderId}/items")
@SecurityRequirement(name = "bearerAuth")
public class OrderItemController {

    private final static Logger log = LoggerFactory.getLogger(OrderItemController.class);
    private final OrderItemService orderItemService;

    public OrderItemController(OrderItemService orderItemService) {
        this.orderItemService = orderItemService;
    }

    @GetMapping
    public ResponseEntity<List<OrderItemResponse>> getOrderItems(
            @PathVariable("orderId") Long orderId
    ) {
        log.info("GET /api/orders/{}/items", orderId);
        long startTime = System.currentTimeMillis();

        try {
            List<OrderItemResponse> response = orderItemService.getOrderItems(orderId);

            long duration = System.currentTimeMillis() - startTime;
            log.info("GET /api/orders/{}/items completed: {}, duration={}ms", orderId, HttpStatus.OK, duration);

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            log.warn("GET /api/orders/{}/items failed: {}", orderId, e.getMessage());
            throw e;

        } catch (Exception e) {
            log.error("GET /api/orders/{}/items unexpected error", orderId, e);
            throw e;
        }
    }

    @PostMapping
    public ResponseEntity<OrderItemResponse> addItemToOrder(
            @PathVariable("orderId") Long orderId,
            @RequestBody @Valid OrderItemRequest request
    ) {
        log.info("POST /api/orders/{}/items with request: {}", orderId, request);
        long startTime = System.currentTimeMillis();

        try {
            OrderItemResponse response = orderItemService.addItemToOrder(orderId, request);

            long duration = System.currentTimeMillis() - startTime;
            log.info("POST /api/orders/{}/items with request: {} completed: {}, duration={}ms",
                    orderId, request, HttpStatus.CREATED, duration);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(response);

        } catch (IllegalArgumentException | IllegalStateException e) {
            log.warn("POST /api/orders/{}/items with request: {} failed: {}", orderId, request, e.getMessage());
            throw e;

        } catch (Exception e) {
            log.error("POST /api/orders/{}/items with request: {} unexpected error", orderId, request, e);
            throw e;
        }
    }

    @DeleteMapping("{itemId}")
    public ResponseEntity<Void> removeItemFromOrder(
            @PathVariable("orderId") Long orderId,
            @PathVariable("itemId") Long itemId
    ) {
        log.info("DELETE /api/orders/{}/items/{}", orderId, itemId);
        long startTime = System.currentTimeMillis();

        try {
            orderItemService.removeItemFromOrder(orderId, itemId);

            long duration = System.currentTimeMillis() - startTime;
            log.info("DELETE /api/orders/{}/items/{} completed: {}, duration={}ms",
                    orderId, itemId, HttpStatus.NO_CONTENT, duration);

            return ResponseEntity.noContent().build();

        } catch (IllegalArgumentException | IllegalStateException e) {
            log.warn("DELETE /api/orders/{}/items/{} failed: {}", orderId, itemId, e.getMessage());
            throw e;

        } catch (Exception e) {
            log.error("DELETE /api/orders/{}/items/{} unexpected error", orderId, itemId, e);
            throw e;
        }
    }
}
