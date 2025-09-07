package com.backend.product.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.EntityGraph.EntityGraphType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.backend.product.model.Product;
import com.backend.product.model.ProductStatus;

public interface ProductRepository extends JpaRepository<Product, Long> {

    @EntityGraph(value = "Product.ProductDTO", type = EntityGraphType.FETCH)
    @Query(value = """
            FROM Product AS p WHERE p.category.id = ?1 AND p.status = ?2
             """)
    Page<Product> findDTOByCategoryIdAndStatus(long categoryId, ProductStatus status, Pageable pageable);

    @EntityGraph(value = "Product.ProductDetailDTO", type = EntityGraphType.FETCH)
    Optional<Product> findDetailDTOByIdAndStatus(long id, ProductStatus status);

    List<Product> findByIdInAndStatus(List<Long> productId, ProductStatus status);

}
