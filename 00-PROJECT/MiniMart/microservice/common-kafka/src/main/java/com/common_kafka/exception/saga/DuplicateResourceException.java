package com.common_kafka.exception.saga;

public class DuplicateResourceException extends NonRetryableSagaException {

    public DuplicateResourceException(String entity, Object reference) {
        super("Duplicate " + entity + " detected for id=" + reference, "DUPLICATE", entity, reference);
    }

    public static DuplicateResourceException of(String entity, Object ref) {
        return new DuplicateResourceException(entity, ref);
    }
}