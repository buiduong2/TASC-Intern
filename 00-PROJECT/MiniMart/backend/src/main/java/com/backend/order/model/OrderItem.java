package com.backend.order.model;

import com.backend.product.model.Product;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
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

    private double sellPrice;

    private double costPrice;

    @ManyToOne(fetch = FetchType.LAZY)
    private Order order;

    @Transient
    public double getTotalPrice() {
        return quantity * sellPrice;
    }
}
