package com.backend.product.model;

import java.util.Set;

import com.backend.common.model.Audit;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Tag {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    private String description;

    @Embedded
    private Audit audit;

    @ManyToMany(mappedBy = "tags")
    private Set<Product> products;

}
