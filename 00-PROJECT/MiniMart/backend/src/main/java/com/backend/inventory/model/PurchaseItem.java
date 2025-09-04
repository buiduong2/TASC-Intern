package com.backend.inventory.model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;

import com.backend.product.model.Product;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class PurchaseItem {
    @Id
    @GeneratedValue
    private Long id;

    private int quantity;

    private int remainingQuantity;

    private double costPrice;

    @ManyToOne
    private Purchase purchase;

    @ManyToOne
    private Product product;

    @CreatedDate
    private LocalDateTime createdAt;

}
