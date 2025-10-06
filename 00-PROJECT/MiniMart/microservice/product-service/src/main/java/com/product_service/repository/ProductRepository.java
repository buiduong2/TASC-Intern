package com.product_service.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.EntityGraph.EntityGraphType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.product_service.dto.res.ProductSummaryDTO;
import com.product_service.enums.ProductStatus;
import com.product_service.model.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("""
            SELECT p.id AS id , p.name AS name, i.url AS imageUrl, p.salePrice AS salePrice, p.compareAtPrice AS compareAtPrice , p.stock AS stock
            FROM Product  AS p
            LEFT JOIN p.image AS i
            WHERE p.category.id = ?1 AND p.status = ?2
            """)
    Page<ProductSummaryDTO> findForDTOByCategoryIdAndStatus(long categoryId, ProductStatus status, Pageable pageable);

    @EntityGraph(value = Product.NamedGraph_DetailDTO, type = EntityGraphType.FETCH)
    Optional<Product> findClientDTOByIdAndStatus(long productId, ProductStatus status);

    @Query("""
            FROM Product AS p
            LEFT JOIN FETCH p.tags
            WHERE p.id = ?1
            """)
    Optional<Product> findForUpdateById(long productId);
}
