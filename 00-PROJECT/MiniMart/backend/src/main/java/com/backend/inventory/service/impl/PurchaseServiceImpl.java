package com.backend.inventory.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.backend.common.exception.ConflictException;
import com.backend.common.exception.ResourceNotFoundException;
import com.backend.common.utils.EntityLookupHelper;
import com.backend.common.utils.ErrorCode;
import com.backend.inventory.dto.event.PurchaseCreatedEvent;
import com.backend.inventory.dto.event.PurchaseDeleteEvent;
import com.backend.inventory.dto.req.CreatePurchaseReq;
import com.backend.inventory.dto.req.UpdatePurchaseReq;
import com.backend.inventory.dto.res.PurchaseDTO;
import com.backend.inventory.mapper.PurchaseMapper;
import com.backend.inventory.model.Purchase;
import com.backend.inventory.model.PurchaseItem;
import com.backend.inventory.repository.PurchaseRepository;
import com.backend.inventory.repository.StockAllocationRepository;
import com.backend.inventory.service.PurchaseService;
import com.backend.product.model.Product;
import com.backend.product.repository.ProductRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PurchaseServiceImpl implements PurchaseService {

    private final PurchaseRepository repository;

    private final ProductRepository productRepository;

    private final PurchaseMapper mapper;

    private final ApplicationEventPublisher publisher;

    private final StockAllocationRepository stockAllocationRepository;

    private final EntityLookupHelper helper;

    @Override
    @Transactional
    public PurchaseDTO create(CreatePurchaseReq req) {
        Purchase entity = mapper.toEntity(req);

        List<Long> productIds = entity.getPurchaseItems().stream().map(PurchaseItem::getProduct).map(Product::getId)
                .toList();
        saveItems(entity, productIds);

        repository.save(entity);
        publisher.publishEvent(new PurchaseCreatedEvent(entity.getId(), productIds));

        return mapper.toDTO(entity);
    }

    private void saveItems(Purchase entity, List<Long> productIds) {
        Map<Long, Product> mapProductById = helper.findMapByIdIn(productRepository, productIds,
                "Product");

        for (PurchaseItem item : entity.getPurchaseItems()) {
            item.setProduct(mapProductById.get(item.getProduct().getId()));
        }
    }

    @Override
    public Page<PurchaseDTO> findPage(Pageable pageable) {
        return repository.findAdminAll(pageable);
    }

    @Override
    public PurchaseDTO update(long id, UpdatePurchaseReq req) {
        Purchase purchase = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.PURCHASE_NOT_FOUND.format(id)));

        purchase.setSupplier(req.getSupplier());
        repository.save(purchase);

        int totalQuantity = 0;
        double totalCostPrice = 0;

        for (PurchaseItem purchaseItem : purchase.getPurchaseItems()) {
            totalQuantity += purchaseItem.getQuantity();
            totalCostPrice += purchaseItem.getCostPrice();
        }

        return new PurchaseDTO(id, purchase.getAudit().getCreatedAt(), purchase.getSupplier(), totalQuantity,
                totalCostPrice);
    }

    @Transactional
    @Override
    public void deleteById(Long id) {
        Purchase purchase = repository.findByIdForDelete(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.PURCHASE_NOT_FOUND.format(id)));

        List<Long> purchaseItemIds = stockAllocationRepository.findAllocatedPurchaseItemIdsByPurchaseId(id);
        if (!purchaseItemIds.isEmpty()) {
            throw new ConflictException(ErrorCode.PURCHASE_ITEM_DELETE_HAS_ALLOCATIONS.format(purchaseItemIds));
        }
        for (PurchaseItem purchaseItem : purchase.getPurchaseItems()) {
            if (purchaseItem.getQuantity() != purchaseItem.getRemainingQuantity()) {
                throw new ConflictException(ErrorCode.PURCHASE_ITEM_DELETE_CONFLICT.format(purchaseItem.getId()));
            }
        }

        repository.delete(purchase);

        publisher.publishEvent(new PurchaseDeleteEvent(purchase.getId(),
                purchase.getPurchaseItems()
                        .stream()
                        .map(PurchaseItem::getProduct)
                        .map(Product::getId)
                        .toList()));
    }

}
