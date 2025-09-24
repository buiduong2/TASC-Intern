package com.backend.product.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.backend.product.dto.req.TagFilter;
import com.backend.product.dto.req.TagUpdateReq;
import com.backend.product.dto.res.TagAdminDTO;
import com.backend.product.dto.res.TagAdminDetailDTO;
import com.backend.product.service.TagService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/tags")
@RequiredArgsConstructor
@PreAuthorize("hasAnyAuthority('STAFF','ADMIN')")
public class AdminTagController {

    private final TagService service;

    @GetMapping
    public Page<TagAdminDTO> findAll(@Valid TagFilter filter, @PageableDefault(size = 10) Pageable pageable) {
        return service.findAdminAll(filter, pageable);
    }

    @GetMapping("{id}")
    public TagAdminDetailDTO findById(@PathVariable long id) {
        return service.findAdminDetailById(id);
    }

    @PostMapping
    public TagAdminDetailDTO create(@Valid @RequestBody TagUpdateReq dto) {
        return service.create(dto);
    }

    @PutMapping("{id}")
    public TagAdminDetailDTO update(@PathVariable long id, @Valid @RequestBody TagUpdateReq dto) {
        return service.update(id, dto);
    }

    @DeleteMapping("{id}")
    public void delete(@PathVariable long id) {
        service.delete(id);
    }
}
