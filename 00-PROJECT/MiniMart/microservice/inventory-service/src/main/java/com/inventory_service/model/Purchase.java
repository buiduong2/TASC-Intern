package com.inventory_service.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.inventory_service.enums.PurchaseStatus;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Purchase {
    @Id
    @GeneratedValue
    private Long id;

    private String supplier;

    @OneToMany(mappedBy = "purchase", cascade = { CascadeType.REMOVE })
    private List<PurchaseItem> purchaseItems;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @CreatedBy
    private Long createdById;

    @LastModifiedBy
    private Long updatedById;

    @Enumerated(EnumType.STRING)
    private PurchaseStatus status;

    public void addPurchaseItem(PurchaseItem purchaseItem) {
        if (this.purchaseItems == null) {
            this.purchaseItems = new ArrayList<>();
        }
        this.purchaseItems.add(purchaseItem);
        purchaseItem.setPurchase(this);
    }

    public void clearPurchaseItem() {
        this.purchaseItems.forEach(i -> i.setPurchase(null));
        this.purchaseItems.clear();
    }

}
