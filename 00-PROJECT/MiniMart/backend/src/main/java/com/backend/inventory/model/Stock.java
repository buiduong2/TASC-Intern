package com.backend.inventory.model;

import com.backend.common.model.Audit;
import com.backend.product.model.Product;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.Setter;

// 1 kho thôi
@Entity
@Getter
@Setter
public class Stock {
    @Id
    @GeneratedValue
    private Long id;

    private int quantity;

    @Embedded
    private Audit audit;
    
    @OneToOne(mappedBy = "stock")
    private Product product;
}
