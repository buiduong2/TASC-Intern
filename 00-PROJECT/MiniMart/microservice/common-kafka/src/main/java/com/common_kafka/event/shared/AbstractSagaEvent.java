package com.common_kafka.event.shared;

import java.time.Instant;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public abstract class AbstractSagaEvent {
    private long orderId;
    private long userId;
    private Instant timestamp;

    public AbstractSagaEvent(long orderId, long userId) {
        this.orderId = orderId;
        this.userId = userId;
        this.timestamp = Instant.now();
    }

}
