package com.inventory_service.service;

import java.util.List;

public interface StockService {
    void create(List<Long> productIds);

    void syncQuantity(long productId);

    void syncQuantity(List<Long> productIds);

}
