package com.common_kafka.exception;

import lombok.Getter;

/**
 * Đại diện cho lỗi không mong đợi (bug, NPE, logic sai)
 * trong quá trình xử lý event.
 * Nên log ERROR và gửi cảnh báo cho DevOps.
 */
@Getter
public class UnhandledEventException extends RuntimeException {

    private final String eventType;
    private final long referenceId;

    public UnhandledEventException(String eventType, long referenceId, String message) {
        super(message);
        this.eventType = eventType;
        this.referenceId = referenceId;
    }

    public UnhandledEventException(String eventType, long referenceId, String message, Throwable cause) {
        super(message, cause);
        this.eventType = eventType;
        this.referenceId = referenceId;
    }

}
