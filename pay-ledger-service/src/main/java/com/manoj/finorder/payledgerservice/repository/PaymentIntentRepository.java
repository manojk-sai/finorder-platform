package com.manoj.finorder.payledgerservice.repository;

import com.manoj.finorder.payledgerservice.model.PaymentIntent;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface PaymentIntentRepository extends MongoRepository<PaymentIntent, String> {
    Optional<PaymentIntent> findByIdempotencyKey(String idempotencyKey);
}
