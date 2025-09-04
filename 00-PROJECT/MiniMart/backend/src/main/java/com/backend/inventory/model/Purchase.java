package com.backend.inventory.model;

import java.util.List;

import com.backend.common.model.Audit;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Purchase {

    @Id
    @GeneratedValue
    private Long id;

    @Embedded
    private Audit audit;

    private String supplier;

    @OneToMany(mappedBy = "purchase")
    private List<PurchaseItem> purchaseItems;
}
