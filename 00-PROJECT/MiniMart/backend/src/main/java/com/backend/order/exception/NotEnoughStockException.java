package com.backend.order.exception;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class NotEnoughStockException extends RuntimeException {

    private long productId;
    private String message;

    private int requestedQuantity;
    private int availableQuantity;

}
