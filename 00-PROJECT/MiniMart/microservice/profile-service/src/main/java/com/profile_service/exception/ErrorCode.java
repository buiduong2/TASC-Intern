package com.profile_service.exception;

import java.text.MessageFormat;

import org.springframework.http.HttpStatus;

public enum ErrorCode implements com.common.exception.ErrorCode {
    ADDRESS_NOT_FOUND(HttpStatus.NOT_FOUND, "Address with id = {0} not found"),
    PROFILE_NOT_FOUND(HttpStatus.NOT_FOUND, "Profile with id = {0} not found"),
    PROFILE_EXISTED(HttpStatus.CONFLICT, "Profile has areadly existsed");

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
