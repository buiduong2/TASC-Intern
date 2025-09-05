package com.backend.inventory.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.backend.common.utils.EntityLookupHelper;
import com.backend.inventory.dto.event.PurchaseCreatedEvent;
import com.backend.inventory.dto.req.CreatePurchaseReq;
import com.backend.inventory.dto.res.PurchaseDTO;
import com.backend.inventory.mapper.PurchaseMapper;
import com.backend.inventory.model.Purchase;
import com.backend.inventory.model.PurchaseItem;
import com.backend.inventory.repository.PurchaseRepository;
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

    @Override
    @Transactional
    public PurchaseDTO create(CreatePurchaseReq req) {
        Purchase entity = mapper.toEntity(req);

        if (entity.getPurchaseItems() != null && entity.getPurchaseItems().size() > 0) {

            List<Long> productIds = entity.getPurchaseItems().stream().map(PurchaseItem::getProduct).map(Product::getId)
                    .toList();
            Map<Long, Product> mapProductById = EntityLookupHelper.findMapByIdIn(productRepository, productIds,
                    "Product");

            for (PurchaseItem item : entity.getPurchaseItems()) {
                item.setProduct(mapProductById.get(item.getProduct().getId()));
            }

        }
        repository.save(entity);

        publisher.publishEvent(new PurchaseCreatedEvent(entity.getId(), entity.getSupplier(), req.getItems()));

        return mapper.toDTO(entity);
    }

    @Override
    public Page<PurchaseDTO> findPage(Pageable pageable) {
        return repository.findAdminAll(pageable);
    }

}
