package com.backend.order.exception;

public class InvalidSignatureException extends RuntimeException {
    public InvalidSignatureException(String msString) {
        super(msString);
    }
}
