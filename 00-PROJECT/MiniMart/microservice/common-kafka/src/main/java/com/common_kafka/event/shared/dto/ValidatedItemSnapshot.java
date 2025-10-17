package com.common_kafka.event.shared.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(of = "productId")
@AllArgsConstructor
public class ValidatedItemSnapshot {
    private Long orderItemId;
    private Long productId;
    private int quantity;
    private BigDecimal unitPrice;
}