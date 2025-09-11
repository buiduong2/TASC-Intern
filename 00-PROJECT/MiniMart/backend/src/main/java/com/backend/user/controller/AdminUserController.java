package com.backend.user.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.backend.user.dto.req.UpdateUserAdminReq;
import com.backend.user.dto.res.UserAdminDTO;
import com.backend.user.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/users")

@RequiredArgsConstructor
public class AdminUserController {

    private final UserService service;

    @GetMapping
    public Page<UserAdminDTO> findPage(@PageableDefault(size = 10) Pageable pageable) {
        return service.findAdminDTOsBy(pageable);
    }

    @GetMapping("{id}")
    public UserAdminDTO findById(@PathVariable Long id) {
        return service.findAdminById(id);
    }

    @PutMapping("{id}")
    public UserAdminDTO editById(@PathVariable long id, @RequestBody UpdateUserAdminReq req) {
        return service.update(id, req);
    }

}
