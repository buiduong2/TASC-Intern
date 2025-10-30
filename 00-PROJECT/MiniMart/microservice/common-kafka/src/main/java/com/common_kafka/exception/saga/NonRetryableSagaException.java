package com.common_kafka.exception.saga;

public abstract class NonRetryableSagaException extends SagaException {

    protected NonRetryableSagaException(String message, String code, String entity, Object reference) {
        super(message, code, entity, reference);
    }

    @Override
    public boolean isRetryable() {
        return false;
    }
}
