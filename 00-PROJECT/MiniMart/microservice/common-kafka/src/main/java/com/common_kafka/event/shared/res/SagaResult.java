package com.common_kafka.event.shared.res;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class SagaResult<T> {

    private final boolean success;
    private final T data;
    private final String reason;
    private final Throwable exception;

    public static <T> SagaResult<T> success(T data) {
        return new SagaResult<>(true, data, null, null);
    }

    public static <T> SagaResult<T> failure(String reason) {
        return new SagaResult<>(false, null, reason, null);
    }

    public static <T> SagaResult<T> failure(Throwable exception) {
        String reason = exception.getMessage() != null
                ? exception.getMessage()
                : exception.getClass().getSimpleName();
        return new SagaResult<>(false, null, reason, exception);
    }

    public boolean hasException() {
        return exception != null;
    }
}
