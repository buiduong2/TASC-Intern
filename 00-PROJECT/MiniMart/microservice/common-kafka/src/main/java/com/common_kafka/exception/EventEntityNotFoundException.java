package com.common_kafka.exception;

/**
 * Xảy ra khi event tham chiếu đến entity không tồn tại trong hệ thống.
 * Thường là lỗi do dữ liệu chưa được đồng bộ hoặc bị xóa.
 */
public class EventEntityNotFoundException extends BusinessEventException {
    public EventEntityNotFoundException(String eventType, long referenceId, String entityName) {
        super(eventType, referenceId,
                String.format("[%s] referenced entity '%s' not found (id=%d)",
                        eventType, entityName, referenceId));
    }

    public EventEntityNotFoundException(String eventType, long referenceId, String entityName, Throwable cause) {
        super(eventType, referenceId,
                String.format("[%s] referenced entity '%s' not found (id=%d)",
                        eventType, entityName, referenceId),
                cause);
    }
}
