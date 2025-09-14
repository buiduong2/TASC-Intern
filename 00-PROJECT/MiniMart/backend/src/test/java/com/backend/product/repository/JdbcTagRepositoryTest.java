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
    void testSave() {

        // Test Create
        Tag tag = new Tag();
        tag.setDescription("test");
        tag.setName("test");

        tag = jdbcTagRepository.save(tag);

        entityManager.flush();
        entityManager.clear();

        Tag actual = tagRepository.findById(tag.getId()).orElseThrow();

        assertThat(actual.getDescription()).isEqualTo("test");
        assertThat(actual.getName()).isEqualTo("test");
        assertThat(actual.getId()).isNotNull();
        Audit audit = actual.getAudit();
        assertThat(audit).isNotNull();
        assertThat(audit.getCreatedAt()).isNotNull();
        assertThat(audit.getUpdatedAt()).isNull();

        entityManager.flush();
        entityManager.clear();

        // Test Update
        long currentCount = tagRepository.count();
        long currentId = tag.getId();
        tag.setName("test-test");
        tag.setDescription("test-test");

        jdbcTagRepository.save(tag);
        entityManager.flush();
        entityManager.clear();

        actual = tagRepository.findById(tag.getId()).orElseThrow();

        assertThat(currentCount).isEqualTo(tagRepository.count());
        assertThat(tag.getId()).isEqualTo(currentId);
        assertThat(actual.getDescription()).isEqualTo("test-test");
        assertThat(actual.getName()).isEqualTo("test-test");
        assertThat(actual.getId()).isNotNull();
        audit = actual.getAudit();
        assertThat(audit).isNotNull();
        assertThat(audit.getCreatedAt()).isNotNull();
        assertThat(audit.getUpdatedAt()).isNotNull();
        assertThat(actual.getAudit().getUpdatedAt()).isAfter(actual.getAudit().getCreatedAt());

    }
}
