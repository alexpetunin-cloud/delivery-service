package com.petunincloud.delivery.service.deliveries.delivery;

import com.petunincloud.delivery.service.common.BaseController;
import com.petunincloud.delivery.service.deliveries.delivery.dto.DeliveryResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/deliveries")
public class DeliveryController extends BaseController<DeliveryService, DeliveryEntity, DeliveryResponse, DeliverySearchFilter> {
    private final static Logger log = LoggerFactory.getLogger(DeliveryController.class);

    public DeliveryController(DeliveryService service) {
        super(service);
    }

    @PostMapping("/assign/{orderId}")
    public ResponseEntity<DeliveryResponse> assignCourier(
            @PathVariable("orderId") Long orderId
    ) {
        log.info("POST /api/deliveries/assign/{}", orderId);
        long startTime = System.currentTimeMillis();

        try {
            DeliveryResponse response = service.assignCourierToOrder(orderId);

            long duration = System.currentTimeMillis() - startTime;
            log.info("POST /api/deliveries/assign/{} completed: {}, duration={}ms",
                    orderId, HttpStatus.CREATED, duration);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(response);

        } catch (IllegalArgumentException | IllegalStateException e) {
            log.warn("POST /api/deliveries/assign/{} failed: {}",
                    orderId, e.getMessage());
            throw e;

        } catch (Exception e) {
            log.error("POST /api/deliveries/assign/{} unexpected error",
                    orderId, e);
            throw e;
        }

    }

    // Курьер доставил заказ
    @PatchMapping("/{deliveryId}/complete")
    public ResponseEntity<DeliveryResponse> completeDelivery(
            @PathVariable("deliveryId") Long deliveryId
    ) {
        log.info("PATCH /api/deliveries/{}/complete", deliveryId);
        long startTime = System.currentTimeMillis();

        try {
            DeliveryResponse response = service.completeDelivery(deliveryId);

            long duration = System.currentTimeMillis() - startTime;
            log.info("PATCH /api/deliveries/{}/complete completed: {}, duration={}ms",
                    deliveryId, HttpStatus.OK, duration);
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException | IllegalStateException e) {
            log.warn("PATCH /api/deliveries/{}/complete failed: {}",
                    deliveryId, e.getMessage());
            throw e;

        } catch (Exception e) {
            log.error("PATCH /api/deliveries/{}/complete unexpected error",
                    deliveryId, e);
            throw e;
        }
    }

    @GetMapping("/{deliveryId}")
    public ResponseEntity<DeliveryResponse> getDeliveryById(
            @PathVariable("deliveryId") Long deliveryId
    ) {
        log.info("GET /api/deliveries/{}", deliveryId);
        long startTime = System.currentTimeMillis();

        try {
            DeliveryResponse response = service.getDeliveryById(deliveryId);

            long duration = System.currentTimeMillis() - startTime;
            log.info("GET /api/deliveries/{} completed: {}, duration={}",
                    deliveryId, HttpStatus.OK, duration);
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            log.warn("GET /api/deliveries/{} failed: {}",
                    deliveryId, e.getMessage());
            throw e;

        } catch (Exception e) {
            log.error("GET /api/deliveries/{} unexpected error",
                    deliveryId, e);
            throw e;
        }
    }
}
