package com.petunincloud.delivery.service.deliveries.courier;

import com.petunincloud.delivery.service.common.BaseController;
import com.petunincloud.delivery.service.deliveries.courier.dto.CourierRequest;
import com.petunincloud.delivery.service.deliveries.courier.dto.CourierResponse;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/couriers")
public class CourierController extends BaseController<CourierService, CourierEntity, CourierResponse, CourierSearchFilter> {

    private final static Logger log = LoggerFactory.getLogger(CourierController.class);

    public CourierController(CourierService service) {
        super(service);
    }

    @PostMapping
    public ResponseEntity<CourierResponse> createCourier(
            @RequestBody @Valid CourierRequest request
    ) {
        log.info("POST /api/couriers - name: {}, phone: {}",
                request.name(),
                request.phone());
        long startTime = System.currentTimeMillis();

        try {
            CourierResponse response = service.createCourier(request);

            long duration = System.currentTimeMillis() - startTime;
            log.info("POST /api/couriers completed: id={}, status={}, duration={}ms",
                    response.id(), HttpStatus.CREATED.value(), duration);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(response);

        } catch (IllegalArgumentException e) {
            log.warn("POST /api/couriers failed: {}", e.getMessage());
            throw e;

        } catch (Exception e) {
            log.error("POST /api/couriers unexpected error", e);
            throw e;
        }
    }

    @GetMapping("/available")
    public ResponseEntity<CourierResponse> findAvailableCourier() {
        log.info("GET /api/couriers/available");
        long startTime = System.currentTimeMillis();

        try {
            CourierResponse response = service.findAvailableCourier();

            long duration = System.currentTimeMillis() - startTime;
            log.info("GET /api/couriers/available completed: id={}, name={}, status={}, httpStatus={}, duration={}ms",
                    response.id(), response.name(), response.status(), HttpStatus.OK, duration);
            return ResponseEntity.ok(response);

        } catch (IllegalStateException e) {
            log.warn("GET /api/couriers/available failed: {}", e.getMessage());
            throw e;

        } catch (Exception e) {
            log.error("GET /api/couriers/available unexpected error", e);
            throw e;
        }
    }
}
