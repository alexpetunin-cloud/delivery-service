package com.petunincloud.delivery.service.payments;

import com.petunincloud.delivery.service.common.BaseController;
import com.petunincloud.delivery.service.payments.dto.PaymentRequest;
import com.petunincloud.delivery.service.payments.dto.PaymentResponse;
import com.petunincloud.delivery.service.security.SecurityUtils;
import com.petunincloud.delivery.service.users.UserEntity;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
public class PaymentController extends BaseController<PaymentService, PaymentEntity, PaymentResponse, PaymentSearchFilter> {

    private final SecurityUtils securityUtils;

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
        UserEntity currentUser = securityUtils.getCurrentUser();
        PaymentResponse response = service.initiatePayment(request, currentUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/{paymentId}/process")
    public ResponseEntity<PaymentResponse> processPayment(
            @PathVariable("paymentId") Long paymentId
    ) {
        return ResponseEntity.ok(service.processPayment(paymentId));
    }
}
