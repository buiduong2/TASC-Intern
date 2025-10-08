package com.product_service.exception;

import java.text.MessageFormat;

import org.springframework.http.HttpStatus;

public enum ErrorCode implements com.common.exception.ErrorCode {
    PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "Product with id = {0} not found"),
    CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, "Category with id = {0} not found"),
    TAG_NOT_FOUND(HttpStatus.NOT_FOUND, "Tag with id = {0} not found"),
    TAG_SOME_NOT_FOUND(HttpStatus.NOT_FOUND, "Some Tags not found"),
    CATEGORY_IMAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "Category Image with id = {0} not found"),
    PRODUCT_IMAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "Product Image with id = {0} not found"),

    //
    CATEGORY_HAS_PRODUCTS(HttpStatus.CONFLICT, "Cannot delete category {0} because it contains existing products");

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
