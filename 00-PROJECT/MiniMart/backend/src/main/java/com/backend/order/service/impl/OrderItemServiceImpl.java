package com.backend.order.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.backend.common.exception.ResourceNotFoundException;
import com.backend.common.utils.Utils;
import com.backend.inventory.model.PurchaseItem;
import com.backend.inventory.model.StockAllocation;
import com.backend.inventory.model.StockAllocationStatus;
import com.backend.inventory.repository.PurchaseItemRepository;
import com.backend.inventory.repository.StockAllocationRepository;
import com.backend.order.dto.req.OrderItemReq;
import com.backend.order.exception.NotEnoughStockException;
import com.backend.order.model.Order;
import com.backend.order.model.OrderItem;
import com.backend.order.repository.OrderItemRepository;
import com.backend.order.service.OrderItemService;
import com.backend.product.model.Product;
import com.backend.product.model.ProductStatus;
import com.backend.product.repository.ProductRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderItemServiceImpl implements OrderItemService {

    private final ProductRepository productRepository;

    private final PurchaseItemRepository purchaseItemRepository;

    private final OrderItemRepository orderItemRepository;

    private final StockAllocationRepository stockAllocationRepository;

    @Transactional(propagation = Propagation.MANDATORY)
    @Override
    public List<OrderItem> create(Order order, Collection<OrderItemReq> dtos) {
        List<Long> productIds = dtos
                .stream()
                .map(OrderItemReq::getProductId)
                .toList();

        List<OrderItem> items = create(order, dtos, productIds);
        List<StockAllocation> stockAllocations = allocateStockForNewOrderItems(items, productIds);

        updateAvgOrderItems(items);
        orderItemRepository.saveAll(items);
        stockAllocationRepository.saveAll(stockAllocations);

        return items;
    }

    private List<OrderItem> create(Order order, Collection<OrderItemReq> dtos, List<Long> productIds) {
        List<Product> products = productRepository.findByIdInAndStatus(productIds, ProductStatus.ACTIVE);
        if (products.size() != productIds.size()) {
            throw new ResourceNotFoundException("Some of productId in " + productIds + " not found");
        }
        Map<Long, Product> mapProductById = products
                .stream()
                .collect(Collectors.toMap(Product::getId, Function.identity()));

        List<OrderItem> orderItems = new ArrayList<>();

        for (OrderItemReq dto : dtos) {
            Product product = mapProductById.get(dto.getProductId());

            OrderItem orderItem = new OrderItem();
            orderItems.add(orderItem);

            orderItem.setProduct(product);
            orderItem.setQuantity(dto.getQuantity());
            orderItem.setUnitPrice(Utils.coalesce(product.getSalePrice(), product.getCompareAtPrice()));

            orderItem.setOrder(order);

        }

        return orderItems;
    }

    /**
     * Nhạy cảm
     */
    private List<StockAllocation> allocateStockForNewOrderItems(List<OrderItem> orderItems,
            List<Long> productIds) {
        List<PurchaseItem> purchaseItems = purchaseItemRepository.findAvaiableByProductIdInForUpdate(productIds);

        Map<Long, List<PurchaseItem>> mapPurchaseItemByProductId = purchaseItems
                .stream()
                .collect(Collectors.groupingBy(p -> p.getProduct().getId(), Collectors.toList()));
        List<StockAllocation> dtos = new ArrayList<>();
        for (OrderItem orderItem : orderItems) {
            List<PurchaseItem> purchaseItemsByProductId = mapPurchaseItemByProductId
                    .getOrDefault(orderItem.getProduct().getId(), Collections.emptyList());
            var dto = allocateStockForNewOrderItem(orderItem, purchaseItemsByProductId);
            dtos.addAll(dto);
        }

        purchaseItemRepository.saveAll(purchaseItems);

        return dtos;
    }

    /**
     * Update PurchaseItem.remaining
     * 
     * INSERT StockAllocation
     */
    private List<StockAllocation> allocateStockForNewOrderItem(
            OrderItem orderItem,
            List<PurchaseItem> purchaseItems) {
        int remainingNeed = orderItem.getQuantity();
        List<StockAllocation> stockAllocations = new ArrayList<>();
        orderItem.setAllocations(stockAllocations);

        for (PurchaseItem purchaseItem : purchaseItems) {
            int availableQty = purchaseItem.getRemainingQuantity();
            int allocatedQty = Math.min(availableQty, remainingNeed);
            if (allocatedQty > 0) {

                // Create and save StockAllocation
                purchaseItem.setRemainingQuantity(purchaseItem.getRemainingQuantity() - allocatedQty);
                StockAllocation allocation = createStockAllocationForOrderItem(orderItem, purchaseItem,
                        allocatedQty);
                stockAllocations.add(allocation);

                remainingNeed -= allocatedQty;
                if (remainingNeed == 0) {
                    break;
                }
            }

        }

        if (remainingNeed > 0) {
            throw new NotEnoughStockException("Product id=" + orderItem.getProduct().getId() +
                    " is out of stock. Needed=" + orderItem.getQuantity() +
                    ", Allocated=" + (orderItem.getQuantity() - remainingNeed));
        }

        return stockAllocations;
    }

    private StockAllocation createStockAllocationForOrderItem(OrderItem orderItem, PurchaseItem purchaseItem,
            int allocatedQuantity) {

        StockAllocation stockAllocation = new StockAllocation();
        stockAllocation.setAllocatedQuantity(allocatedQuantity);
        stockAllocation.setOrderItem(orderItem);
        stockAllocation.setPurchaseItem(purchaseItem);

        return stockAllocation;
    }

    // avgCostPrice = Tổng (costPrice * quantity) / Tổng quantity
    private void updateAvgOrderItems(List<OrderItem> orderItems) {
        for (OrderItem orderItem : orderItems) {
            double totalCost = 0;
            double totalQuantity = 0;

            for (StockAllocation allocation : orderItem.getAllocations()) {
                totalCost += allocation.getPurchaseItem().getCostPrice() * allocation.getAllocatedQuantity();
                totalQuantity += allocation.getAllocatedQuantity();
            }

            double avgCostPrice = totalQuantity == 0 ? 0 : totalCost / totalQuantity;

            orderItem.setAvgCostPrice(avgCostPrice);
        }
    }

    @Transactional(propagation = Propagation.MANDATORY)
    @Override
    public void releaseStockAllocation(Order order) {
        List<StockAllocation> stockAllocations = stockAllocationRepository
                .findByOrderId(order.getId(), StockAllocationStatus.ACTIVE);

        for (StockAllocation allocation : stockAllocations) {
            int delta = allocation.getAllocatedQuantity();
            long purchaseItemId = allocation.getPurchaseItem().getId();
            int i = purchaseItemRepository.increaseRemainingQuantity(purchaseItemId, delta);
            allocation.setStatus(StockAllocationStatus.RELEASED);

            if (i != 1) {
                throw new RuntimeException("Something When Wrong, " + "missing PurchaseItem");
            }
        }
    }
}
