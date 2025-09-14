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
import com.backend.product.model.Tag;

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

}
