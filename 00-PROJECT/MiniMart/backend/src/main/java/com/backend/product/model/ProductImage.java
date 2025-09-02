package com.backend.product.model;

import com.backend.common.model.BaseImage;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class ProductImage extends BaseImage {

    @OneToOne(mappedBy = "image")
    private Product product;
}
