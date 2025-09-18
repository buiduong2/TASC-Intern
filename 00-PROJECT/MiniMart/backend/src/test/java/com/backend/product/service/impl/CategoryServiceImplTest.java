package com.backend.product.service.impl;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import com.backend.common.config.JpaConfig;
import com.backend.inventory.repository.CustomPurchaseRepositoryImpl;
import com.backend.inventory.utils.PurchaseOrderConverter;
import com.backend.product.mapper.CategoryMapperImpl;
import com.backend.product.repository.CategoryRepository;
import com.backend.product.repository.JdbcCategoryRepository;
import com.backend.product.repository.ProductRepository;
import com.backend.product.service.CategoryService;

import jakarta.persistence.EntityManager;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Import({ JpaConfig.class, JdbcCategoryRepository.class, CustomPurchaseRepositoryImpl.class,
        PurchaseOrderConverter.class, CategoryMapperImpl.class, CategoryServiceImpl.class })
public class CategoryServiceImplTest {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private EntityManager em;

    @Test
    void testDeleteById() {

        long countCatBefore = categoryRepository.count();
        long countProductBefore = productRepository.count();

        assertThat(categoryRepository.existsById(1L)).isTrue();

        categoryService.deleteById(1L);

        em.flush();
        em.clear();

        assertThat(categoryRepository.existsById(1L)).isFalse();

        long countCatAfter = categoryRepository.count();
        long countProductAfter = productRepository.count();

        assertThat(countCatAfter).isEqualTo(countCatBefore - 1);
        assertThat(countProductAfter).isEqualTo(countProductBefore);
    }
}
