package com.backend.product.repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Primary;

import com.backend.product.model.Category;

@Primary
public interface TestCategoryRepository extends CategoryRepository {
    default List<Category> findRandomProductsInMemory(int size, Set<Long> excludeIds) {
        List<Category> all = findAll()
                .stream()
                .filter(tag -> !excludeIds.contains(tag.getId()))
                .toList();

        if (all.size() == 0) {
            throw new IllegalArgumentException("Tag list is empty");
        }
        Collections.shuffle(new ArrayList<>(all));
        return all.stream().limit(size).collect(Collectors.toList());
    }
}
