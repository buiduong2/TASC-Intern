package com.backend.product.repository;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import com.backend.product.model.Product;

@Repository
@Primary
public interface TestProductRepository extends ProductRepository {

    default List<Product> findRandomProductsInMemory(int size) {
        List<Product> all = findAll();
        Collections.shuffle(all);
        return all.stream().limit(size).collect(Collectors.toList());
    }
}
