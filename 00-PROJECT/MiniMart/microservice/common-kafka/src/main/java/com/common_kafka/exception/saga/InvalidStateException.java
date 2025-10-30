package com.common_kafka.exception.saga;

public class InvalidStateException extends NonRetryableSagaException {

    public InvalidStateException(String entity, Object reference, String current, String expected) {
        super(String.format("%s in invalid state. Current=%s, Expected=%s",
                entity, current, expected),
                "INVALID_STATE", entity, reference);
    }

    public static InvalidStateException of(String entity, Object ref, String current, String expected) {
        return new InvalidStateException(entity, ref, current, expected);
    }
}