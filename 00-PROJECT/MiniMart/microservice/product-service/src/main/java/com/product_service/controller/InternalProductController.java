package com.product_service.controller;

import java.util.List;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.common_kafka.event.catalog.product.ProductValidationFailedEvent;
import com.common_kafka.event.catalog.product.ProductValidationPassedEvent;
import com.common_kafka.event.sales.order.OrderProductValidationRequestedEvent;
import com.common_kafka.event.shared.dto.ValidatedItemSnapshot;
import com.product_service.dto.req.ProductCheckExistsReq;
import com.product_service.dto.res.ProductCheckExistsRes;
import com.product_service.dto.res.ProductValidationResult;
import com.product_service.model.Product;
import com.product_service.saga.utils.ProductSagaUtils;
import com.product_service.service.ProductCoreService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/internal/products")
@RequiredArgsConstructor
public class InternalProductController {

    private final ProductCoreService service;

    private final ProductSagaUtils utils;

    @PostMapping("/validate-existence")
    public ProductCheckExistsRes checkProductExsitsByIds(@Valid @RequestBody ProductCheckExistsReq req) {
        return service.validateExistsByIdsForInternal(req);
    }

    @PostMapping("/order-validation")
    public ResponseEntity<?> validateOrder(@Valid @RequestBody OrderProductValidationRequestedEvent req) {

        try {
            ProductValidationResult result = service.processOrderCreationRequested(req);
            List<Product> validProducts = result.getValidProducts();

            if (result.isAllValid()) {
                Set<ValidatedItemSnapshot> validatedItemSnapshots = utils.getValidatedItemSnapshots(req,
                        result.getValidProducts());

                ProductValidationPassedEvent passedEvent = new ProductValidationPassedEvent(
                        req.getOrderId(),
                        req.getUserId(),
                        validatedItemSnapshots);
                return ResponseEntity
                        .status(HttpStatus.OK)
                        .body(passedEvent);
            } else {

                Set<Long> failedProdutIds = utils.get(req, validProducts);

                ProductValidationFailedEvent failedEvent = new ProductValidationFailedEvent(
                        req.getOrderId(),
                        req.getUserId(),
                        "Some product is missing",
                        failedProdutIds);

                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(failedEvent);
            }

        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build();
        }

    }

}
