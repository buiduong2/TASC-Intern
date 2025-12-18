package com.common_kafka.event.shared.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@EqualsAndHashCode(of = "productId")
@AllArgsConstructor
@Setter
@NoArgsConstructor
public class ValidatedItemSnapshot {
    private Long orderItemId;
    private Long productId;
    private int quantity;
    private BigDecimal unitPrice;
}