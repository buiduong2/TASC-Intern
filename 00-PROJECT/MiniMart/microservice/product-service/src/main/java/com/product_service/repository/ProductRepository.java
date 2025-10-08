package com.product_service.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.EntityGraph.EntityGraphType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.product_service.dto.res.ProductDetailDTO.ProductRelateDTO;
import com.product_service.dto.res.ProductSummaryDTO;
import com.product_service.enums.ProductStatus;
import com.product_service.model.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("""
            SELECT p.id AS id , p.name AS name, i.url AS imageUrl, p.salePrice AS salePrice, p.compareAtPrice AS compareAtPrice , p.stock AS stock
            FROM Product  AS p
            LEFT JOIN p.image AS i
            WHERE p.status = ?2 AND p.category.id = ?1
            """)
    Page<ProductSummaryDTO> findForDTOByCategoryIdAndStatus(long categoryId, ProductStatus status, Pageable pageable);

    @EntityGraph(value = Product.NamedGraph_DetailDTO, type = EntityGraphType.FETCH)
    Optional<Product> findClientDTOByIdAndStatus(long productId, ProductStatus status);

    @EntityGraph(value = Product.NamedGraph_DetailDTO, type = EntityGraphType.FETCH)
    List<Product> findClientDTOByIdInAndStatus(List<Long> ids, ProductStatus active);

    @Query("""
            FROM Product AS p
            LEFT JOIN FETCH p.tags
            WHERE p.id = ?1
            """)
    Optional<Product> findForUpdateById(long productId);

    @Query("""
            SELECT new com.product_service.dto.res.ProductDetailDTO$ProductRelateDTO(
                    p.id ,
                    p.name ,
                    i.url ,
                    p.salePrice ,
                    p.compareAtPrice ,
                    p.stock
            )
            FROM Product AS p
            LEFT JOIN p.image AS i
            WHERE p.id IN ?1 AND status = ?2
            """)
    List<ProductRelateDTO> findRelatedByIdIn(Collection<Long> ids, ProductStatus status);

    @Query("""
            SELECT p.id
            FROM Product AS p
            WHERE p.category.id = ?1 AND p.status = ?2
            """)
    List<Long> findProductIdByCategoryId(long categoryId, ProductStatus status);

    @Query("""
            SELECT DISTINCT p.id
            FROM Product AS p
            JOIN p.tags AS t
            WHERE t.id = ?1
            """)
    List<Long> findProductIdByTagId(long tagId);

    @Query("""
            SELECT EXISTS 1
            FROM Product  AS p
            WHERE p.category.id = ?1
            """)
    boolean existsByCategoryId(long categoryId);

}
