package com.product_service.dto.res;

import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ProductCheckExistsRes {
    private boolean isValid;
    private Set<Long> missingIds;
}
