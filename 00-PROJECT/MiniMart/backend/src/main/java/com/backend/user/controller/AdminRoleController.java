package com.backend.user.controller;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.backend.user.dto.res.RoleAdminDTO;
import com.backend.user.service.RoleService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/roles")
@RequiredArgsConstructor
public class AdminRoleController {
    private final RoleService service;

    @GetMapping
    public List<RoleAdminDTO> findAll(Pageable pageable) {
        return service.findAllAdmin(pageable);
    }
}
