package com.inventory_service.dto.res;

import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductCheckExistsRes {
    private boolean isValid;
    private Set<Long> missingIds;
}
