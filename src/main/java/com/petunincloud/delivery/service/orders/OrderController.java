package com.petunincloud.delivery.service.orders;

import com.petunincloud.delivery.service.common.BaseController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController extends BaseController<OrderService, OrderEntity, OrderDto, OrderSearchFilter> {

    private static final Logger log = LoggerFactory.getLogger(OrderController.class);

    public OrderController(OrderService service) {
        super(service);
    }

    @GetMapping // Получение всех заказов по фильтру
    public ResponseEntity<List<OrderDto>> getAllOrder(
            @RequestParam(name = "orderId", required = false) Long orderId,
            @RequestParam(name = "userId", required = false) Long userId,
            @RequestParam(name = "pageSize", required = false) Integer pageSize,
            @RequestParam(name = "pageNumber", required = false) Integer pageNumber
    ) {
        log.info("Called getAllOrder");

        var filter = new OrderSearchFilter(
                orderId,
                userId,
                pageSize,
                pageNumber
        );

        return ResponseEntity.status(HttpStatus.OK)
                .body(service.search(filter));
    }
}
