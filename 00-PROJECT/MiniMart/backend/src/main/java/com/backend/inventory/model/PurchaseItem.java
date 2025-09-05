package com.backend.inventory.model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;

import com.backend.product.model.Product;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(uniqueConstraints = { @UniqueConstraint(columnNames = { "purchase_id", "product_id" }) })
public class PurchaseItem {
    @Id
    @GeneratedValue
    private Long id;

    private int quantity;

    private int remainingQuantity;

    private double costPrice;

    @ManyToOne(fetch = FetchType.LAZY)
    private Purchase purchase;

    @ManyToOne(fetch = FetchType.LAZY)
    private Product product;

    @CreatedDate
    private LocalDateTime createdAt;

}
