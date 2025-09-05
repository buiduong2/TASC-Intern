package com.backend.inventory.model;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.backend.common.model.Audit;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
public class Purchase {

    @Id
    @GeneratedValue
    private Long id;

    @Embedded
    private Audit audit = new Audit();

    private String supplier;

    @OneToMany(mappedBy = "purchase", cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    private List<PurchaseItem> purchaseItems;

    public void addPurchaseItem(PurchaseItem purchaseItem) {
        if (this.purchaseItems == null) {
            this.purchaseItems = new ArrayList<>();
        }
        this.purchaseItems.add(purchaseItem);
        purchaseItem.setPurchase(this);
    }

}
