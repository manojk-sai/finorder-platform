package com.manoj.finorder.inventoryservice.ops;

import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class ChaosService {
    private final AtomicBoolean failNextReservation = new AtomicBoolean(false);
    private final AtomicReference<String> failNextReason = new AtomicReference<>();
    private final AtomicReference<Instant> crashUntil = new AtomicReference<>(Instant.EPOCH);

    public void failNextReservation(String reason) {
        failNextReservation.set(true);
        failNextReason.set(reason);
    }

    public Optional<String> consumeFailureReason() {
        boolean shouldFail = failNextReservation.getAndSet(false);
        String reason = failNextReason.getAndSet(null);
        return shouldFail ? Optional.ofNullable(reason) : Optional.empty();
    }

    public void crashFor(Duration duration) {
        crashUntil.set(Instant.now().plus(duration));
    }

    public boolean isCrashActive() {
        return Instant.now().isBefore(crashUntil.get());
    }

}
