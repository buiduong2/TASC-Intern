package com.common_kafka.exception.saga;

public class LogicViolationException extends NonRetryableSagaException {

    public LogicViolationException(String entity, Object reference, String message) {
        super(message, "LOGIC_VIOLATION", entity, reference);
    }

    public static LogicViolationException of(String entity, Object ref, String message) {
        return new LogicViolationException(entity, ref, message);
    }
}