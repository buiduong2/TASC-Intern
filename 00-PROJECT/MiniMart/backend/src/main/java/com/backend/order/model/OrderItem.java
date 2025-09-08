package com.backend.order.model;

import java.util.List;

import com.backend.inventory.model.StockAllocation;
import com.backend.product.model.Product;

import jakarta.persistence.CascadeType;
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

    private double unitPrice;

    private double avgCostPrice;

    @ManyToOne(fetch = FetchType.LAZY)
    private Order order;

    @OneToMany(mappedBy = "orderItem", cascade = CascadeType.ALL)
    private List<StockAllocation> allocations;

    @Transient
    public double getTotalPrice() {
        return quantity * unitPrice;
    }

    public void calculateAvgCost() {
        double totalCost = 0;
        int totalQty = 0;

        for (StockAllocation allocation : allocations) {
            totalCost += allocation.getPurchaseItem().getCostPrice() * allocation.getAllocatedQuantity();
            totalQty += allocation.getAllocatedQuantity();
        }

        this.avgCostPrice = totalQty == 0 ? 0 : totalCost / totalQty;
    }
}
