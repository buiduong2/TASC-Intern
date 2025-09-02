package com.backend.product.model;

import java.util.List;

import com.backend.common.model.Audit;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Category {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    private String description;

    @Enumerated(EnumType.STRING)
    private Status status;

    @Embedded
    private Audit audit;

    @OneToOne(fetch = FetchType.LAZY)
    private CategoryImage image;

    @OneToMany(mappedBy = "category")
    private List<Product> products;

}
