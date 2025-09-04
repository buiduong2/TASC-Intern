package com.backend.product.model;

import com.backend.common.model.BaseImage;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class CategoryImage extends BaseImage {

    @OneToOne(mappedBy = "image", fetch = FetchType.LAZY)
    private Category category;
}
