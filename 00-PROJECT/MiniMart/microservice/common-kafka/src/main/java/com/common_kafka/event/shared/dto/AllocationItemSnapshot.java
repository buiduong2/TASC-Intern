package com.common_kafka.event.shared.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode(of = "productId")
@AllArgsConstructor(staticName = "of")
public class AllocationItemSnapshot {
    private final long productId;
    private final long quantity;
    private final BigDecimal avgCostPrice;
}
