package com.common.exception;

public interface ErrorCode {
    int getStatus();

    String getError();

    String getTemplate();

    String getTemplateFormat(Object[] messageArg);
}
