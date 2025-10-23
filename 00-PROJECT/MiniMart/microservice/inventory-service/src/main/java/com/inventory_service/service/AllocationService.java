package com.inventory_service.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.common_kafka.event.sales.order.OrderCreationCompensatedEvent;
import com.common_kafka.event.sales.order.OrderStockAllocationRequestedEvent;
import com.common_kafka.event.shared.res.SagaResult;
import com.inventory_service.dto.req.StockAllocationFilter;
import com.inventory_service.dto.res.StockAllocationResult;
import com.inventory_service.dto.res.StockAllocationSummaryDTO;
import com.inventory_service.model.Allocation;

public interface AllocationService {

    Page<StockAllocationSummaryDTO> findAll(StockAllocationFilter filter, Pageable pageable);

    SagaResult<StockAllocationResult> processOrderStockAllocationRequest(OrderStockAllocationRequestedEvent event);

    Allocation processOrderCreationCompensated(OrderCreationCompensatedEvent event);

}
