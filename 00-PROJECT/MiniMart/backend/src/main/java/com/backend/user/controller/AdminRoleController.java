package com.backend.user.controller;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.backend.user.dto.req.RoleUpdateReq;
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

    @PutMapping("{id}")
    public RoleAdminDTO editById(@PathVariable long id, @RequestBody RoleUpdateReq req) {
        return service.update(id, req);
    }
}
