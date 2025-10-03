package com.authentication_service.exception;

import java.text.MessageFormat;

import org.springframework.http.HttpStatus;

public enum ErrorCode implements com.common.exception.ErrorCode {
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "User with id = {0} not found"),
    ROLE_NAME_NOT_FOUND(HttpStatus.NOT_FOUND, "Role with name = {0} not found"),

    AUTH_WRONG_PASSWORD(HttpStatus.UNAUTHORIZED, "Invalid password");

    private final String template;

    private final HttpStatus status;

    ErrorCode(HttpStatus status, String template) {
        this.template = template;
        this.status = status;
    }

    public String format(Object... args) {
        return MessageFormat.format(template, args);
    }

    public int getStatus() {
        return status.value();
    }

    @Override
    public String getError() {
        return status.getReasonPhrase();
    }

    @Override
    public String getTemplate() {
        return this.template;
    }

    @Override
    public String getTemplateFormat(Object[] messageArg) {
        return MessageFormat.format(template, messageArg);
    }
}
