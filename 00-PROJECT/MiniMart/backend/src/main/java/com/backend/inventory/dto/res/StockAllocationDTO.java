package com.backend.inventory.dto.res;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StockAllocationDTO {

    private long id;

    private int allocatedQuantity;

    private LocalDateTime createdAt;

    // Ánh xạ từ các entity liên quan
    private PurchaseItemDTO purchaseItem;
    private OrderItemDTO orderItem;
    private ProductDTO product;

    @Getter
    @Setter
    public static class PurchaseItemDTO {
        private Long id;
        private BigDecimal costPrice;
    }

    @Getter
    @Setter
    public static class OrderItemDTO {
        private Long id;
        private BigDecimal unitPrice;
    }

    @Getter
    @Setter
    public static class ProductDTO {
        private Long id;
        private String name;
    }
}
