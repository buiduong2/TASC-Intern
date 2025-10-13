package com.product_service.dto.req;

import java.util.List;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductCheckExistsReq {

    @NotEmpty
    private List<Long> productIds;
}
