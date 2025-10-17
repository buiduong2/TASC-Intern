package com.common_kafka.event.shared.dto;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of = "productId")
@AllArgsConstructor
public class OrderItemData {
    private Long productId;
    private int quantity;
}
