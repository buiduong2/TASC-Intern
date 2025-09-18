package com.backend.product.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.EntityGraph.EntityGraphType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.backend.product.dto.res.ProductDTO;
import com.backend.product.model.Product;
import com.backend.product.model.ProductStatus;

public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query(value = """
            SELECT
            new com.backend.product.dto.res.ProductDTO(
                p.id AS id,
                p.name AS name,
                i.url AS imageUrl,
                p.salePrice AS salePrice,
                p.compareAtPrice AS compareAtPrice,
                s.quantity AS stock
                )
            FROM Product AS p
            LEFT JOIN p.image i
            LEFT JOIN p.stock s
            WHERE p.category.id = ?1 AND p.status = ?2
             """)
    Page<ProductDTO> findDTOByCategoryIdAndStatus(long categoryId, ProductStatus status, Pageable pageable);

    @EntityGraph(value = Product.NamedGraph_DTO, type = EntityGraphType.FETCH)
    Page<Product> findAdminDTOBy(Pageable pageable);

    @EntityGraph(value = Product.NamedGraph_DetailDTO, type = EntityGraphType.FETCH)
    Optional<Product> findDetailDTOByIdAndStatus(long id, ProductStatus status);

    @EntityGraph(value = Product.NamedGraph_DetailDTO, type = EntityGraphType.FETCH)
    Optional<Product> findAdminDetailById(long id);

    List<Product> findByIdInAndStatus(List<Long> productId, ProductStatus status);

    @Query("""
            FROM Product AS p 
            LEFT JOIN FETCH p.image
            WHERE p.id = ?1
            """)
    Optional<Product> findByIdForUpdate(long id);

}
