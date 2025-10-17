package com.inventory_service.saga.handler;

import org.springframework.stereotype.Component;

import com.common_kafka.event.catalog.product.ProductValidationPassedEvent;
import com.inventory_service.service.StockService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ProductValidationHandler {

    private final StockService stockService;

    public void handleValidationPassedEvent(ProductValidationPassedEvent event) {
        stockService.processProductValidationEvent(event);
    }
}
