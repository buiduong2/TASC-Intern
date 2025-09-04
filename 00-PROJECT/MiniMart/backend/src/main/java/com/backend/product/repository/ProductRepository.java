package com.backend.product.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.EntityGraph.EntityGraphType;
import org.springframework.data.jpa.repository.JpaRepository;

import com.backend.product.model.Product;
import com.backend.product.model.Status;

public interface ProductRepository extends JpaRepository<Product, Long> {

    @EntityGraph(value = "Product.ProductDTO", type = EntityGraphType.LOAD)
    Page<Product> findDTOByCategoryIdAndStatus(long categoryId, Status status, Pageable pageable);

    @EntityGraph(value = "Product.ProductDetailDTO", type = EntityGraphType.LOAD)
    Optional<Product> findDetailDTOByIdAndStatus(long id, Status status);

}
