package com.inventory_service.event;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderCompletedEvent {

    private long orderId;
}
