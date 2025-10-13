package com.inventory_service.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.util.ReflectionTestUtils;

import com.common.exception.GenericException;
import com.inventory_service.client.ProductClient;
import com.inventory_service.dto.req.CreatePurchaseReq;
import com.inventory_service.dto.req.ProductCheckExistsReq;
import com.inventory_service.dto.req.PurchaseItemReq;
import com.inventory_service.dto.req.UpdateStatusPurchaseReq;
import com.inventory_service.dto.res.ProductCheckExistsRes;
import com.inventory_service.dto.res.PurchaseItemDTO;
import com.inventory_service.dto.res.PurchaseSummaryDTO;
import com.inventory_service.enums.PurchaseStatus;
import com.inventory_service.event.PurchaseActiveEvent;
import com.inventory_service.mapper.PurchaseItemMapper;
import com.inventory_service.mapper.PurchaseMapper;
import com.inventory_service.mapper.PurchaseMapperImpl;
import com.inventory_service.model.Purchase;
import com.inventory_service.model.PurchaseItem;
import com.inventory_service.repository.CustomPurchaseRepositoryImpl;
import com.inventory_service.repository.PurchaseItemRepository;
import com.inventory_service.repository.PurchaseRepository;
import com.inventory_service.repository.StockAllocationRepository;
import com.inventory_service.utils.PurchaseOrderConverter;

@DataJpaTest
@ActiveProfiles("test")
@Sql({
        "/purchase-sample.sql",
})
@Import({ PurchaseOrderConverter.class, CustomPurchaseRepositoryImpl.class })
public class PurchaseServiceImplTest {

    private PurchaseServiceImpl purchaseService;

    @Autowired
    private StockAllocationRepository stockAllocationRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    private PurchaseRepository purchaseRepository;

    @Autowired
    private PurchaseItemRepository purchaseItemRepository;

    @Autowired
    private TestEntityManager em;

    @Spy
    private static PurchaseMapper mapper = Mappers.getMapper(PurchaseMapper.class);

    @Spy
    private static PurchaseItemMapper itemMapper = Mappers.getMapper(PurchaseItemMapper.class);

    @Mock
    private ProductClient productClient;

    private CreatePurchaseReq req;

    @BeforeAll
    static void setupBean() throws Exception {
        itemMapper = Mappers.getMapper(PurchaseItemMapper.class);
        PurchaseMapperImpl impl = (PurchaseMapperImpl) Mappers.getMapper(PurchaseMapper.class);
        ReflectionTestUtils.setField(impl, "purchaseItemMapper", itemMapper);
        mapper = spy(impl);
    }

    @BeforeEach
    public void setup() {
        req = new CreatePurchaseReq();
        req.setSupplier("ACME Supplier Ltd.");

        PurchaseItemReq item1 = new PurchaseItemReq();
        item1.setProductId(101L);
        item1.setQuantity(50);
        item1.setCostPrice(new BigDecimal("12.75"));

        PurchaseItemReq item2 = new PurchaseItemReq();
        item2.setProductId(202L);
        item2.setQuantity(30);
        item2.setCostPrice(new BigDecimal("9.50"));

        req.setPurchaseItems(List.of(item1, item2));

        purchaseService = new PurchaseServiceImpl(productClient, purchaseRepository, stockAllocationRepository,
                purchaseItemRepository, mapper, itemMapper, eventPublisher);
    }

    @Test
    void testCreate() {

        when(productClient.checkProductExsitsByIds(any()))
                .thenReturn(new ProductCheckExistsRes(true, Collections.emptySet()));

        PurchaseSummaryDTO result = purchaseService.create(req);

        assertThat(result).isNotNull();

        assertThat(result.getId()).isNotNull();
        assertThat(result.getSupplier()).isEqualTo(req.getSupplier());
        assertThat(result.getTotalQuantity()).isEqualTo(
                req.getPurchaseItems().stream().map(PurchaseItemReq::getQuantity).reduce(0, (a, b) -> a + b));

        em.flush();
        em.clear();

        Purchase actual = purchaseRepository.findById(result.getId()).orElseThrow();
        assertThat(actual).isNotNull();
        assertThat(actual.getPurchaseItems())
                .isNotEmpty()
                .hasSize(req.getPurchaseItems().size());

    }

    @Captor
    ArgumentCaptor<ProductCheckExistsReq> productCheckExistsReq;

    @Test
    void givenDraft_WhenUpdateActive_ThenMustUpdate() {
        // Given
        Purchase draftPurchase = getDraftPurchase();

        UpdateStatusPurchaseReq req = new UpdateStatusPurchaseReq();
        req.setStatus(PurchaseStatus.ACTIVE.toString());

        List<Long> productIds = draftPurchase.getPurchaseItems().stream().map(PurchaseItem::getProductId).toList();
        when(productClient.checkProductExsitsByIds(any()))
                .thenAnswer(ans -> new ProductCheckExistsRes(true, Collections.emptySet()));

        em.flush();
        em.clear();
        // then
        PurchaseSummaryDTO result = purchaseService.updateStatus(draftPurchase.getId(), req);
        verify(productClient, times(1)).checkProductExsitsByIds(productCheckExistsReq.capture());
        verify(eventPublisher, times(1)).publishEvent(any(PurchaseActiveEvent.class));

        em.flush();
        em.clear();

        // Actual mút active
        Purchase actual = purchaseRepository.findById(draftPurchase.getId()).orElseThrow();
        assertThat(actual).isNotNull();
        assertThat(actual.getStatus()).isEqualTo(PurchaseStatus.ACTIVE);
        assertThat(result.getStatus()).isEqualTo(PurchaseStatus.ACTIVE);

        // Call Client with productIds
        ProductCheckExistsReq clientReq = productCheckExistsReq.getValue();
        assertThat(clientReq.getProductIds()).isEqualTo(productIds);
    }

    private Purchase getDraftPurchase() {
        Purchase draftPurchase = purchaseRepository.findById(1000L).orElseThrow();
        assertThat(draftPurchase).isNotNull();
        assertThat(draftPurchase.getStatus()).isEqualTo(PurchaseStatus.DRAFT);
        return draftPurchase;
    }

    @Test
    void givenNonDraftAndAllocated_WhenUpdatePeingDelete_MustThrown() {

        // Given
        Purchase draftPurchase = getNonDraftAllocatedPurchase();

        UpdateStatusPurchaseReq req = new UpdateStatusPurchaseReq();
        req.setStatus(PurchaseStatus.PENDING_DELETION.toString());

        em.flush();
        em.clear();
        // Then
        assertThrows(GenericException.class, () -> purchaseService.updateStatus(draftPurchase.getId(), req));

        verify(productClient, times(0));

    }

    private Purchase getNonDraftAllocatedPurchase() {
        Purchase draftPurchase = purchaseRepository.findById(1001L).orElseThrow();
        assertThat(draftPurchase).isNotNull();
        assertThat(draftPurchase.getStatus()).isEqualTo(PurchaseStatus.ACTIVE);
        return draftPurchase;
    }

    @Test
    void givenDraft_thenDelete_mustDeleteAndNoEffect() {
        Purchase purchase = getDraftPurchase();

        em.flush();
        em.clear();

        purchaseService.deleteById(purchase.getId());

        verify(eventPublisher, times(0));

        em.flush();
        em.clear();

        assertThat(purchaseRepository.existsById(purchase.getId())).isFalse();

        Page<PurchaseItemDTO> purchaseItems = purchaseItemRepository.findByPurchaseId(purchase.getId(),
                PageRequest.ofSize(100));

        assertThat(purchaseItems.getContent().isEmpty()).isTrue();

    }

}
