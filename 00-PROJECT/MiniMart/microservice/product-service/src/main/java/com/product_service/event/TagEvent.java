package com.product_service.event;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class TagEvent {

    private long id;
    private List<Long> productIds;
    private Action action;
}
