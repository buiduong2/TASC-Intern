package com.backend.product.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.backend.common.config.JpaConfig;
import com.backend.common.model.Audit;
import com.backend.inventory.utils.PurchaseOrderConverter;
import com.backend.product.dto.req.CategoryFilter;
import com.backend.product.dto.res.CategoryAdminDTO;
import com.backend.product.model.Category;
import com.backend.product.model.CategoryImage;
import com.backend.product.model.Category_;
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

    @Test
    void testFindAllAdmin_DefaultPaging_ReturnsFirstPage() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Category_.ID).descending());
        Page<CategoryAdminDTO> pageAdmin = jdbcCategoryRepository.findAllAdmin(new CategoryFilter(), pageable);

        Page<Category> pageActual = categoryRepository.findAll(pageable);

        assertThat(pageAdmin.getContent()).isNotEmpty();
        assertThat(pageAdmin.getSort().isSorted()).isTrue();
        assertThat(pageAdmin.getNumberOfElements()).isEqualTo(pageActual.getNumberOfElements());
        assertThat(pageAdmin.getTotalElements()).isEqualTo(pageActual.getTotalElements());
        assertThat(pageAdmin.getNumber()).isEqualTo(pageActual.getNumber());
        assertThat(pageAdmin.getContent()).hasSize(pageActual.getContent().size());

        List<CategoryAdminDTO> dtos = pageAdmin.getContent();
        List<Category> entites = pageActual.getContent();
        for (int i = 0; i < dtos.size(); i++) {
            assertThat(entites.get(i).getId()).isEqualTo(dtos.get(i).getId());
        }

    }

    @Test
    void testFindAllAdmin_FilterAndSortTogether() {
        // Given
        assertThat(categoryRepository.count()).isGreaterThanOrEqualTo(3);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS");

        LocalDateTime dateTime = LocalDateTime.parse("2025-09-15 11:10:30.869312", formatter);

        CategoryFilter filter = new CategoryFilter();
        filter.setName("e"); // lọc theo chữ cái 'o'
        filter.setStatus(ProductStatus.ACTIVE.name());
        filter.setCreatedFrom(dateTime);
        filter.setUpdatedFrom(dateTime);
        filter.setMinProductCount(5L);
        filter.setMaxProductCount(25L);

        Sort sort = Sort.by(
                Sort.Order.asc("name"),
                Sort.Order.desc("id"),
                Sort.Order.asc("createdAt"),
                Sort.Order.desc("updatedAt"),
                Sort.Order.desc("status"));

        Pageable pageable = PageRequest.of(0, 10, sort);

        Comparator<Category> catComparator = Comparator
                .comparing(Category::getName, Comparator.nullsLast(String::compareTo)) // name ASC
                .thenComparing(Comparator.comparing(Category::getId, Comparator.reverseOrder())) // id DESC
                .thenComparing(Comparator.comparing(c -> c.getAudit().getCreatedAt(), Comparator.reverseOrder())) // createdAt
                                                                                                                  // DESC
                .thenComparing(Comparator.comparing(c -> c.getAudit().getUpdatedAt(), Comparator.reverseOrder())) // updatedAt
                                                                                                                  // DESC
                .thenComparing(
                        Comparator.comparing(Category::getStatus, Comparator.nullsLast(Enum::compareTo)).reversed()); // status
                                                                                                                      // DESC

        Predicate<Category> p = c -> true;

        if (filter.getName() != null) {
            p = p.and(c -> c.getName() != null && c.getName().contains("e"));
        }

        if (filter.getStatus() != null) {
            p = p.and(c -> c.getStatus() != null && c.getStatus().name().equals(filter.getStatus()));
        }

        if (filter.getCreatedFrom() != null) {
            p = p.and(c -> c.getAudit() != null
                    && c.getAudit().getCreatedAt() != null
                    && !c.getAudit().getCreatedAt().isBefore(filter.getCreatedFrom()));
        }

        if (filter.getUpdatedFrom() != null) {
            p = p.and(c -> c.getAudit() != null
                    && c.getAudit().getUpdatedAt() != null
                    && !c.getAudit().getUpdatedAt().isBefore(filter.getUpdatedFrom()));
        }

        if (filter.getMinProductCount() != null) {
            p = p.and(c -> c.getProducts() != null
                    && c.getProducts().size() >= filter.getMinProductCount());
        }

        if (filter.getMaxProductCount() != null) {
            p = p.and(c -> c.getProducts() != null
                    && c.getProducts().size() <= filter.getMaxProductCount());
        }

        // WHEN
        Page<CategoryAdminDTO> page = jdbcCategoryRepository.findAllAdmin(filter, pageable);

        List<Category> filtered = categoryRepository.findAll()
                .stream()
                .filter(p)
                .sorted(catComparator)
                .toList();

        long totalCount = filtered.size();
        List<Category> expected = filtered.stream()
                .skip(pageable.getOffset())
                .limit(pageable.getPageSize())
                .toList();

        assertThat(expected).isNotEmpty();
        assertThat(page.getNumber()).isEqualTo(pageable.getPageNumber());
        assertThat(page.getTotalElements()).isEqualTo(totalCount);
        assertThat(page.getSort().isSorted()).isTrue();
        List<CategoryAdminDTO> dtos = page.getContent();
        for (int i = 0; i < dtos.size(); i++) {
            assertThat(expected.get(i).getId()).isEqualTo(dtos.get(i).getId());
        }

    }
}
