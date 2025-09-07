package com.backend.order.dto.event;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderCreatedEvent {

    private Long id;

    private List<OrderItemEvent> orderItems;

    @Getter
    @Setter
    public static class OrderItemEvent {
        private long id;
        private int quantity;
        private long productId;
    }
}
