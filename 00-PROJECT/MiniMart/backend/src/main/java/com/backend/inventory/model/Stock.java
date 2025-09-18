package com.backend.inventory.model;

import com.backend.product.model.Product;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
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

    @OneToOne(fetch = FetchType.LAZY)
    private Product product;

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Stock other = (Stock) obj;
        return id != null && id == other.getId();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }
}
