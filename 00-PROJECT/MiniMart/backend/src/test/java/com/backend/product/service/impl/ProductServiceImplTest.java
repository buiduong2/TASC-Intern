package com.backend.product.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.HashSet;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;

import com.backend.inventory.repository.CustomPurchaseRepositoryImpl;
import com.backend.inventory.service.StockService;
import com.backend.inventory.service.impl.StockServiceImpl;
import com.backend.inventory.utils.PurchaseOrderConverter;
import com.backend.product.dto.event.ProductCreatedEvent;
import com.backend.product.dto.event.ProductDeleteEvent;
import com.backend.product.dto.req.ProductUpdateReq;
import com.backend.product.dto.res.ProductDTO;
import com.backend.product.mapper.ProductMapperImpl;
import com.backend.product.model.Category;
import com.backend.product.model.Product;
import com.backend.product.model.ProductStatus;
import com.backend.product.model.Tag;
import com.backend.product.repository.ProductRepository;
import com.backend.product.repository.TestCategoryRepository;
import com.backend.product.repository.TestTagRepository;
import com.backend.product.service.ProductService;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Import({ ProductServiceImpl.class, ProductMapperImpl.class, CustomPurchaseRepositoryImpl.class,
        PurchaseOrderConverter.class, StockServiceImpl.class })
public class ProductServiceImplTest {

    @Autowired
    private ProductService productService;

    @MockitoBean
    private ApplicationEventPublisher publisher;

    @Autowired
    private StockService stockService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private TestTagRepository tagRepository;

    @Autowired
    private TestCategoryRepository categoryRepository;

    @Autowired
    private TestEntityManager em;

    @BeforeEach
    void setup() {
        ReflectionTestUtils.setField(productService, "eventPublisher", publisher);

    }

    @Test
    void testFindByCategoryId() {
        Page<ProductDTO> pageDtos = productService.findByCategoryId(1L, PageRequest.ofSize(5));

        assertThat(pageDtos).isNotNull();
        assertThat(pageDtos.getContent()).isNotEmpty();

        assertThat(pageDtos.getContent())
                .allMatch(p -> p.getId() > 0)
                .allMatch(p -> p.getName() != null)
                .allMatch(p -> p.getCompareAtPrice() > 0)
                .anyMatch(p -> p.getSalePrice() != null);
    }

    @Test
    void testCreate() {

        // Given

        Tag tag = new Tag();
        tag.setName("fake-tag-1");

        Tag tag2 = new Tag();
        tag2.setName("fake-tag-2");

        Category category = new Category();
        category.setName("Fake-cat-1");

        em.persist(category);
        em.persist(tag);
        em.persist(tag2);
        em.flush();
        em.clear();

        assertThat(tag.getId()).isNotNull();
        assertThat(tag2.getId()).isNotNull();
        assertThat(category.getId()).isNotNull();

        ProductUpdateReq req = new ProductUpdateReq();
        req.setName("hello world");
        req.setDescription("Description");
        req.setSalePrice(100d);
        req.setCompareAtPrice(80d);
        req.setTagIds(new HashSet<>(List.of(tag.getId(), tag2.getId())));
        req.setCategoryId(category.getId());
        req.setStatus(ProductStatus.ACTIVE.name());

        // Then
        ProductDTO dto = productService.create(req);

        verify(publisher, times(1)).publishEvent(any(ProductCreatedEvent.class));

        em.flush();
        em.clear();

        //
        assertThat(dto.getId()).isNotNull();

        Product product = productRepository.findById(dto.getId()).orElseThrow();

        assertThat(dto.getCompareAtPrice())
                .isEqualTo(req.getCompareAtPrice())
                .isEqualTo(product.getCompareAtPrice());
        assertThat(dto.getSalePrice())
                .isEqualTo(req.getSalePrice())
                .isEqualTo(product.getSalePrice());
        assertThat(dto.getStock())
                .isEqualTo(0);
        assertThat(dto.getName())
                .isEqualTo(req.getName())
                .isEqualTo(product.getName());

    }

