package com.inventory_service.exception;

public class AllocationNotFoundException extends RuntimeException {
    public AllocationNotFoundException(String msg) {
        super(msg);
    }
}
