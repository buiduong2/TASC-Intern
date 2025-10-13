package com.inventory_service.dto.req;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ProductCheckExistsReq {

    private List<Long> productIds;
}
