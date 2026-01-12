package com.manoj.finorder.payledgerservice.api;

import com.manoj.finorder.payledgerservice.model.PaymentIntent;
import com.manoj.finorder.payledgerservice.service.PaymentIntentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;

@RequestMapping("/payments/intents")
@RestController
@RequiredArgsConstructor
public class PaymentIntentController {
    private final PaymentIntentService paymentIntentService;

    @PostMapping
    public ResponseEntity<PaymentIntentResponse> createPaymentIntent(@Valid @RequestBody CreatePaymentIntentRequest request,
                                                                     @RequestHeader(value = "Idempotency-key", required = false) String idempotencyKey) {
        PaymentIntent paymentIntent = paymentIntentService.createPaymentIntent(request, idempotencyKey);
        return ResponseEntity.created(URI.create("/payments/intents/" + paymentIntent.getId())).body(toResponse(paymentIntent));
    }

    @PostMapping("/{id}/capture")
    public ResponseEntity<PaymentIntentResponse> capturePaymentIntent(@PathVariable String id) {
        PaymentIntent paymentIntent = paymentIntentService.capturePaymentIntent(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Payment Intent not found"));
        return ResponseEntity.ok(toResponse(paymentIntent));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentIntentResponse> getPaymentIntent(@PathVariable String id) {
        PaymentIntent paymentIntent = paymentIntentService.getPaymentIntent(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Payment Intent not found"));
        return ResponseEntity.ok(toResponse(paymentIntent));
    }

    private PaymentIntentResponse toResponse(PaymentIntent paymentIntent) {
        return PaymentIntentResponse.builder()
                .id(paymentIntent.getId())
                .orderId(paymentIntent.getOrderId())
                .currency(paymentIntent.getCurrency())
                .status(paymentIntent.getStatus())
                .amount(paymentIntent.getAmount())
                .createdAt(paymentIntent.getCreatedAt())
                .updatedAt(paymentIntent.getUpdatedAt())
                .build();
    }
}
