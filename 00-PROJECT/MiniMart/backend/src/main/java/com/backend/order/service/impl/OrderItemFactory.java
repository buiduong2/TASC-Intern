package com.backend.order.service.impl;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.backend.common.exception.ResourceNotFoundException;
import com.backend.common.utils.Utils;
import com.backend.order.dto.req.OrderItemReq;
import com.backend.order.model.Order;
import com.backend.order.model.OrderItem;
import com.backend.product.model.Product;
import com.backend.product.model.ProductStatus;
import com.backend.product.repository.ProductRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderItemFactory {
    private final ProductRepository productRepository;

    public List<OrderItem> create(Order order, Collection<OrderItemReq> itemReqs) {
        List<Long> productIds = itemReqs.stream().map(OrderItemReq::getProductId).toList();
        List<Product> products = productRepository.findByIdInAndStatus(productIds, ProductStatus.ACTIVE);

        if (products.size() != productIds.size()) {
            throw new ResourceNotFoundException("Some products not found: " + productIds);
        }

        Map<Long, Product> map = products.stream()
                .collect(Collectors.toMap(Product::getId, Function.identity()));

        return itemReqs.stream().map(dto -> {
            Product product = map.get(dto.getProductId());
            OrderItem item = new OrderItem();
            item.setOrder(order);
            item.setProduct(product);
            item.setQuantity(dto.getQuantity());
            item.setUnitPrice(Utils.coalesce(product.getSalePrice(), product.getCompareAtPrice()));
            return item;
        }).toList();
    }
}
