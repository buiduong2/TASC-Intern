package com.order_service.exception;

import java.text.MessageFormat;

import org.springframework.http.HttpStatus;

public enum ErrorCode implements com.common.exception.ErrorCode {
    ORDER_NOT_FOUND(HttpStatus.NOT_FOUND, "Order with id = {0} not found"),
    // --- LỖI RIÊNG BIỆT CHO HÀM CANCEL ---

    ORDER_CANCEL_STATUS_INVALID(HttpStatus.UNAUTHORIZED,
            "Order with id = {0} is in a non-cancelable status (e.g., Shipping, Delivered)."),

    ORDER_UPDATE_STATUS_INVALID(HttpStatus.UNAUTHORIZED,
            "Order Status with id = {0} can't not satisfied ALLOCATED or SHIPPING "),
    ORDER_UPDATE_STATUS_STEP_INVALID(HttpStatus.UNAUTHORIZED, "Order status with id = {0} current is {1} expected {2}"),
    ORDER_UPDATE_STATUS_PAYMENT(HttpStatus.UNAUTHORIZED, "Order with id = {0} require pay to be process"),

    // --- LỖI CHUNG KHÁC ---
    PAYMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "Payment with id = {0} not found"),
    PAYMENT_TRANSACTION_BLOCKED(HttpStatus.METHOD_NOT_ALLOWED,
            "Payment ID {0} is in status '{1}' and cannot accept new transactions."),
    PAYMENT_TRANSACTION_REFUND_STATUS(HttpStatus.METHOD_NOT_ALLOWED,
            "payment Transaction id = {0} is in status {1} and cannot not accept refund"),
    GATEWAY_REFUND_FAILED(HttpStatus.UNAUTHORIZED, "Gateway refund failed consider try manual"),
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
