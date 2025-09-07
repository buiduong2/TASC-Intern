package com.backend.order.exception;

public class NotEnoughStockException extends RuntimeException {

    public NotEnoughStockException(String msg) {
        super(msg);
    }
}
