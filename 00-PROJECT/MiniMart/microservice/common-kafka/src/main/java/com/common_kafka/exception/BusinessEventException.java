package com.common_kafka.exception;

import lombok.Getter;

/**
 * Dành cho lỗi logic nghiệp vụ (permanent failure) — retry lại cũng sẽ thất
 * bại.
 * 
 */
@Getter
public class BusinessEventException extends RuntimeException {
    private final String eventType;
    private final long referenceId;

    public BusinessEventException(String eventType, long referenceId, String message) {
        super(message);
        this.eventType = eventType;
        this.referenceId = referenceId;
    }

    public BusinessEventException(String eventType, long referenceId, String message, Throwable cause) {
        super(message, cause);
        this.eventType = eventType;
        this.referenceId = referenceId;
    }

}
