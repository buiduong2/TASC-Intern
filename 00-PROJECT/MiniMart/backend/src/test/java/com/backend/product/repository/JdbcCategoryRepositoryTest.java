package com.backend.product.repository;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

import com.backend.common.config.JpaConfig;
import com.backend.common.model.Audit;
import com.backend.inventory.utils.PurchaseOrderConverter;
import com.backend.product.model.Category;
import com.backend.product.model.CategoryImage;
import com.backend.product.model.ProductStatus;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Import({ PurchaseOrderConverter.class, JpaConfig.class, JdbcCategoryRepository.class })
public class JdbcCategoryRepositoryTest {

    @Autowired
    private JdbcCategoryRepository jdbcCategoryRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void testDeleteById() {
        // Given:
        Category category = new Category();
        category.setName("delete-me");
        category.setDescription("delete-me");
        category = categoryRepository.save(category);

        entityManager.flush();
        entityManager.clear();

        assertThat(categoryRepository.existsById(category.getId())).isTrue();

        // When:
        jdbcCategoryRepository.deleteById(category.getId());
        entityManager.flush();
        entityManager.clear();

        // Then:
        assertThat(categoryRepository.existsById(category.getId())).isFalse();
    }

    @Test
    void testFindById() {
        // Given:
        Category category = new Category();
        category.setName("find-me");
        category.setDescription("find-me");
        category = categoryRepository.save(category);

        entityManager.flush();
        entityManager.clear();

        // When:
        Category actual = jdbcCategoryRepository.findById(category.getId()).orElseThrow();
        Category expected = categoryRepository.findById(category.getId()).orElseThrow();

        // Then:
        assertThat(actual.getId()).isEqualTo(expected.getId());
        assertThat(actual.getName()).isEqualTo(expected.getName());
        assertThat(actual.getDescription()).isEqualTo(expected.getDescription());
        assertThat(actual.getAudit().getCreatedAt()).isEqualTo(expected.getAudit().getCreatedAt());
    }

    @Test
    void testCreateCategory_WithCategoryImage() {
        // Given:
        CategoryImage image = new CategoryImage();
        image.setPublicId("public-123");
        image = entityManager.persistFlushFind(image);

        Category category = new Category();
        category.setName("cat-with-image");
        category.setDescription("category with image");
        category.setStatus(ProductStatus.ACTIVE);
        category.setImage(image);

        // When
        category = jdbcCategoryRepository.save(category);
        entityManager.flush();
        entityManager.clear();

        // Then
        Category actual = categoryRepository.findById(category.getId()).orElseThrow();

        assertThat(actual.getId()).isNotNull();
        assertThat(actual.getName()).isEqualTo("cat-with-image");
        assertThat(actual.getImage()).isNotNull();
        assertThat(actual.getImage().getId()).isEqualTo(image.getId());
        assertThat(actual.getImage().getPublicId()).isEqualTo("public-123");

        Audit audit = actual.getAudit();
        assertThat(audit).isNotNull();
        assertThat(audit.getCreatedAt()).isNotNull();
    }

    @Test
    void testUpdateCategory_ChangeCategoryImage() {
        // Given:
        CategoryImage oldImage = new CategoryImage();
        oldImage.setPublicId("old-public");
        oldImage = entityManager.persistFlushFind(oldImage);

        CategoryImage newImage = new CategoryImage();
        newImage.setPublicId("new-public");
        newImage = entityManager.persistFlushFind(newImage);

        Category category = new Category();
        category.setName("cat-image");
        category.setDescription("with old image");
        category.setStatus(ProductStatus.DRAFT);
        category.setImage(oldImage);
        category = categoryRepository.save(category);

        entityManager.flush();
        entityManager.clear();

        long categoryId = category.getId();

        // When:
        category.setImage(newImage);
        category.setName("updated-cat");
        jdbcCategoryRepository.save(category);

        entityManager.flush();
        entityManager.clear();

        // Then
        Category actual = categoryRepository.findById(categoryId).orElseThrow();

        assertThat(actual.getName()).isEqualTo("updated-cat");
        assertThat(actual.getImage()).isNotNull();
        assertThat(actual.getImage().getId()).isEqualTo(newImage.getId());
        assertThat(actual.getImage().getPublicId()).isEqualTo("new-public");

        Audit audit = actual.getAudit();
        assertThat(audit.getUpdatedAt()).isNotNull();
    }
}
