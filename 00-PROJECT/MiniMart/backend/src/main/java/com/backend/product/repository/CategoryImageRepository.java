package com.backend.product.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.backend.product.model.CategoryImage;

public interface CategoryImageRepository extends JpaRepository<CategoryImage, Long> {
    Optional<CategoryImage> findByPublicId(String publicId);

}
