package com.manoj.finorder.payledgerservice.service;

import com.manoj.finorder.payledgerservice.api.CreatePaymentIntentRequest;
import com.manoj.finorder.payledgerservice.event.PaymentEvent;
import com.manoj.finorder.payledgerservice.model.LedgerEntry;
import com.manoj.finorder.payledgerservice.model.LedgerEntryType;
import com.manoj.finorder.payledgerservice.model.PaymentIntent;
import com.manoj.finorder.payledgerservice.model.PaymentIntentStatus;
import com.manoj.finorder.payledgerservice.repository.LedgerEntryRepository;
import com.manoj.finorder.payledgerservice.repository.PaymentIntentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PaymentIntentService {
    private static final String CASH_ACCOUNT = "CASH";
    private static final String REVENUE_ACCOUNT = "REVENUE";
    private final PaymentIntentRepository paymentIntentRepository;
    private final LedgerEntryRepository ledgerEntryRepository;
    private final KafkaTemplate<String, PaymentEvent> kafkaTemplate;
    @Value("${app.kafka.topics.payment-events:payment-events}")
    private String paymentEventsTopic;

    public PaymentIntent createPaymentIntent(CreatePaymentIntentRequest request, String idempotencyKey) {
        if(idempotencyKey != null && !idempotencyKey.isEmpty()) {
            Optional<PaymentIntent> existing = paymentIntentRepository.findByIdempotencyKey(idempotencyKey);
            if(existing.isPresent()) {
                PaymentIntent paymentIntent = existing.get();
                boolean matches = paymentIntent.getAmount().compareTo(request.getAmount()) == 0 &&
                        paymentIntent.getCurrency().equalsIgnoreCase(request.getCurrency()) &&
                        paymentIntent.getOrderId().equals(request.getOrderId());
                if(!matches) {
                    throw new ResponseStatusException(HttpStatus.CONFLICT, "Idempotency key reuse with different payload: Payment Intent already exists");
                }
                return paymentIntent;
            }
        }

        Instant now = Instant.now();
        PaymentIntent paymentIntent = PaymentIntent.builder()
                .orderId(request.getOrderId())
                .amount(request.getAmount())
                .currency(request.getCurrency())
                .status(PaymentIntentStatus.CREATED)
                .idempotencyKey(idempotencyKey)
                .createdAt(now)
                .updatedAt(now)
                .build();
        return paymentIntentRepository.save(paymentIntent);
    }

    public Optional<PaymentIntent> getPaymentIntent(String paymentIntentId) {
        return paymentIntentRepository.findById(paymentIntentId);
    }

    public Optional<PaymentIntent> capturePaymentIntent(String id) {
        return paymentIntentRepository.findById(id).map(paymentIntent -> {
            if (paymentIntent.getStatus() == PaymentIntentStatus.CAPTURED) {
                return paymentIntent;
            }
            paymentIntent.setStatus(PaymentIntentStatus.CAPTURED);
            paymentIntent.setUpdatedAt(Instant.now());
            PaymentIntent saved = paymentIntentRepository.save(paymentIntent);
            postLedgerEntries(saved);
            publishCaptured(saved);

            return saved;
        });
    }

    private void postLedgerEntries(PaymentIntent paymentIntent) {
        Instant now = Instant.now();
        LedgerEntry debit = LedgerEntry.builder()
                .paymentIntentId(paymentIntent.getId())
                .orderId(paymentIntent.getOrderId())
                .account(CASH_ACCOUNT)
                .amount(paymentIntent.getAmount())
                .currency(paymentIntent.getCurrency())
                .entryType(LedgerEntryType.DEBIT)
                .createdAt(now)
                .build();
        LedgerEntry credit = LedgerEntry.builder()
                .paymentIntentId(paymentIntent.getId())
                .orderId(paymentIntent.getOrderId())
                .account(REVENUE_ACCOUNT)
                .amount(paymentIntent.getAmount())
                .currency(paymentIntent.getCurrency())
                .entryType(LedgerEntryType.CREDIT)
                .createdAt(now)
                .build();
        ledgerEntryRepository.saveAll(List.of(debit, credit));
    }

    private void publishCaptured(PaymentIntent paymentIntent) {
        PaymentEvent event = PaymentEvent.builder()
                .eventType("PaymentCaptured")
                .orderId(paymentIntent.getOrderId())
                .paymentIntentId(paymentIntent.getId())
                .amount(paymentIntent.getAmount())
                .currency(paymentIntent.getCurrency())
                .occuredAt(Instant.now())
                .build();
        kafkaTemplate.send(paymentEventsTopic, paymentIntent.getOrderId(), event);
    }
}
