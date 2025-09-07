package com.backend.inventory.service.impl;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Pageable;

import com.backend.common.config.ServiceTest;
import com.backend.inventory.dto.event.PurchaseCreatedEvent;
import com.backend.inventory.dto.req.CreatePurchaseReq;
import com.backend.inventory.dto.req.PurchaseItemReq;
import com.backend.inventory.dto.res.PurchaseDTO;
import com.backend.inventory.model.Purchase;
import com.backend.inventory.model.PurchaseItem;
import com.backend.inventory.repository.PurchaseRepository;
import com.backend.inventory.service.PurchaseService;
import com.backend.product.model.Product;
import com.backend.product.repository.TestProductRepository;
import com.github.javafaker.Faker;

import jakarta.persistence.EntityManager;

@ServiceTest
public class PurchaseServiceImplTest {

    @Autowired
    private PurchaseService service;

    @Autowired
    private EntityManager em;

    @Autowired
    private TestProductRepository productRepository;

    @Autowired
    private PurchaseRepository repository;

    @Autowired
    @Qualifier("mockPublisher")
    private ApplicationEventPublisher publisher;

    @Test
    void testCreate() {
        List<Product> products = productRepository.findRandomProductsInMemory(3);
        Faker faker = new Faker();
        em.flush();
        em.clear();

        CreatePurchaseReq req = new CreatePurchaseReq();
        List<PurchaseItemReq> reqItems = new ArrayList<>();

        req.setSupplier("Supplier");
        req.setItems(reqItems);

        for (Product product : products) {
            PurchaseItemReq itemReq = new PurchaseItemReq();
            itemReq.setCostPrice(faker.random().nextDouble());
            itemReq.setQuantity(faker.random().nextInt(1, 5));
            itemReq.setProductId(product.getId());
            reqItems.add(itemReq);
        }

        PurchaseDTO dto = service.create(req);
        em.flush();
        em.clear();

        // VALIDATED all VALue
        assertThat(dto.getCreatedAt()).isNotNull();
        assertThat(dto.getId()).isGreaterThan(0);
        assertThat(dto.getSupplier()).isNotNull();
        assertThat(dto.getTotalCostPrice()).isGreaterThan(0);
        assertThat(dto.getTotalQuantity()).isGreaterThan(0);

        Purchase result = repository.findById(dto.getId()).orElse(null);
        System.out.println(result.getAudit());
        assertThat(result).isNotNull();
        assertThat(result.getAudit().getCreatedAt()).isNotNull();
        assertThat(result.getId()).isEqualTo(dto.getId());
        assertThat(result.getSupplier()).isEqualTo(dto.getSupplier());
        assertThat(result.getPurchaseItems())
                .isNotEmpty()
                .allMatch(item -> item.getProduct() != null)
                .allMatch(item -> item.getCostPrice() > 0)
                .allMatch(item -> item.getCreatedAt() != null)
                .allMatch(item -> item.getQuantity() > 0)
                .allMatch(item -> item.getQuantity() > 0)
                .allMatch(item -> item.getRemainingQuantity() == item.getQuantity());

        Mockito.verify(publisher, Mockito.times(1))
                .publishEvent(new PurchaseCreatedEvent(result.getId(),
                        result.getPurchaseItems().stream()
                                .map(PurchaseItem::getProduct).map(Product::getId).distinct().toList()));
    }

    @Test
    void testFindPage() {
        service.findPage(Pageable.ofSize(5));
    }
}
