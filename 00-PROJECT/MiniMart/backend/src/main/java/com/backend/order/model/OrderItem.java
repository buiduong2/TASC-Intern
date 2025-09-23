package com.backend.order.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import com.backend.inventory.model.StockAllocation;
import com.backend.product.model.Product;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class OrderItem {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private Product product;

    private int quantity;

    @Column(precision = 19, scale = 2)
    private BigDecimal unitPrice;

    @Column(precision = 19, scale = 2)
    private BigDecimal avgCostPrice;

    @ManyToOne(fetch = FetchType.LAZY)
    private Order order;

    @OneToMany(mappedBy = "orderItem", cascade = CascadeType.ALL)
    private List<StockAllocation> allocations;

    @Transient
    public BigDecimal getTotalPrice() {
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }

    public void calculateAvgCost() {
        BigDecimal totalCost = BigDecimal.ZERO;
        int totalQty = 0;

        for (StockAllocation allocation : allocations) {
            BigDecimal costPrice = allocation.getPurchaseItem().getCostPrice(); // BigDecimal
            int qty = allocation.getAllocatedQuantity(); // int

            totalCost = totalCost.add(costPrice.multiply(BigDecimal.valueOf(qty)));
            totalQty += qty;
        }

        this.avgCostPrice = totalQty == 0
                ? BigDecimal.ZERO
                : totalCost.divide(BigDecimal.valueOf(totalQty), 2, RoundingMode.HALF_UP);
    }
}
