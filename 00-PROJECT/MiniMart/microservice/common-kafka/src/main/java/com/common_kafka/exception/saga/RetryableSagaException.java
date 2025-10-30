package com.common_kafka.exception.saga;

public abstract class RetryableSagaException extends SagaException {

    protected RetryableSagaException(String message, String code, String entity, Object reference) {
        super(message, code, entity, reference);
    }

    @Override
    public boolean isRetryable() {
        return true;
    }
}
