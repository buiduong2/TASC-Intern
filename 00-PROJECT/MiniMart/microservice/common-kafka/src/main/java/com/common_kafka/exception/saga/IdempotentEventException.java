package com.common_kafka.exception.saga;

import java.time.Instant;

public class IdempotentEventException extends NonRetryableSagaException {

    private final Instant processedAt;

    public IdempotentEventException(String entity, Object reference, Instant processedAt) {
        super("Duplicate or already processed event for " + entity + " id=" + reference,
                "IDEMPOTENT_EVENT", entity, reference);
        this.processedAt = processedAt;
    }

    public Instant getProcessedAt() {
        return processedAt;
    }

    public static IdempotentEventException of(String entity, Object ref, Instant at) {
        return new IdempotentEventException(entity, ref, at);
    }
}