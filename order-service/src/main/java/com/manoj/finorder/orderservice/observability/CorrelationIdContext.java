package com.manoj.finorder.orderservice.observability;

import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.Headers;
import org.slf4j.MDC;

import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.UUID;

public final class CorrelationIdContext {
    public static final String HEADER_NAME = "X-Correlation-ID";
    public static final String MDC_KEY = "correlationId";

    private CorrelationIdContext() {
    }

    public static String getOrCreate(String existing) {
        if (existing != null && !existing.isBlank()) {
            return existing;
        }
        return UUID.randomUUID().toString();
    }

    public static String fromHeaders(Headers headers) {
        if (headers == null) {
            return null;
        }
        Header header = headers.lastHeader(HEADER_NAME);
        if (header == null) {
            return null;
        }
        return new String(header.value(), StandardCharsets.UTF_8);
    }

    public static void set(String correlationId) {
        MDC.put(MDC_KEY, getOrCreate(correlationId));
    }

    public static Optional<String> get() {
        return Optional.ofNullable(MDC.get(MDC_KEY));
    }

    public static void clear() {
        MDC.remove(MDC_KEY);
    }
}