    @Test
    void testUpdate() {
        // given
        Product product = productRepository.findById(1L).get();

        assertThat(product).isNotNull();
        assertThat(product.getTags()).hasSizeGreaterThan(1);
        assertThat(product.getCategory().getId()).isNotNull();

        ProductUpdateReq req = new ProductUpdateReq();
        req.setName("random Name");
        req.setDescription("random description");
        req.setSalePrice(product.getSalePrice() == null ? 100d : product.getSalePrice() + 1d);
        req.setCompareAtPrice(product.getCompareAtPrice() + 1d);

        List<Tag> tags = tagRepository.findRandomProductsInMemory(3,
                new HashSet<>(product.getTags().stream().map(Tag::getId).toList()));

        req.setTagIds(new HashSet<>(tags.stream().map(Tag::getId).toList()));

        List<Category> cats = categoryRepository.findRandomProductsInMemory(1,
                new HashSet<>(List.of(product.getCategory().getId())));

        req.setCategoryId(cats.get(0).getId());

        em.flush();
        em.clear();
        // Then
        ProductDTO dto = productService.update(product.getId(), req);

        em.flush();
        em.clear();

        // Should
        Product actual = productRepository.findById(product.getId()).get();

        assertThat(actual).isNotNull();
        assertThat(dto).isNotNull();
        assertThat(dto.getId())
                .isEqualTo(actual.getId())
                .isEqualTo(product.getId());
        assertThat(dto.getCompareAtPrice())
                .isEqualTo(req.getCompareAtPrice())
                .isEqualTo(actual.getCompareAtPrice())
                .isNotEqualTo(product.getCompareAtPrice());
        assertThat(dto.getSalePrice())
                .isEqualTo(req.getSalePrice())
                .isEqualTo(actual.getSalePrice())
                .isNotEqualTo(product.getSalePrice());

        assertThat(dto.getName())
                .isEqualTo(req.getName())
                .isEqualTo(actual.getName())
                .isNotEqualTo(product.getName());

        assertThat(new HashSet<>(actual.getTags().stream().toList()))
                .containsAnyElementsOf(actual.getTags())
                .doesNotContainAnyElementsOf(product.getTags());
        assertThat(actual.getCategory().getId()).isNotEqualTo(product.getCategory().getId());

    }

    @Test
    void testDeleteById() {
        // Given
        doAnswer(invocation -> {
            ProductCreatedEvent event = invocation.getArgument(0);
            stockService.create(event.getProductId());
            return null;
        }).when(publisher).publishEvent(any(ProductCreatedEvent.class));
        doAnswer(invocation -> {
            ProductDeleteEvent event = invocation.getArgument(0);
            stockService.deleteByProductId(event.getProductId());
            return null;
        }).when(publisher).publishEvent(any(ProductDeleteEvent.class));

        Tag tag = new Tag();
        tag.setName("fake-tag-1");

        Tag tag2 = new Tag();
        tag2.setName("fake-tag-2");

        Category category = new Category();
        category.setName("Fake-cat-1");

        em.persist(category);
        em.persist(tag);
        em.persist(tag2);
        em.flush();
        em.clear();

        assertThat(tag.getId()).isNotNull();
        assertThat(tag2.getId()).isNotNull();
        assertThat(category.getId()).isNotNull();

        ProductUpdateReq req = new ProductUpdateReq();
        req.setName("hello world");
        req.setDescription("Description");
        req.setSalePrice(100d);
        req.setCompareAtPrice(80d);
        req.setTagIds(new HashSet<>(List.of(tag.getId(), tag2.getId())));
        req.setCategoryId(category.getId());
        req.setStatus(ProductStatus.ACTIVE.name());

        // Then
        ProductDTO dto = productService.create(req);

        verify(publisher, times(1)).publishEvent(any(ProductCreatedEvent.class));

        em.flush();
        em.clear();

        // Given
        long deletedId = dto.getId();

        productService.deleteById(deletedId);
        em.flush();
        em.clear();
        assertThat(productRepository.existsById(deletedId)).isFalse();
    }
}
