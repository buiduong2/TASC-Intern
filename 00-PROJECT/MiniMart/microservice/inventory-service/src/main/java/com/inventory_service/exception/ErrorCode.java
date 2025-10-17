package com.inventory_service.exception;

import java.text.MessageFormat;

import org.springframework.http.HttpStatus;

public enum ErrorCode implements com.common.exception.ErrorCode {
    PURCHASE_NOT_FOUND(HttpStatus.NOT_FOUND, "Purchase with id = {0} not found"),
    PURCHASE_UPDATE_ACTIVE_STATUS_CONFLICT(HttpStatus.CONFLICT,
            "Cannot update status = Active for Purchase with id = {0} because some product is stale"),
    PURCHASE_DELETE_STATUS_CONLICT(HttpStatus.CONFLICT,
            "cannot delete Purchase with id = {0} because status is not valid"),

    PURCHASE_CREATE_ITEM_STATUS_CONLICT(HttpStatus.CONFLICT,
            "cannot create item of Purchase with id = {0} because it confirmed"),
    PURCHASE_CREATE_ITEM_PRODUCT_CONLICT(HttpStatus.CONFLICT,
            "cannot create item of Purchase with id = {0} because productId has exisited"),
    PURCHASE_ITEM_DELETE_HAS_ALLOCATIONS(HttpStatus.CONFLICT,
            "Cannot delete purchase item with id={0} because it has stock allocations"),
    PURCHASE_ITEM_DELETE_CONFLICT(HttpStatus.CONFLICT,
            "Cannot delete purchase item with id={0} because stock has already been used"),

    PRODUCTS_NOT_FOUND(HttpStatus.NOT_FOUND, "Some product not found"),
    PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "Product with id = {0} not found"),

    PURCHASE_ITEM_NOT_FOUND(HttpStatus.NOT_FOUND, "Purchase Item with id = {0} not found"),

    STOCK_RESERVATION_PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "Stock With product id = {0} not found"),

    STOCK_RESERVATION_INSUFFICIENT(HttpStatus.INSUFFICIENT_STORAGE, "Insufficient stock for ProductId = {0}");

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
