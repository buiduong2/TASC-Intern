package com.common.event;

import java.time.Instant;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class DomainEvent<T, ID> {
    private final ID entityId;
    private final Instant occurredAt = Instant.now();
    private final T entity;

    protected DomainEvent(T entity, ID entityId) {
        this.entity = entity;
        this.entityId = entityId;
    }

}