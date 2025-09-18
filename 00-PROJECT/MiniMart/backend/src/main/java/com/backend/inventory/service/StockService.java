package com.backend.inventory.service;

import java.util.List;

public interface StockService {

    /**
     * Tăng các giá trị Quantity dựa vào số lượng hàng mới nhập
     */
    void syncQuantity(List<Long> productIds);

    void create(long productId);

    void deleteByProductId(long productId);

}
