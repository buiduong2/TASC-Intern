package com.backend.user.exception;

public class TokenBlacklistedException extends RuntimeException {
    public TokenBlacklistedException(String msg) {
        super(msg);
    }
}
