package com.common_kafka.event.shared.dto;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(of = "productId")
@AllArgsConstructor
public class FailedItemInfo {
    private long productId;
    private int quantityRequested;
    private int quantityAvailable;
}
