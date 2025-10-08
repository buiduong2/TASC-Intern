package com.product_service.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ProductEvent {

    private long id;
    private Long categoryId;
    private Long oldCategoryId;
    private Action action;
}
