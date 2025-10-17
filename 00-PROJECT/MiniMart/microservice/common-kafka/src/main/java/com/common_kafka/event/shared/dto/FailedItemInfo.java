package com.common_kafka.event.shared.dto;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@EqualsAndHashCode(of = "productId")
@AllArgsConstructor
@NoArgsConstructor
public class FailedItemInfo {
    private long productId;
    private int quantityRequested;
    private int quantityAvailable;
}
