package com.backend.product.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.backend.common.service.ImageCloudService;
import com.backend.common.service.impl.ImageCloudServiceImpl;
import com.backend.common.validation.Image;
import com.backend.product.dto.req.CategoryFilter;
import com.backend.product.dto.req.CategoryUpdateReq;
import com.backend.product.dto.res.CategoryAdminDTO;
import com.backend.product.dto.res.CategoryAdminDetailDTO;
import com.backend.product.service.CategoryImageService;
import com.backend.product.service.CategoryService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/categories")
public class AdminCategoryController {

    private final CategoryService service;

    private final CategoryImageService imageService;

    private final ImageCloudService cloudService;

    @GetMapping
    public Page<CategoryAdminDTO> findPage(CategoryFilter filter, @PageableDefault(size = 10) Pageable pageable) {
        return service.findAllAdmin(filter, pageable);
    }

    @GetMapping("{id}")
    public CategoryAdminDetailDTO findById(@PathVariable long id) {
        return service.findAdminById(id);
    }

    @PostMapping
    public CategoryAdminDetailDTO create(
            @RequestPart("category") @Valid CategoryUpdateReq dto,
            @RequestParam(required = false) @Image MultipartFile image) {
        CategoryAdminDetailDTO result = service.create(dto);
        if (image != null && !image.isEmpty()) {
            cloudService.updateImage(image, result.getId(), ImageCloudServiceImpl.CATEGORY_PREFIX)
                    .thenAccept(meta -> imageService.save(meta, result.getId()));
        }
        return result;
    }

    @PutMapping("{id}")
    public CategoryAdminDetailDTO update(@PathVariable Long id, @RequestPart("category") @Valid CategoryUpdateReq dto,
            @RequestParam(required = false) @Image MultipartFile image) {
        CategoryAdminDetailDTO update = service.update(id, dto);
        if (image != null && !image.isEmpty()) {
            cloudService.updateImage(image, update.getId(), ImageCloudServiceImpl.CATEGORY_PREFIX)
                    .thenAccept(meta -> imageService.save(meta, update.getId()));
        }
        return update;
    }

    @DeleteMapping("{id}")
    public void deleteById(@PathVariable long id) {
        service.deleteById(id);
    }

}
