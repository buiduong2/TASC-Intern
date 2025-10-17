package com.product_service.dto.res;

import java.util.List;

import com.product_service.model.Product;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(staticName = "of")
public class ProductValidationResult {
    private final boolean allValid;
    private final List<Product> validProducts;
}
