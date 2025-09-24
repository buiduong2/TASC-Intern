package com.backend.common.exception;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class StockConflictResponse {
    private String code;
    private String message;
    private LocalDateTime timestamp;
    private int status;
    private ConflictDetail detail;

    @Getter
    @Setter
    @Builder
    public static class ConflictDetail {
        long productId;
        int requestedQuantity;
        int availableQuantity;
    }
}
