package com.product_service.saga.command;

import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.common_kafka.config.KafkaTopics;
import com.common_kafka.event.sales.order.OrderProductValidationRequestedEvent;
import com.product_service.dto.res.ProductValidationResult;
import com.product_service.saga.ProductSagaManager;
import com.product_service.service.ProductCoreService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@KafkaListener(topics = KafkaTopics.CATALOG_PRODUCT_VALIDATION, groupId = "catalog-product-validation-group")
@Slf4j
public class ProductValidationCommandHandler {

    private final ProductCoreService coreService;

    private final ProductSagaManager sagaManager;

    @KafkaHandler
    public void handleOrderCreationRequestedEvent(OrderProductValidationRequestedEvent event) {
        final long orderId = event.getOrderId();
        final String SAGA = "ORDER_CREATE";
        log.info("🟢📥 [{}][OrderProductValidationRequestedEvent][RECEIVED][ID={}] Validate products for order", SAGA,
                orderId);

        try {
            ProductValidationResult result = coreService.processOrderCreationRequested(event);

            if (result.isAllValid()) {
                sagaManager.publishProductValidationPassedEvent(event, result.getValidProducts());
                log.info("📤 [{}][ProductValidationPassedEvent][PUBLISHED][ID={}] All products valid", SAGA, orderId);
            } else {
                sagaManager.publishProductValidationFailedEvent(event, result.getValidProducts());
                log.warn("🔴📤 [{}][ProductValidationFailedEvent][PUBLISHED][ID={}] Invalid products detected", SAGA,
                        orderId);
            }

        } catch (Exception e) {
            sagaManager.publishProductValidationFailedEvent(event);
            log.error("❌ [{}][OrderProductValidationRequestedEvent][ERROR][ID={}] {}", SAGA, orderId, e.getMessage(),
                    e);
            log.warn("🔴📤 [{}][ProductValidationFailedEvent][PUBLISHED][ID={}] Exception occurred during validation",
                    SAGA, orderId);
        }
    }

}
