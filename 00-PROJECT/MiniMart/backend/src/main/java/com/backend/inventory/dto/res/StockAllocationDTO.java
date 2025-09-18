package com.backend.inventory.dto.res;

import java.time.LocalDateTime;

public interface StockAllocationDTO {
    Long getId();

    int getAllocatedQuantity();

    LocalDateTime getCreatedAt();

    // Ánh xạ từ các entity liên quan
    Long getPurchaseItem_Id();

    String getPurchaseItem_Product_Name();

    Double getPurchaseItem_CostPrice();

    Long getOrderItem_Id();

    Double getOrderItem_UnitPrice();

    String getOrderItem_Product_Name(); // Nếu cần tên sản phẩm từ OrderItem
}
