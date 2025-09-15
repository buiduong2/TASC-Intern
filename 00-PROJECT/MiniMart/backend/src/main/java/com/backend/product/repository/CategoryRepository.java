package com.backend.product.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.EntityGraph.EntityGraphType;
import org.springframework.data.jpa.repository.JpaRepository;

import com.backend.product.model.Category;
import com.backend.product.model.ProductStatus;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    @EntityGraph(value = Category.NamedGraph_CategoryDTO, type = EntityGraphType.LOAD)
    List<Category> findDTOByStatus(ProductStatus status);

    @EntityGraph(value = Category.NamedGraph_CategoryDTO, type = EntityGraphType.LOAD)
    Optional<Category> findDetailDTOByIdAndStatus(long id, ProductStatus status);

}
