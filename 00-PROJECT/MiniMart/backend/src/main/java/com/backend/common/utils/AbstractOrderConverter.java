package com.backend.common.utils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.query.QueryUtils;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Root;

public abstract class AbstractOrderConverter<E> {
    private final Map<String, OrderSupplier<E>> suppliers = this.getSuppliers();

    abstract public Map<String, OrderSupplier<E>> getSuppliers();

    public List<Order> convert(List<Sort.Order> orders, Root<E> root, CriteriaQuery<?> query,
            CriteriaBuilder builder) {
        Queue<Integer> insertConvertIndex = new LinkedList<>();
        List<Sort.Order> convertAble = new ArrayList<>();
        List<Sort.Order> needConvert = new ArrayList<>();
        for (int i = 0; i < orders.size(); i++) {
            Sort.Order order = orders.get(i);
            if (suppliers.containsKey(order.getProperty())) {
                needConvert.add(order);
                insertConvertIndex.add(i);
            } else {
                convertAble.add(order);
            }
        }
        
        List<Order> resConvertAble = QueryUtils.toOrders(Sort.by(convertAble), root, builder);
        List<Order> resNeedConvert = needConvertToOrders(needConvert, root, query, builder);

        if (resNeedConvert.isEmpty()) {
            return resConvertAble;
        }

        return this.joinOrder(resConvertAble, resNeedConvert, insertConvertIndex);
    }

    private List<Order> needConvertToOrders(List<Sort.Order> orders, Root<E> root, CriteriaQuery<?> query,
            CriteriaBuilder builder) {
        return orders.stream()
                .map(order -> suppliers
                        .get(order.getProperty())
                        .apply(order, root, query, builder))
                .toList();
    }

    private List<Order> joinOrder(List<Order> orders, List<Order> neededConverts,
            Queue<Integer> insertPoints) {
        int totalSize = orders.size() + neededConverts.size();
        List<Order> result = new ArrayList<>(totalSize);

        Integer insertIndex = insertPoints.poll();
        int i = 0;
        int neededIndex = 0;
        while (result.size() < totalSize) {
            if (result.size() == insertIndex) {
                result.add(neededConverts.get(neededIndex));
                neededIndex++;
                insertIndex = insertPoints.poll();
            } else {
                result.add(orders.get(i));
                i++;
            }
        }

        return result;
    }
}
