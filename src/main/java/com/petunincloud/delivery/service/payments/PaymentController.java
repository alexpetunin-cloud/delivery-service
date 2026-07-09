package com.petunincloud.delivery.service.payments;

import com.petunincloud.delivery.service.common.BaseController;
import com.petunincloud.delivery.service.payments.dto.PaymentRequest;
import com.petunincloud.delivery.service.payments.dto.PaymentResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
public class PaymentController extends BaseController<PaymentService, PaymentEntity, PaymentResponse, PaymentSearchFilter> {

    public PaymentController(PaymentService service) {
        super(service);
    }

    @PostMapping("/initiate")
    public ResponseEntity<PaymentResponse> initiatePayment(
            @RequestBody @Valid PaymentRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.initiatePayment(request));
    }

    @PostMapping("/{paymentId}/process")
    public ResponseEntity<PaymentResponse> processPayment(
            @PathVariable Long paymentId
    ) {
        return ResponseEntity.ok()
                .body(service.processPayment(paymentId));
    }
}
