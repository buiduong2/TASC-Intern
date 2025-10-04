package com.product_service.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.product_service.dto.res.CategoryDTO;
import com.product_service.dto.res.CategoryDetailDTO;
import com.product_service.service.CategoryService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/categories")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService service;

    @GetMapping
    public List<CategoryDTO> findAll() {
        return service.findAll();
    }

    @GetMapping("{id}")
    public CategoryDetailDTO findById(@PathVariable long id) {
        return service.findById(id);
    }
}
