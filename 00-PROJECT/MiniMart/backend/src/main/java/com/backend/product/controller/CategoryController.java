package com.backend.product.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.backend.product.dto.res.CategoryDTO;
import com.backend.product.dto.res.CategoryDetailDTO;
import com.backend.product.service.CategoryService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/categories")
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
