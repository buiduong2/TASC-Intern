package com.inventory_service.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.inventory_service.dto.req.AdjustmentPurchaseItemReq;
import com.inventory_service.dto.req.ReturnedPurchaseItemReq;
import com.inventory_service.dto.req.UpdatePurchaseItemReq;
import com.inventory_service.dto.res.PurchaseItemDTO;
import com.inventory_service.mapper.PurchaseItemMapper;
import com.inventory_service.repository.PurchaseItemRepository;
import com.inventory_service.repository.PurchaseRepository;
import com.inventory_service.service.PurchaseItemService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PurchaseItemServiceImpl implements PurchaseItemService {

    private final PurchaseRepository purchaseRepository;

    private final PurchaseItemRepository itemRepository;

    private final PurchaseItemMapper itemMapper;

    @Override
    public Page<PurchaseItemDTO> findByPurchaseId(long id, Pageable pageable) {
        return itemRepository.findByPurchaseId(id, pageable);
    }

    @Override
    public PurchaseItemDTO update(long id, UpdatePurchaseItemReq req) {
        
        
        return null;
    }

    @Override
    public PurchaseItemDTO adjustment(long id, AdjustmentPurchaseItemReq req) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'adjustment'");
    }

    @Override
    public PurchaseItemDTO returns(long id, ReturnedPurchaseItemReq req) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'returns'");
    }

    @Override
    public void delete(long id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'delete'");
    }

}
