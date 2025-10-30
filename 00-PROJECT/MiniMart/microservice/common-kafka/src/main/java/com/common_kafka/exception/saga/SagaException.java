package com.common_kafka.exception.saga;

public abstract class SagaException extends RuntimeException {

    private final String entity;
    private final Object reference;
    private final String code;

    protected SagaException(String message, String code, String entity, Object reference) {
        super(message);
        this.entity = entity;
        this.reference = reference;
        this.code = code;
    }

    /** Có thể override để cho biết lỗi này có nên retry hay không */
    public abstract boolean isRetryable();

    @Override
    public String toString() {
        return String.format("[%s][%s id=%s] %s", code, entity, reference, getMessage());
    }
}
