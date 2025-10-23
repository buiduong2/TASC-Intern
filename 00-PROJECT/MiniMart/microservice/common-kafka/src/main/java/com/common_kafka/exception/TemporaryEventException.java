package com.common_kafka.exception;

import lombok.Getter;

/**
 * Lỗi tạm thời khi xử lý event (transient error).
 * Có thể retry vì lỗi này thường do hạ tầng hoặc tạm thời.
 */
@Getter
public class TemporaryEventException extends RuntimeException {

    private final String eventType;
    private final long referenceId;

    public TemporaryEventException(String eventType, long referenceId, String message) {
        super(message);
        this.eventType = eventType;
        this.referenceId = referenceId;
    }

    public TemporaryEventException(String eventType, long referenceId, String message, Throwable cause) {
        super(message, cause);
        this.eventType = eventType;
        this.referenceId = referenceId;
    }

}