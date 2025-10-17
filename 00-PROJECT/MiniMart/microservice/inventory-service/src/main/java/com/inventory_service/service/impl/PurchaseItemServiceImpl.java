package com.inventory_service.service.impl;

import java.math.BigDecimal;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.common.exception.GenericException;
import com.common.exception.ValidationException;
import com.inventory_service.dto.req.AdjustmentPurchaseItemReq;
import com.inventory_service.dto.req.ReturnedPurchaseItemReq;
import com.inventory_service.dto.req.UpdatePurchaseItemReq;
import com.inventory_service.dto.res.PurchaseItemDTO;
import com.inventory_service.event.PurchaseItemDeleteEvent;
import com.inventory_service.event.PurchaseItemUpdateEvent;
import com.inventory_service.exception.ErrorCode;
import com.inventory_service.mapper.PurchaseItemMapper;
import com.inventory_service.model.PurchaseItem;
import com.inventory_service.repository.AllocationItemRepository;
import com.inventory_service.repository.PurchaseItemRepository;
import com.inventory_service.service.PurchaseItemService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PurchaseItemServiceImpl implements PurchaseItemService {

    private final PurchaseItemRepository repository;

    private final AllocationItemRepository stockAllocationRepository;

    private final PurchaseItemMapper mapper;

    private final ApplicationEventPublisher eventPublisher;

    @Override
    public Page<PurchaseItemDTO> findByPurchaseId(long id, Pageable pageable) {
        return repository.findByPurchaseId(id, pageable);
    }

    @Override
    public PurchaseItemDTO update(long id, UpdatePurchaseItemReq req) {
        PurchaseItem purchaseItem = repository.findByIdForUpdate(id)
                .orElseThrow(() -> new GenericException(ErrorCode.PURCHASE_ITEM_NOT_FOUND, id));

        BigDecimal newCostPrice = req.getCostPrice();
        Integer newQuantity = req.getQuantity();

        if (newCostPrice != null && !newCostPrice.equals(purchaseItem.getCostPrice())) {
            purchaseItem.setCostPrice(newCostPrice);
        }

        if (newQuantity != null && !newQuantity.equals(purchaseItem.getQuantity())) {
            int delta = newQuantity - purchaseItem.getQuantity();
            purchaseItem.setQuantity(newQuantity);
            purchaseItem.setRemainingQuantity(delta + purchaseItem.getRemainingQuantity());
            if (purchaseItem.getRemainingQuantity() < 0) {
                throw new ValidationException("quantity",
                        "New quantity cannot be less than the number of items already used");
            }
        }

        repository.save(purchaseItem);

        eventPublisher.publishEvent(new PurchaseItemUpdateEvent(purchaseItem));

        return mapper.toDTO(purchaseItem);
    }

    @Override
    public PurchaseItemDTO adjustment(long id, AdjustmentPurchaseItemReq req) {
        PurchaseItem purchaseItem = repository.findByIdForUpdate(id)
                .orElseThrow(() -> new GenericException(ErrorCode.PURCHASE_ITEM_NOT_FOUND, id));
        int newRemainingQuantity = purchaseItem.getRemainingQuantity() + req.getDeltaRemainingQuantity();
        if (newRemainingQuantity < 0) {
            throw new ValidationException("deltaRemainingQuantity",
                    "Resulting remaining quantity cannot be negative");
        }
        if (newRemainingQuantity > purchaseItem.getQuantity()) {
            throw new ValidationException("deltaRemainingQuantity",
                    "Remaining quantity cannot exceed the total purchased quantity. Consider creating another PurchaseItem");
        }

        purchaseItem.setRemainingQuantity(newRemainingQuantity);

        repository.save(purchaseItem);
        eventPublisher.publishEvent(new PurchaseItemUpdateEvent(purchaseItem));

        return mapper.toDTO(purchaseItem);
    }

    @Override
    public PurchaseItemDTO returns(long id, ReturnedPurchaseItemReq req) {
        PurchaseItem purchaseItem = repository.findByIdForUpdate(id)
                .orElseThrow(() -> new GenericException(ErrorCode.PURCHASE_ITEM_NOT_FOUND, id));
        int newQuantity = purchaseItem.getQuantity() - req.getReturnQuantity();
        int newRemainingQuantity = purchaseItem.getRemainingQuantity() - req.getReturnQuantity();
        if (newQuantity < 0) {
            throw new ValidationException("returnQuantity",
                    "Return quantity cannot exceed the total purchased quantity");

        }

        if (newRemainingQuantity < 0) {
            throw new ValidationException("returnQuantity",
                    "Return quantity cannot exceed the remaining stock");
        }

        purchaseItem.setQuantity(newQuantity);
        purchaseItem.setRemainingQuantity(newRemainingQuantity);

        repository.save(purchaseItem);

        eventPublisher.publishEvent(new PurchaseItemUpdateEvent(purchaseItem));

        return mapper.toDTO(purchaseItem);
    }

    @Override
    public void delete(long id) {
        PurchaseItem purchaseItem = repository.findByIdForUpdate(id)
                .orElseThrow(() -> new GenericException(ErrorCode.PURCHASE_ITEM_NOT_FOUND, id));
        if (stockAllocationRepository.existsByPurchaseItemId(id)) {
            throw new GenericException(ErrorCode.PURCHASE_ITEM_DELETE_HAS_ALLOCATIONS, id);
        }

        if (purchaseItem.getQuantity() != purchaseItem.getRemainingQuantity()) {
            throw new GenericException(ErrorCode.PURCHASE_ITEM_DELETE_CONFLICT, id);
        }
        repository.delete(purchaseItem);

        eventPublisher.publishEvent(new PurchaseItemDeleteEvent(purchaseItem));
    }

}
