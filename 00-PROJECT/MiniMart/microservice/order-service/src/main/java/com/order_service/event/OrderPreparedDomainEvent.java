package com.order_service.event;

import com.order_service.model.Order;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class OrderPreparedDomainEvent {
    private Order order;

}
