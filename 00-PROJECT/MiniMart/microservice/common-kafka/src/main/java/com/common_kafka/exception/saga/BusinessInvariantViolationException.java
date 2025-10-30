package com.common_kafka.exception.saga;

public class BusinessInvariantViolationException extends NonRetryableSagaException {
    public BusinessInvariantViolationException(String entity, Object reference, String messsage) {
        super(messsage, "SYSTEM_ERROR", entity, reference);
    }

    public static BusinessInvariantViolationException of(String entity, Object ref, String msg) {
        return new BusinessInvariantViolationException(entity, ref, msg);
    }
}
