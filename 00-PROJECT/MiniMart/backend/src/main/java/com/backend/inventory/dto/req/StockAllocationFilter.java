package com.backend.inventory.dto.req;

import java.time.LocalDateTime;

import jakarta.validation.constraints.Past;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StockAllocationFilter {
    private Long orderId;

    private Long purchaseId;
    
    private Long productId;

    @Past
    private LocalDateTime startDate;

    @Past
    private LocalDateTime endDate;
}
