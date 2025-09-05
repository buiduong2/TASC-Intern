package com.backend.inventory.utils;

import java.util.Collections;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.backend.common.utils.AbstractOrderConverter;
import com.backend.common.utils.OrderSupplier;
import com.backend.inventory.model.Purchase;

@Component
public class PurchaseOrderConverter extends AbstractOrderConverter<Purchase> {

    @Override
    public Map<String, OrderSupplier<Purchase>> getSuppliers() {
        return Collections.emptyMap();
    }

}
