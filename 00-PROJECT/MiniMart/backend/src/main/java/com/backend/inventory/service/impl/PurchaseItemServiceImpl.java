package com.backend.inventory.service.impl;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.backend.common.exception.ConflictException;
import com.backend.common.exception.ResourceNotFoundException;
import com.backend.common.exception.ValidationException;
import com.backend.common.utils.ErrorCode;
import com.backend.inventory.dto.event.PurchaseItemUpdateEvent;
import com.backend.inventory.dto.req.AdjustmentPurchaseItemReq;
import com.backend.inventory.dto.req.ReturnedPurchaseItemReq;
import com.backend.inventory.dto.req.UpdatePurchaseItemReq;
import com.backend.inventory.dto.res.PurchaseItemDTO;
import com.backend.inventory.mapper.PurchaseItemMapper;
import com.backend.inventory.model.PurchaseItem;
import com.backend.inventory.repository.PurchaseItemRepository;
import com.backend.inventory.repository.StockAllocationRepository;
import com.backend.inventory.service.PurchaseItemService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PurchaseItemServiceImpl implements PurchaseItemService {

    private final PurchaseItemRepository repository;

    private final ApplicationEventPublisher publisher;

    private final PurchaseItemMapper mapper;

    private final StockAllocationRepository stockAllocationRepository;

    @Transactional
    @Override
    public PurchaseItemDTO update(long id, UpdatePurchaseItemReq req) {
        PurchaseItem purchaseItem = repository.findByIdForUpdate(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.PURCHASE_ITEM_NOT_FOUND.format(id)));
        Double newCostPrice = req.getCostPrice();
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
        publisher.publishEvent(new PurchaseItemUpdateEvent(purchaseItem.getProduct().getId()));

        return mapper.toDTO(purchaseItem);
    }

    @Transactional
    @Override
    public PurchaseItemDTO adjustment(long id, AdjustmentPurchaseItemReq req) {
        PurchaseItem purchaseItem = repository.findByIdForUpdate(id)
                .orElseThrow(() -> new ResourceNotFoundException("Purchase item with id = " + id + " was not found"));
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
        publisher.publishEvent(new PurchaseItemUpdateEvent(purchaseItem.getProduct().getId()));

        return mapper.toDTO(purchaseItem);
    }

    @Transactional
    @Override
    public PurchaseItemDTO returns(long id, ReturnedPurchaseItemReq req) {
        PurchaseItem purchaseItem = repository.findByIdForUpdate(id)
                .orElseThrow(() -> new ResourceNotFoundException("Purchase item id = " + id + " is not found"));
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
        publisher.publishEvent(new PurchaseItemUpdateEvent(purchaseItem.getProduct().getId()));

        return mapper.toDTO(purchaseItem);
    }

    @Transactional
    @Override
    public void delete(long id) {
        PurchaseItem purchaseItem = repository.findByIdForUpdate(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.PURCHASE_ITEM_NOT_FOUND.format(id)));

        if (stockAllocationRepository.existsByPurchaseItemId(id)) {
            throw new ConflictException(ErrorCode.PURCHASE_ITEM_DELETE_HAS_ALLOCATIONS.format(id));
        }

        if (purchaseItem.getQuantity() != purchaseItem.getRemainingQuantity()) {
            throw new ConflictException(ErrorCode.PURCHASE_ITEM_DELETE_CONFLICT.format(id));
        }

        repository.delete(purchaseItem);
        publisher.publishEvent(new PurchaseItemUpdateEvent(purchaseItem.getProduct().getId()));
    }

}
