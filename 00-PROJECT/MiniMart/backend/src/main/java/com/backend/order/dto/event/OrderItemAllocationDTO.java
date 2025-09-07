package com.backend.order.dto.event;

import lombok.Data;

@Data
public class OrderItemAllocationDTO {

    private long purchaseId;

    private long orderItemId;

    private long delta;

}
