package com.backend.inventory.service;

import com.backend.inventory.dto.event.PurchaseCreatedEvent;

public interface StockService {

    /**
     * Tăng các giá trị Quantity dựa vào số lượng hàng mới nhập
     */
    void updateOrCreateStock(PurchaseCreatedEvent event);

}
