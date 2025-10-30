package com.order_service.event;

import com.order_service.model.Order;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OrderCompletedDomainEvent {

    private final Order order;
}
