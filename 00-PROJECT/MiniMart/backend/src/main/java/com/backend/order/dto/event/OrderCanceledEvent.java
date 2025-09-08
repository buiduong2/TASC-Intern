package com.backend.order.dto.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class OrderCanceledEvent {
    private Long orderId;
}
