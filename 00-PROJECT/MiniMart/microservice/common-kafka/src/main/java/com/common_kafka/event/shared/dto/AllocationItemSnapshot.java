package com.common_kafka.event.shared.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@EqualsAndHashCode(of = "productId")
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
public class AllocationItemSnapshot {
    private long productId;
    private long quantity;
    private BigDecimal avgCostPrice;
}
