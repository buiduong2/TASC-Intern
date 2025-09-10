package com.backend.inventory.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.util.ReflectionTestUtils;

import com.backend.common.exception.ResourceNotFoundException;
import com.backend.common.utils.EntityLookupHelper;
import com.backend.inventory.dto.event.PurchaseCreatedEvent;
import com.backend.inventory.dto.req.CreatePurchaseReq;
import com.backend.inventory.dto.req.PurchaseItemReq;
import com.backend.inventory.mapper.PurchaseItemMapper;
import com.backend.inventory.mapper.PurchaseMapper;
import com.backend.inventory.mapper.PurchaseMapperImpl;
import com.backend.inventory.model.Purchase;
import com.backend.inventory.repository.PurchaseRepository;
import com.backend.product.repository.ProductRepository;

@ExtendWith(MockitoExtension.class)
public class PurchaseServiceUnitTest {

    private CreatePurchaseReq req;

    @Mock
    private PurchaseRepository repository;

    @Mock
    private ProductRepository productRepository;

    @Spy
    private static PurchaseMapper mapper = Mappers.getMapper(PurchaseMapper.class);

    @Spy
    private static PurchaseItemMapper itemMapper = Mappers.getMapper(PurchaseItemMapper.class);

    @Mock
    private ApplicationEventPublisher publisher;

    @Spy
    private EntityLookupHelper entityLookupHelper;

    @InjectMocks
    private PurchaseServiceImpl purchaseService;

    @Captor
    ArgumentCaptor<Purchase> purchaseCaptor;

    @Captor
    ArgumentCaptor<PurchaseCreatedEvent> eventCaptore;

    @BeforeAll
    static void setupBean() throws Exception {
        itemMapper = Mappers.getMapper(PurchaseItemMapper.class);
        PurchaseMapperImpl impl = (PurchaseMapperImpl) Mappers.getMapper(PurchaseMapper.class);
        ReflectionTestUtils.setField(impl, "purchaseItemMapper", itemMapper);
        mapper = spy(impl);
    }

    @BeforeEach
    void setup() {
        PurchaseItemReq item1 = new PurchaseItemReq();
        item1.setProductId(1L);
        item1.setQuantity(10);
        item1.setCostPrice(1000.0);

        PurchaseItemReq item2 = new PurchaseItemReq();
        item2.setProductId(2L);
        item2.setQuantity(5);
        item2.setCostPrice(2000.0);

        req = new CreatePurchaseReq();
        req.setSupplier("ABC Corp");
        req.setItems(List.of(item1, item2));
    }

    /**
     * Mock. trả về Empty hoặc size() khác với purchaseItem() throw
     * 
     * Và nó phải gọi
     */
    @Test
    void create_givenInvalidProduct_shouldThrowResourceNotFound() {
        List<Long> productIds = req.getItems().stream().map(i -> i.getProductId()).toList();

        when(productRepository.findAllById(productIds)).thenReturn(Collections.emptyList());

        assertThatThrownBy(() -> purchaseService.create(req))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(productRepository, times(1)).findAllById(eq(productIds));

    }

    /**
     * Mock: Check remaining Quantity = quantity. lúc vừa tạo
     */
    @Test
    void create_givenValidRequest_shouldMapRemainingQuantityEqualToQuantity() {
        doReturn(Collections.emptyMap())
                .when(entityLookupHelper).findMapByIdIn(any(), any(), any());

        purchaseService.create(req);

        verify(repository).save(purchaseCaptor.capture());
        verify(mapper, times(1)).toEntity(req);

        Purchase purchase = purchaseCaptor.getValue();

        Assertions.assertThat(purchase.getPurchaseItems())
                .allMatch(pi -> pi.getQuantity() == pi.getRemainingQuantity());
    }

    /**
     * Bắn event khi Success
     */
    @Test
    void create_givenValidRequest_shouldPublishPurchaseCreatedEvent() {
        doReturn(Collections.emptyMap())
                .when(entityLookupHelper).findMapByIdIn(any(), any(), any());

        doAnswer(invocation -> {
            Purchase purchase = invocation.getArgument(0);
            purchase.setId(1L);
            return purchase;
        }).when(repository).save(any());
        purchaseService.create(req);
        verify(publisher, times(1)).publishEvent(eventCaptore.capture());

        PurchaseCreatedEvent event = eventCaptore.getValue();

        assertThat(event.getPurchaseId()).isEqualTo(1L);
        assertThat(event.getProductIds()).isEqualTo(req.getItems().stream().map(i -> i.getProductId()).toList());
    }

    @Test
    void create_givenInValidRequest_shouldNotPublishPurchaseCreatedEvent() {
        List<Long> productIds = req.getItems().stream().map(i -> i.getProductId()).toList();

        when(productRepository.findAllById(productIds)).thenReturn(Collections.emptyList());

        assertThatThrownBy(() -> purchaseService.create(req))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(publisher, times(0)).publishEvent(any());

    }

    /**
     * Phải gọi được repository.save()
     */
    @Test
    void createPurchase_withValidRequest_shouldCallRepositorySave() {
        doReturn(Collections.emptyMap())
                .when(entityLookupHelper).findMapByIdIn(any(), any(), any());

        purchaseService.create(req);
        verify(repository, times(1)).save(any());

    }

}
