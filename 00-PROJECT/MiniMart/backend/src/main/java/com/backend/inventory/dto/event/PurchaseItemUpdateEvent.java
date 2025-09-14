package com.backend.inventory.dto.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PurchaseItemUpdateEvent {

    private Long productId;
}
