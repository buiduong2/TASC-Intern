package com.product_service.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.product_service.dto.res.CategoryDetailDTO;
import com.product_service.dto.res.CategorySummaryDTO;
import com.product_service.enums.ProductStatus;
import com.product_service.model.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    @Query("""
            SELECT new com.product_service.dto.res.CategorySummaryDTO( c.id , c.name , i.url )
            FROM Category AS c LEFT JOIN c.image AS i
            WHERE c.status = ?1
            """)
    List<CategorySummaryDTO> findClientDTOByStatus(ProductStatus status);

    @Query("""
            SELECT new com.product_service.dto.res.CategoryDetailDTO ( c.id , c.name , i.url  , c.description )
            FROM Category AS c LEFT JOIN c.image AS i
            WHERE c.id = ?1 AND c.status = ?2
            """)
    Optional<CategoryDetailDTO> findClientDTOByIdAndStatus(long id, ProductStatus status);
}
