package com.inventory_service.service.impl;

import java.util.List;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.common.exception.GenericException;
import com.common.exception.ValidationException;
import com.inventory_service.client.ProductClient;
import com.inventory_service.dto.req.CreatePurchaseReq;
import com.inventory_service.dto.req.ProductCheckExistsReq;
import com.inventory_service.dto.req.PurchaseItemReq;
import com.inventory_service.dto.req.UpdatePurchaseReq;
import com.inventory_service.dto.req.UpdateStatusPurchaseReq;
import com.inventory_service.dto.res.ProductCheckExistsRes;
import com.inventory_service.dto.res.PurchaseDetailDTO;
import com.inventory_service.dto.res.PurchaseItemDTO;
import com.inventory_service.dto.res.PurchaseSummaryDTO;
import com.inventory_service.enums.PurchaseStatus;
import com.inventory_service.event.PurchaseActiveEvent;
import com.inventory_service.event.PurchaseAddedItemEvent;
import com.inventory_service.event.PurchaseArchivedEvent;
import com.inventory_service.event.PurchaseDeletedEvent;
import com.inventory_service.exception.ErrorCode;
import com.inventory_service.mapper.PurchaseItemMapper;
import com.inventory_service.mapper.PurchaseMapper;
import com.inventory_service.model.Purchase;
import com.inventory_service.model.PurchaseItem;
import com.inventory_service.repository.PurchaseItemRepository;
import com.inventory_service.repository.PurchaseRepository;
import com.inventory_service.repository.StockAllocationRepository;
import com.inventory_service.service.PurchaseService;
import com.inventory_service.utils.ErrorDetails;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PurchaseServiceImpl implements PurchaseService {

    private final ProductClient productClient;

    private final PurchaseRepository repository;

    private final StockAllocationRepository stockAllocationRepository;

    private final PurchaseItemRepository itemRepository;

    private final PurchaseMapper mapper;

    private final PurchaseItemMapper itemMapper;

    private final ApplicationEventPublisher eventPublisher;

    @Override
    public Page<PurchaseSummaryDTO> findPage(Pageable pageable) {
        return repository.findAdminAll(pageable);
    }

    @Override
    public PurchaseDetailDTO findDetailById(long id) {
        return repository.findAdminById(id)
                .orElseThrow(() -> new GenericException(ErrorCode.PURCHASE_NOT_FOUND, id));
    }

    @Transactional
    @Override
    public PurchaseSummaryDTO create(CreatePurchaseReq req) {
        List<Long> productIds = req.getPurchaseItems().stream().map(PurchaseItemReq::getProductId).toList();
        ProductCheckExistsRes res = productClient.checkProductExsitsByIds(new ProductCheckExistsReq(productIds));

        if (!res.isValid()) {
            throw new ValidationException(ErrorDetails.productIdstoErrorDetails(res.getMissingIds()));
        }

        Purchase entity = mapper.toEntity(req);
        entity = repository.save(entity);
        itemRepository.saveAll(entity.getPurchaseItems());
        entity.setStatus(PurchaseStatus.DRAFT);

        return mapper.toSummaryDTO(entity);
    }

    @Transactional(timeout = 10)
    @Override
    public PurchaseSummaryDTO updateStatus(long id, UpdateStatusPurchaseReq req) {

        Purchase purchase = repository.findWithItemByIdAndLock(id)
                .orElseThrow(() -> new GenericException(ErrorCode.PURCHASE_NOT_FOUND, id));
        PurchaseStatus newStatus = PurchaseStatus.valueOf(req.getStatus());
        PurchaseStatus oldStatus = purchase.getStatus();
        if (newStatus == oldStatus) {
            throw new ValidationException("status", "New status must not equal old Status");
        }

        List<Long> productIds = purchase.getPurchaseItems().stream().map(PurchaseItem::getProductId).toList();

        purchase.setStatus(newStatus);
        repository.save(purchase);

        if (oldStatus == PurchaseStatus.DRAFT) {
            confirmUpdateActive(purchase, productIds);
        } else if (newStatus == PurchaseStatus.PENDING_DELETION) {
            cofirmUpdatePendingDelete(purchase);
        }

        if (newStatus == PurchaseStatus.ACTIVE) {
            eventPublisher.publishEvent(new PurchaseActiveEvent(purchase));
        } else {
            eventPublisher.publishEvent(new PurchaseArchivedEvent(purchase));
        }

        return mapper.toSummaryDTO(purchase);
    }

    private void confirmUpdateActive(Purchase purchase, List<Long> productIds) {
        if (purchase.getStatus() != PurchaseStatus.ACTIVE) {
            throw new ValidationException("status", " Current Status is DRAFT . Can only update to ACTIVE");
        }

        ProductCheckExistsRes res = productClient.checkProductExsitsByIds(new ProductCheckExistsReq(productIds));

        if (!res.isValid()) {
            throw new ValidationException(ErrorDetails.productIdstoErrorDetails(productIds));
        }
    }

    private void cofirmUpdatePendingDelete(Purchase purchase) {
        validateNoReference(purchase.getId());
        validateNoItemAllocated(purchase);
    }

    @Transactional
    @Override
    public PurchaseSummaryDTO update(long id, UpdatePurchaseReq req) {
        Purchase purchase = repository.findForSummaryById(id)
                .orElseThrow(() -> new GenericException(ErrorCode.PURCHASE_NOT_FOUND, id));

        purchase.setSupplier(req.getSupplier());
        repository.save(purchase);
        return mapper.toSummaryDTO(purchase);
    }

    @Transactional(timeout = 10)
    @Override
    public void deleteById(Long id) {
        Purchase purchase = repository.findWithItemByIdAndLock(id)
                .orElseThrow(() -> new GenericException(ErrorCode.PURCHASE_NOT_FOUND, id));

        if (purchase.getStatus() == PurchaseStatus.DRAFT) {
            draftDelete(purchase);
            return;
        }

        if (purchase.getStatus() != PurchaseStatus.PENDING_DELETION) {
            hardDelete(purchase);
            return;
        }
        throw new GenericException(ErrorCode.PURCHASE_DELETE_STATUS_CONLICT, id);

    }

    private void draftDelete(Purchase purchase) {

        repository.delete(purchase);
    }

    private void hardDelete(Purchase purchase) {
        long purchaseId = purchase.getId();
        validateNoReference(purchaseId);
        validateNoItemAllocated(purchase);

        repository.delete(purchase);

        List<Long> purchaseItemIds = purchase.getPurchaseItems().stream().map(PurchaseItem::getProductId).toList();
        eventPublisher.publishEvent(new PurchaseDeletedEvent(purchaseId, purchaseItemIds));
    }

    private void validateNoItemAllocated(Purchase purchase) {
        for (PurchaseItem purchaseItem : purchase.getPurchaseItems()) {
            if (purchaseItem.getQuantity() != purchaseItem.getRemainingQuantity()) {
                throw new GenericException(ErrorCode.PURCHASE_ITEM_DELETE_CONFLICT, purchaseItem.getId());
            }
        }
    }

    private void validateNoReference(long purchaseId) {
        List<Long> allocatedPurchaseItemIds = stockAllocationRepository
                .getAllocationPurchaseItemIdByPurchaseId(purchaseId);

        if (!allocatedPurchaseItemIds.isEmpty()) {
            throw new GenericException(ErrorCode.PURCHASE_ITEM_DELETE_HAS_ALLOCATIONS, allocatedPurchaseItemIds);
        }
    }

    @Transactional(timeout = 10)
    @Override
    public PurchaseItemDTO createItem(long id, PurchaseItemReq req) {
        Purchase purchase = repository.findWithItemByIdAndLock(id)
                .orElseThrow(() -> new GenericException(ErrorCode.PURCHASE_NOT_FOUND, id));
        if (purchase.getStatus() != PurchaseStatus.DRAFT) {
            throw new GenericException(ErrorCode.PURCHASE_CREATE_ITEM_STATUS_CONLICT, id);
        }

        long newProductId = req.getProductId();

        if (purchase.getPurchaseItems().stream().anyMatch(i -> i.getProductId() == newProductId)) {
            throw new GenericException(ErrorCode.PURCHASE_CREATE_ITEM_PRODUCT_CONLICT, id);
        }

        ProductCheckExistsRes res = productClient
                .checkProductExsitsByIds(new ProductCheckExistsReq(List.of(newProductId)));
        if (!res.isValid()) {
            new ValidationException(ErrorDetails.productIdstoErrorDetails(res.getMissingIds()));
        }

        PurchaseItem purchaseItem = itemMapper.toEntity(req);

        purchase.addPurchaseItem(purchaseItem);

        repository.save(purchase);
        itemRepository.save(purchaseItem);

        eventPublisher.publishEvent(new PurchaseAddedItemEvent(purchase, purchaseItem));

        return itemMapper.toDTO(purchaseItem);
    }

}
