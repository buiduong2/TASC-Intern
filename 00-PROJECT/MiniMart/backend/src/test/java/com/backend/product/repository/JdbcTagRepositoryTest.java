package com.backend.product.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;

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
import com.backend.order.model.Order_;
import com.backend.product.dto.req.TagFilter;
import com.backend.product.dto.res.TagAdminDTO;
import com.backend.product.model.Tag;
import com.backend.product.model.Tag_;
import com.backend.utils.TestUtils;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Import({ PurchaseOrderConverter.class, JpaConfig.class, JdbcTagRepository.class })
public class JdbcTagRepositoryTest {

    @Autowired
    private JdbcTagRepository jdbcTagRepository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void testDeleteById() {
        Tag tag = new Tag();

        tag.setDescription("Abc");
        tag.setName("Abc");
        tagRepository.save(tag);

        entityManager.flush();
        entityManager.clear();

        assertThat(tagRepository.existsById(tag.getId())).isTrue();

        jdbcTagRepository.deleteById(tag.getId());
        entityManager.flush();
        entityManager.clear();
        assertThat(tagRepository.existsById(tag.getId())).isFalse();
    }

    @Test
    void testDeleteById_WithProduct_tags() {

        assertThat(tagRepository.existsById(1L)).isTrue();

        jdbcTagRepository.deleteById(1L);
        entityManager.flush();
        entityManager.clear();
        assertThat(tagRepository.existsById(1L)).isFalse();
    }

    @Test
    void testFindById() {
        Tag tag = new Tag();

        tag.setDescription("Abc");
        tag.setName("Abc");
        tagRepository.save(tag);

        entityManager.flush();
        entityManager.clear();

        assertThat(tagRepository.existsById(tag.getId())).isTrue();

        Tag actual = jdbcTagRepository.findById(tag.getId()).orElseThrow();
        Tag expected = tagRepository.findById(tag.getId()).orElseThrow();

        assertThat(actual.getId()).isEqualTo(expected.getId());
        assertThat(actual.getName()).isEqualTo(expected.getName());
        assertThat(actual.getDescription()).isEqualTo(expected.getDescription());
        assertThat(actual.getAudit().getCreatedAt()).isEqualTo(expected.getAudit().getCreatedAt());
    }

    @Test
    void testCreateTag() {
        // GIven
        Tag tag = new Tag();
        tag.setDescription("test");
        tag.setName("test");

        // When:
        tag = jdbcTagRepository.save(tag);
        entityManager.flush();
        entityManager.clear();

        // then
        Tag actual = tagRepository.findById(tag.getId()).orElseThrow();

        assertThat(actual.getId()).isNotNull();
        assertThat(actual.getName()).isEqualTo("test");
        assertThat(actual.getDescription()).isEqualTo("test");

        Audit audit = actual.getAudit();
        assertThat(audit).isNotNull();
        assertThat(audit.getCreatedAt()).isNotNull();
        assertThat(audit.getUpdatedAt()).isNull();
    }

    @Test
    void testUpdateTag() {
        // Given
        Tag tag = new Tag();
        tag.setDescription("test");
        tag.setName("test");
        tag = tagRepository.save(tag);
        entityManager.flush();
        entityManager.clear();

        long countBefore = tagRepository.count();
        long tagId = tag.getId();

        // WHen
        tag.setName("test-test");
        tag.setDescription("test-test");
        jdbcTagRepository.save(tag);
        entityManager.flush();
        entityManager.clear();

        // Then
        Tag actual = tagRepository.findById(tagId).orElseThrow();

        assertThat(tagRepository.count()).isEqualTo(countBefore);
        assertThat(actual.getId()).isEqualTo(tagId);
        assertThat(actual.getName()).isEqualTo("test-test");
        assertThat(actual.getDescription()).isEqualTo("test-test");

        Audit audit = actual.getAudit();
        assertThat(audit).isNotNull();
        assertThat(audit.getCreatedAt()).isNotNull();
        assertThat(audit.getUpdatedAt()).isNotNull();
        assertThat(audit.getUpdatedAt()).isAfter(audit.getCreatedAt());
    }

    @Test
    void testFindAllAdmin() {
        List<Tag> tags = tagRepository.findAll();
        tags.forEach(System.out::println);
    }

