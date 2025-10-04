package com.authentication_service.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.authentication_service.dto.res.UserAuthSummary;
import com.authentication_service.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final UserService service;

    @GetMapping("/summary")
    Page<UserAuthSummary> getUsers(Pageable pageable) {
        return service.getUsers(pageable);
    }
}
