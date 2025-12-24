package org.example.fasthost.controller;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentApiController {

    private final PaymentService payService;

    @PostMapping("/create")
    public ResponseEntity<PaymentResponse> create(
            @RequestBody CreatePaymentRequest request,
            @CookieValue("AUTH_TOKEN") String token
    ) {
        return ResponseEntity.ok(
                payService.createPayment(request.getAmount(), token)
        );
    }
}
