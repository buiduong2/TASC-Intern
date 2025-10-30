package com.common_kafka.exception.saga;

public class ResourceNotFoundException extends NonRetryableSagaException {

    public ResourceNotFoundException(String entity, Object reference) {
        super(entity + " not found for id=" + reference, "NOT_FOUND", entity, reference);
    }

    public static ResourceNotFoundException of(String entity, Object ref) {
        return new ResourceNotFoundException(entity, ref);
    }
}