package com.product_service.service.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.common_kafka.event.sales.order.OrderProductValidationRequestedEvent;
import com.common_kafka.event.shared.dto.OrderItemData;
import com.product_service.dto.req.ProductCheckExistsReq;
import com.product_service.dto.res.ProductCheckExistsRes;
import com.product_service.dto.res.ProductValidationResult;
import com.product_service.enums.ProductStatus;
import com.product_service.model.Product;
import com.product_service.repository.ProductRepository;
import com.product_service.service.ProductCoreService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductCoreServiceImpl implements ProductCoreService {

    private final ProductRepository repository;

    @Override
    public ProductCheckExistsRes validateExistsByIdsForInternal(ProductCheckExistsReq req) {
        List<Long> existsedProductIds = repository.getProductIdByIdnAndStatusIn(
                req.getProductIds(),
                ProductStatus.getTransactableStatuses());
        Set<Long> requiredIds = new HashSet<>(req.getProductIds());

        requiredIds.removeAll(existsedProductIds);

        return new ProductCheckExistsRes(requiredIds.isEmpty(), requiredIds);
    }

    @Override
    public ProductValidationResult processOrderCreationRequested(OrderProductValidationRequestedEvent event) {
        Set<Long> requestIds = event.getItems().stream().map(OrderItemData::getProductId)
                .collect(Collectors.toCollection(HashSet::new));

        List<Product> products = repository.findByIdInAndStatusIn(requestIds, List.of(ProductStatus.ACTIVE));
        boolean allValid = products.size() == requestIds.size();
        return ProductValidationResult.of(allValid, products);
    }

}
