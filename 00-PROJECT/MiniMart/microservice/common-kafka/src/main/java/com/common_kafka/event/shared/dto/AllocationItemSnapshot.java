package com.common_kafka.event.shared.dto;

import java.math.BigDecimal;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(of = "productId")
public class AllocationItemSnapshot {
    private long productId;
    private long quantity;
    private BigDecimal avgCostPrice;
}
