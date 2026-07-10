package com.petunincloud.delivery.service.deliveries.delivery;

import com.petunincloud.delivery.service.common.BaseController;
import com.petunincloud.delivery.service.deliveries.delivery.dto.DeliveryResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/deliveries")
public class DeliveryController extends BaseController<DeliveryService, DeliveryEntity, DeliveryResponse, DeliverySearchFilter> {
    public DeliveryController(DeliveryService service) {
        super(service);
    }

    @PatchMapping("/assign/{orderId}")
    public ResponseEntity<DeliveryResponse> assignCourier(
            @PathVariable Long orderId
    ) {
        return ResponseEntity.ok(service.assignCourierToOrder(orderId));
    }

    // Курьер доставил заказ
    @PatchMapping("/{deliveryId}/complete")
    public ResponseEntity<DeliveryResponse> completeDelivery(
            @PathVariable Long deliveryId
    ) {
        return ResponseEntity.ok(service.completeDelivery(deliveryId));
    }
}
