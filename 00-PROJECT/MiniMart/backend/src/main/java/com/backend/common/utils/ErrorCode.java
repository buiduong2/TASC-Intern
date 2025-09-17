package com.backend.common.utils;

import java.text.MessageFormat;

public enum ErrorCode {
    PRODUCT_NOT_FOUND("Product with id={0} not found"),

    // Inventory
    PURCHASE_NOT_FOUND("Purchase with id={0} not found"),
    PURCHASE_ITEM_DELETE_CONFLICT("Cannot delete purchase item with id={0} because stock has already been used"),
    PURCHASE_ITEM_DELETE_HAS_ALLOCATIONS("Cannot delete purchase item with id={0} because it has stock allocations"),
    PURCHASE_ITEM_NOT_FOUND("Purchase item with id={0} not found");

    private final String template;

    ErrorCode(String template) {
        this.template = template;
    }

    public String format(Object... args) {
        return MessageFormat.format(template, args);
    }

    public String getTemplate() {
        return template;
    }

    public String getCode() {
        return name();
    }
}
