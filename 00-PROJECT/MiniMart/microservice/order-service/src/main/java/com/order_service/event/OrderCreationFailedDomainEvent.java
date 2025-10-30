package com.order_service.event;

import com.order_service.model.Order;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@RequiredArgsConstructor
public class OrderCreationFailedDomainEvent {
    private final Order order;
}
