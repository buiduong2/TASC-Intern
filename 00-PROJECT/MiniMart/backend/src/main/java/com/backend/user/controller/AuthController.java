package com.backend.user.controller;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.backend.user.dto.req.LoginReq;
import com.backend.user.dto.req.RegisterReq;
import com.backend.user.dto.res.AuthRes;
import com.backend.user.service.AuthService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
@Validated
public class AuthController {

    private final AuthService authService;

    @PostMapping
    public AuthRes login(@Valid @RequestBody LoginReq loginReq) {
        return authService.login(loginReq);
    }

    @PostMapping
    public AuthRes register(@Valid @RequestBody RegisterReq registerReq) {
        return authService.register(registerReq);
    }

}
