package com.inventory_service.exception;

public class StockReservationException extends RuntimeException {
    public StockReservationException(ErrorCode errorCode, Object... messageArg) {
        super(errorCode.format(messageArg));
    }
}
