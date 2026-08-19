package com.petunincloud.delivery.service.payments;

import com.petunincloud.delivery.service.common.BaseController;
import com.petunincloud.delivery.service.payments.dto.PaymentRequest;
import com.petunincloud.delivery.service.payments.dto.PaymentResponse;
import com.petunincloud.delivery.service.security.SecurityUtils;
import com.petunincloud.delivery.service.users.UserEntity;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@SecurityRequirement(name = "bearerAuth")
public class PaymentController extends BaseController<PaymentService, PaymentEntity, PaymentResponse, PaymentSearchFilter> {

    private final SecurityUtils securityUtils;
    private final static Logger log = LoggerFactory.getLogger(PaymentController.class);

    public PaymentController(
            PaymentService paymentService,
            SecurityUtils securityUtils
    ) {
        super(paymentService);
        this.securityUtils = securityUtils;
    }

    @PostMapping("/initiate")
    public ResponseEntity<PaymentResponse> initiatePayment(
            @RequestBody @Valid PaymentRequest request
    ) {
        log.info("POST /api/payments/initiate with request: {}", request);
        long startTime = System.currentTimeMillis();

        try {
            UserEntity currentUser = securityUtils.getCurrentUser();

            PaymentResponse response = service.initiatePayment(request, currentUser);

            long duration = System.currentTimeMillis() - startTime;
            log.info("POST /api/payments/initiate with request: {} completed: {}, duration={}ms",
                    request, HttpStatus.CREATED, duration);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(response);

        } catch (IllegalStateException | IllegalArgumentException e) {
            log.warn("POST /api/payments/initiate with request: {} failed: {}", request, e.getMessage());
            throw e;

        } catch (Exception e) {
            log.error("POST /api/payments/initiate with request: {} unexpected error", request, e);
            throw e;
        }
    }

    @PostMapping("/{paymentId}/process")
    public ResponseEntity<PaymentResponse> processPayment(
            @PathVariable("paymentId") Long paymentId
    ) {
        log.info("POST /api/payments/{}/process", paymentId);
        long startTime = System.currentTimeMillis();

        try {
            PaymentResponse response = service.processPayment(paymentId);

            long duration = System.currentTimeMillis() - startTime;
            log.info("POST /api/payments/{}/process completed: {}, duration={}ms",
                    paymentId, HttpStatus.OK, duration);

            return ResponseEntity.ok(response);

        } catch (IllegalStateException | IllegalArgumentException e) {
            log.warn("POST /api/payments/{}/process failed: {}", paymentId, e.getMessage());
            throw e;

        } catch (Exception e) {
            log.error("POST /api/payments/{}/process unexpected error", paymentId, e);
            throw e;
        }
    }
}
