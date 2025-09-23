package com.backend.inventory.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.backend.product.model.Product;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(uniqueConstraints = { @UniqueConstraint(columnNames = { "purchase_id", "product_id" }) })
@EntityListeners(AuditingEntityListener.class)
public class PurchaseItem {

    @Id
    @GeneratedValue
    private Long id;

    private int quantity;

    private int remainingQuantity;

    @Column(precision = 19, scale = 2)
    private BigDecimal costPrice;

    @ManyToOne(fetch = FetchType.LAZY)
    private Purchase purchase;

    @ManyToOne(fetch = FetchType.LAZY)
    private Product product;

    @OneToMany(mappedBy = "purchaseItem")
    private List<StockAllocation> stockAllocations;

    @CreatedDate
    private LocalDateTime createdAt;

}
