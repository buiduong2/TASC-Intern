package com.backend.inventory.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.backend.common.config.ServiceTest;
import com.backend.inventory.dto.event.PurchaseCreatedEvent;
import com.backend.inventory.dto.req.CreatePurchaseReq;
import com.backend.inventory.dto.req.PurchaseItemReq;
import com.backend.inventory.dto.res.PurchaseDTO;
import com.backend.inventory.model.Purchase;
import com.backend.inventory.model.PurchaseItem;
import com.backend.inventory.repository.PurchaseItemRepository;
import com.backend.inventory.repository.PurchaseRepository;
import com.backend.inventory.service.PurchaseService;
import com.backend.product.model.Product;
import com.backend.product.repository.TestProductRepository;
import com.github.javafaker.Faker;

import jakarta.persistence.EntityManager;

@ServiceTest
public class PurchaseServiceIntegrationTest {

    @Autowired
    private PurchaseService service;

    @Autowired
    private EntityManager em;

    @Autowired
    private TestProductRepository productRepository;

    @Autowired
    private PurchaseRepository repository;

    @Autowired
    private PurchaseItemRepository itemRepository;

    @Autowired
    @Qualifier("mockPublisher")
    private ApplicationEventPublisher publisher;

    /**
     * Sau khi save. DB phải có đủ Purchase và PurchaseItem
     */
    @Test
    void createPurchase_withValidRequest_shouldPersistPurchaseAndItems() {
        // Prepare Product
        List<Product> products = productRepository.findRandomProductsInMemory(3);

        em.flush();
        em.clear();

        // Prepare RequestDTO
        CreatePurchaseReq req = buildValidRequest(products);

        // Act
        PurchaseDTO dto = service.create(req);
        em.flush();
        em.clear();

        // Assert PurchaseDTO returned
        assertThat(dto.getCreatedAt()).isNotNull();
        assertThat(dto.getId()).isGreaterThan(0);
        assertThat(dto.getSupplier()).isNotNull();
        assertThat(dto.getTotalCostPrice()).isGreaterThan(0);
        assertThat(dto.getTotalQuantity()).isGreaterThan(0);

        // Assert persisted Purchase + Items
        Purchase result = repository.findById(dto.getId()).orElseThrow();

        assertThat(result).isNotNull();
        assertThat(result.getAudit().getCreatedAt()).isNotNull();
        assertThat(result.getId()).isEqualTo(dto.getId());
        assertThat(result.getSupplier()).isEqualTo(dto.getSupplier());
        assertThat(result.getPurchaseItems())
                .allSatisfy(item -> {
                    assertThat(item.getProduct()).isNotNull();
                    assertThat(item.getCostPrice()).isGreaterThan(0);
                    assertThat(item.getCreatedAt()).isNotNull();
                    assertThat(item.getQuantity()).isGreaterThan(0);
                    assertThat(item.getRemainingQuantity())
                            .isEqualTo(item.getQuantity());
                });

        // Assert Event
        verify(publisher, times(1))
                .publishEvent(Mockito.refEq(
                        new PurchaseCreatedEvent(result.getId(), result.getPurchaseItems().stream()
                                .map(PurchaseItem::getProduct).map(Product::getId).toList())));
    }

    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    void createPurchase_whenSaveFails_shouldRollback() {
        // Prepare Product
        List<Product> products = productRepository.findRandomProductsInMemory(3);

        // Prepare RequestDTO
        CreatePurchaseReq req = buildValidRequest(products);
        long beforePurchase = repository.count();
        long beforePurchaseItem = itemRepository.count();
        em.clear();

        // Stub
        doThrow(new RuntimeException("Mock")).when(publisher).publishEvent(isA(PurchaseCreatedEvent.class));

        // Act
        assertThrows(RuntimeException.class, () -> {
            service.create(req);
        }, "Mock");
        verify(publisher).publishEvent(isA(PurchaseCreatedEvent.class));

        em.clear();

        long afterPurchase = repository.count();
        long afterPurchaseItem = itemRepository.count();

        assertThat(beforePurchase)
                .as("Purchase should rollback")
                .isEqualTo(afterPurchase);
        assertThat(beforePurchaseItem)
                .as("PurchaseItem should rollback")
                .isEqualTo(afterPurchaseItem);

    }

    private CreatePurchaseReq buildValidRequest(List<Product> products) {
        Faker faker = new Faker();
        CreatePurchaseReq req = new CreatePurchaseReq();
        List<PurchaseItemReq> reqItems = new ArrayList<>();

        req.setSupplier("Supplier");
        req.setItems(reqItems);

        for (Product product : products) {
            PurchaseItemReq itemReq = new PurchaseItemReq();
            itemReq.setCostPrice(faker.number().randomDouble(2, 10, 100));
            itemReq.setQuantity(faker.random().nextInt(1, 5));
            itemReq.setProductId(product.getId());
            reqItems.add(itemReq);
        }
        return req;
    }

    @Test
    void testFindPage() {
        service.findPage(Pageable.ofSize(5));
    }
}
