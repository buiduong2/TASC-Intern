package com.backend.product.dto.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ProductCreatedEvent {

    private long productId;

}
