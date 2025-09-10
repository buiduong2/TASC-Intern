package com.backend.user.exception;

public class TokenVersionMismatchException extends RuntimeException {
    public TokenVersionMismatchException(String msg) {
        super(msg);
    }

}
