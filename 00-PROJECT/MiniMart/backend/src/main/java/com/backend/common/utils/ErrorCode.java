package com.backend.common.utils;

import java.text.MessageFormat;

public enum ErrorCode {
    // Product
    PRODUCT_NOT_FOUND("Product with id={0} not found"),
    PRODUCT_IMAGE_NOT_FOUND("product Image with id={0} not found"),
    TAGS_NOT_FOUND("Some Tags with id={0} not found"),

    // Inventory
    PURCHASE_NOT_FOUND("Purchase with id={0} not found"),
    PURCHASE_ITEM_DELETE_CONFLICT("Cannot delete purchase item with id={0} because stock has already been used"),
    PURCHASE_ITEM_DELETE_HAS_ALLOCATIONS("Cannot delete purchase item with id={0} because it has stock allocations"),
    PURCHASE_ITEM_NOT_FOUND("Purchase item with id={0} not found"),

    // Order
    ORDER_NOT_FOUND("Order with id={0} not found"),
    PAYMENT_NOT_FOUND("Payment with id={0} not found"),
    PAYMENT_COMPLETED("Payment with id={0} already completed and cannot be modified"),
    PAYMENT_TRANSACTION_NOT_FOUND("Payment transaction txnRef={0} not found"),
    // AUTH
    ROLE_NOT_FOUND("Role with id={0} not found"),

    // Cart
    CART_ITEM_NOT_FOUND("Cart Item with id={0} not found"),
    CART_NOT_FOUND("Cart with userId={0} not found");

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
