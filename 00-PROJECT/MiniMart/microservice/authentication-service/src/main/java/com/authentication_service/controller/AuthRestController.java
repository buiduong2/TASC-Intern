package com.authentication_service.controller;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.authentication_service.dto.req.ChangePasswordReq;
import com.authentication_service.dto.req.RegisterReq;
import com.authentication_service.dto.res.UserDTO;
import com.authentication_service.service.AuthService;
import com.authentication_service.utils.AuthUtils;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
public class AuthRestController {

    private final AuthService authService;

    @GetMapping("/me")
    public UserDTO getInfo(Authentication auth) {
        return authService.getInfo(AuthUtils.getAuthId(auth));
    }

    @PostMapping("register")
    @ResponseStatus(HttpStatus.CREATED)
    public UserDTO register(@Valid @RequestBody RegisterReq registerReq) {
        return authService.register(registerReq);
    }

    @PostMapping("change-password")
    public void changePassword(@Valid @RequestBody ChangePasswordReq req, Authentication auth) {
        authService.changePassword(req, AuthUtils.getAuthId(auth));

    }
}
