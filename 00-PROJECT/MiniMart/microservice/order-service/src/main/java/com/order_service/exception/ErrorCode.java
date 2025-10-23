package com.order_service.exception;

import java.text.MessageFormat;

import org.springframework.http.HttpStatus;

public enum ErrorCode implements com.common.exception.ErrorCode {
    ORDER_NOT_FOUND(HttpStatus.NOT_FOUND, "Order with id = {0} not found"),
    PAYMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "Payment with id = {0} not found"),
    PAYMENT_CONFLICT_COMPLETED(HttpStatus.METHOD_NOT_ALLOWED,
            "Payment with id={0} already completed and cannot be modified"),
    GATEWAY_INVALID_SIGNATURE(HttpStatus.UNAUTHORIZED, "Invalid {0} signature"),
    GATEWAY_QUERY_DR_INVALID_SIGNATURE(HttpStatus.SERVICE_UNAVAILABLE,
            "Invalid signature from gateway query Dr response"),
    PAYMENT_TRANSACTION_NOT_FOUND(HttpStatus.NOT_FOUND, "Payment transaction txnRef={0} not found");

    private final String template;

    private final HttpStatus status;

    ErrorCode(HttpStatus status, String template) {
        this.template = template;
        this.status = status;
    }

    public String format(Object... args) {
        return MessageFormat.format(template, args);
    }

    @Override
    public int getStatus() {
        return status.value();
    }

    @Override
    public String getError() {
        return status.getReasonPhrase();
    }

    @Override
    public String getTemplate() {
        return template;
    }

    @Override
    public String getTemplateFormat(Object[] messageArg) {
        return MessageFormat.format(template, messageArg);
    }
}