    @Test
    void testFindAllAdmin_DefaultPaging_ReturnsFirstPage() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Tag_.ID).descending());

        Page<TagAdminDTO> pageAdmin = jdbcTagRepository.findAllAdmin(new TagFilter(), pageable);
        Page<Tag> pageTags = tagRepository.findAll(pageable);

        assertThat(pageAdmin.getContent()).isNotEmpty();
        assertThat(pageAdmin.getSort().isSorted()).isTrue();
        assertThat(pageAdmin.getNumberOfElements()).isEqualTo(pageTags.getNumberOfElements());
        assertThat(pageAdmin.getTotalElements()).isEqualTo(pageTags.getTotalElements());
        assertThat(pageAdmin.getNumber()).isEqualTo(pageTags.getNumber());
        assertThat(pageAdmin.getContent()).hasSize(pageTags.getContent().size());

        List<TagAdminDTO> dtos = pageAdmin.getContent();
        List<Tag> entites = pageTags.getContent();
        for (int i = 0; i < dtos.size(); i++) {
            assertThat(entites.get(i).getId()).isEqualTo(dtos.get(i).getId());
        }
    }

    // WHERE
    @Test
    void testFindAllAdmin_FilterByCreatedFrom() {
        // GIVEN
        List<Tag> tags = tagRepository.findAll();
        Tag randomTag = TestUtils.getRandomElement(tags);
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Order_.ID).descending());
        TagFilter filter = new TagFilter();
        filter.setCreatedFrom(randomTag.getAudit().getCreatedAt());

        assertThat(tags).isNotEmpty();
        assertThat(randomTag.getAudit().getCreatedAt()).isNotNull();

        // When
        Page<TagAdminDTO> page = jdbcTagRepository.findAllAdmin(filter, pageable);

        // Then
        List<Tag> filtered = tags.stream()
                .filter(t -> t.getAudit() != null)
                .filter(t -> t.getAudit().getCreatedAt() != null)
                .filter(t -> {
                    LocalDateTime createdAt = t.getAudit().getCreatedAt();
                    LocalDateTime createdFrom = randomTag.getAudit().getCreatedAt();
                    return createdAt.isEqual(createdFrom) || createdAt.isAfter(createdFrom);
                })
                .sorted(Comparator.comparing(Tag::getId).reversed())

                .toList();
        List<Tag> expected = filtered.stream()
                .skip(pageable.getOffset())
                .limit(pageable.getPageSize())
                .toList();
        long totalCount = filtered.size();

        assertThat(page.getNumber()).isEqualTo(pageable.getPageNumber());
        assertThat(page.getTotalElements()).isEqualTo(totalCount);
        assertThat(page.getSort().isSorted()).isTrue();

        List<TagAdminDTO> dtos = page.getContent();
        for (int i = 0; i < expected.size(); i++) {
            assertThat(dtos.get(i).getId()).isEqualTo(expected.get(i).getId());
        }

    }

    @Test
    void testFindAllAdmin_FilterByUpdatedFrom() {
        // GIVEN
        List<Tag> tags = tagRepository.findAll();
        Tag randomTag = TestUtils.getRandomElement(tags);
        LocalDateTime updatedFrom = randomTag.getAudit().getUpdatedAt();
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Order_.ID).descending());
        TagFilter filter = new TagFilter();
        filter.setUpdatedFrom(updatedFrom);

        assertThat(tags).isNotEmpty();
        assertThat(randomTag.getAudit().getCreatedAt()).isNotNull();

        // When
        Page<TagAdminDTO> page = jdbcTagRepository.findAllAdmin(filter, pageable);

        // Then
        List<Tag> filtered = tags.stream()
                .filter(t -> t.getAudit() != null)
                .filter(t -> t.getAudit().getUpdatedAt() != null)
                .filter(t -> {
                    LocalDateTime updatedAt = t.getAudit().getUpdatedAt();
                    return updatedAt.isEqual(updatedFrom) || updatedAt.isAfter(updatedFrom);
                })
                .sorted(Comparator.comparing(Tag::getId).reversed())

                .toList();
        List<Tag> expected = filtered.stream()
                .skip(pageable.getOffset())
                .limit(pageable.getPageSize())
                .toList();
        long totalCount = filtered.size();

        assertThat(page.getNumber()).isEqualTo(pageable.getPageNumber());
        assertThat(page.getTotalElements()).isEqualTo(totalCount);
        assertThat(page.getSort().isSorted()).isTrue();

        List<TagAdminDTO> dtos = page.getContent();
        for (int i = 0; i < expected.size(); i++) {
            assertThat(dtos.get(i).getId()).isEqualTo(expected.get(i).getId());
        }
    }

    @Test
    void testFindAllAdmin_FilterByNameLike() {
        // GIVEN
        List<Tag> tags = tagRepository.findAll();
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Order_.ID).descending());
        TagFilter filter = new TagFilter();
        filter.setName("a");

        assertThat(tags).isNotEmpty();

        // When
        Page<TagAdminDTO> page = jdbcTagRepository.findAllAdmin(filter, pageable);

        // Then
        List<Tag> filtered = tags.stream()
                .filter(t -> t.getName() != null)
                .filter(t -> t.getName().contains(filter.getName()))
                .sorted(Comparator.comparing(Tag::getId).reversed())
                .toList();
        List<Tag> expected = filtered.stream()
                .skip(pageable.getOffset())
                .limit(pageable.getPageSize())
                .toList();
        long totalCount = filtered.size();

        assertThat(page.getNumber()).isEqualTo(pageable.getPageNumber());
        assertThat(page.getTotalElements()).isEqualTo(totalCount);
        assertThat(page.getSort().isSorted()).isTrue();
        assertThat(expected).isNotEmpty();

        List<TagAdminDTO> dtos = page.getContent();
        for (int i = 0; i < expected.size(); i++) {
            assertThat(dtos.get(i).getId()).isEqualTo(expected.get(i).getId());
        }
    }

    // @Test
    void testFindAllAdmin_FilterByMultipleWhereConditions() {

    }

    // HAVING
    void testFindAllAdmin_FilterByMinProductCount() {

    }

    void testFindAllAdmin_FilterByMaxProductCount() {

    }

    void testFindAllAdmin_FilterByProductCountRange() {

    }

    // ORder BY
    void testFindAllAdmin_SortByCreatedAtDesc() {

    }

    void testFindAllAdmin_SortByNameAsc() {

    }

    void testFindAllAdmin_SortByMultipleFields() {

    }

    // Ket hop
    @Test
    void testFindAllAdmin_FilterAndSortTogether() {
        // Given
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS");
        LocalDateTime dateTime = LocalDateTime.parse("2025-09-15 11:10:30.970383", formatter);

        TagFilter filter = new TagFilter();
        filter.setName("oil");
        filter.setMinProductCount(2L);
        filter.setMaxProductCount(5L);
        filter.setCreatedFrom(dateTime);
        filter.setUpdatedFrom(dateTime);

        Sort sort = Sort.by(
                Sort.Order.asc("name"),
                Sort.Order.desc("id"),
                Sort.Order.desc("createdAt"),
                Sort.Order.desc("updatedAt"));

        Pageable pageable = PageRequest.of(0, 10, sort);

        List<Tag> filtered = tagRepository.findAll().stream()
                .filter(t -> t.getAudit() != null)
                .filter(t -> t.getAudit().getCreatedAt() != null)
                .filter(t -> t.getAudit().getUpdatedAt() != null)
                .filter(t -> TestUtils.afterOrEqual(t.getAudit().getCreatedAt(), dateTime))
                .filter(t -> TestUtils.afterOrEqual(t.getAudit().getUpdatedAt(), dateTime))
                .filter(t -> t.getName().contains(filter.getName()))
                .filter(t -> t.getProducts().size() >= filter.getMinProductCount())
                .filter(t -> t.getProducts().size() <= filter.getMaxProductCount())
                .toList();

        long totalCount = filtered.stream().count();
        List<Tag> expected = filtered.stream()
                .sorted(
                        Comparator
                                .comparing(Tag::getName, Comparator.nullsLast(String::compareTo))
                                .thenComparing(Comparator.comparing(Tag::getId, Comparator.reverseOrder()))
                                .thenComparing(
                                        Comparator.comparing(t -> t.getAudit().getCreatedAt(),
                                                Comparator.reverseOrder()))
                                .thenComparing(
                                        Comparator.comparing(t -> t.getAudit().getUpdatedAt(),
                                                Comparator.reverseOrder())))
                .skip(pageable.getOffset())
                .limit(pageable.getPageSize())
                .toList();

        // When
        Page<TagAdminDTO> page = jdbcTagRepository.findAllAdmin(filter, pageable);

        // Then

        assertThat(page.getNumber()).isEqualTo(pageable.getPageNumber());
        assertThat(page.getTotalElements()).isEqualTo(totalCount);
        assertThat(page.getSort().isSorted()).isTrue();
        assertThat(expected).isNotEmpty();

        List<TagAdminDTO> dtos = page.getContent();
        for (int i = 0; i < expected.size(); i++) {
            assertThat(dtos.get(i).getId()).isEqualTo(expected.get(i).getId());
        }

    }

    void testFindAllAdmin_WhenNoResult_ReturnsEmptyPage() {

    }

}
