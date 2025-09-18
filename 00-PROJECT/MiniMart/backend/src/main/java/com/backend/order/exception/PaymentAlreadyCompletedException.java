package com.backend.order.exception;

public class PaymentAlreadyCompletedException extends RuntimeException {

    public PaymentAlreadyCompletedException(String msgString) {
        super(msgString);
    }
}
