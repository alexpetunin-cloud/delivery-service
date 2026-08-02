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
        log.info("Called getOrderItems for orderId = {}", orderId);

        return ResponseEntity.ok()
                .body(orderItemService.getOrderItems(orderId));
    }

    @PostMapping
    public ResponseEntity<OrderItemResponse> addItemToOrder(
            @PathVariable("orderId") Long orderId,
            @RequestBody @Valid OrderItemRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(orderItemService.addItemToOrder(orderId, request));
    }

    @DeleteMapping("{itemId}")
    public ResponseEntity<Void> removeItemFromOrder(
            @PathVariable("orderId") Long orderId,
            @PathVariable("itemId") Long itemId
    ) {
        orderItemService.removeItemFromOrder(orderId, itemId);
        return ResponseEntity.noContent()
                .build();
    }
}
