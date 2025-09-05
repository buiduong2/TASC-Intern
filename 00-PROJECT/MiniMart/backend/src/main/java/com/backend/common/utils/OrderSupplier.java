package com.backend.common.utils;

import org.springframework.data.domain.Sort;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Root;

public interface OrderSupplier<E> extends TetraFunction<Sort.Order, Root<E>, CriteriaQuery<?>, CriteriaBuilder, Order> {

}
