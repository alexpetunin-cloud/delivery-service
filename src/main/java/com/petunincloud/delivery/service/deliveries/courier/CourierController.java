package com.petunincloud.delivery.service.deliveries.courier;

import com.petunincloud.delivery.service.common.BaseController;
import com.petunincloud.delivery.service.deliveries.courier.dto.CourierRequest;
import com.petunincloud.delivery.service.deliveries.courier.dto.CourierResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/couriers")
public class CourierController extends BaseController<CourierService, CourierEntity, CourierResponse, CourierSearchFilter> {
    public CourierController(CourierService service) {
        super(service);
    }

    @PostMapping
    public ResponseEntity<CourierResponse> createCourier(
            @RequestBody @Valid CourierRequest request
    ) {
        CourierResponse response = service.createCourier(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(response);
    }
}
