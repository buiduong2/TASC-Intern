package com.admin_bff.controller;

import java.util.concurrent.CompletableFuture;

import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.admin_bff.dto.res.UserSummaryDTO;
import com.admin_bff.service.UserService;
import com.common.dto.PageResponseDTO;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

@RestController
@RequestMapping("/v1/admin/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    @SneakyThrows
    public PageResponseDTO<UserSummaryDTO> getUsers(
            @RequestParam MultiValueMap<String, String> params) {
        return userService.getUsers(params).get();
    }
}
