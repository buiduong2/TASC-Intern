package com.backend.user.controller;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.backend.user.dto.req.ChangePasswordReq;
import com.backend.user.dto.req.LoginReq;
import com.backend.user.dto.req.RefreshTokenReq;
import com.backend.user.dto.req.RegisterReq;
import com.backend.user.dto.req.RevokeJwtReq;
import com.backend.user.dto.res.AuthRes;
import com.backend.user.dto.res.UserDTO;
import com.backend.user.security.CustomUserDetail;
import com.backend.user.service.AuthService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
@PreAuthorize("permitAll()")
public class AuthController {

    private final AuthService authService;

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/me")
    public UserDTO getInfo(Authentication authentication) {
        if (authentication.getPrincipal() instanceof CustomUserDetail customUserDetail) {
            return authService.getInfo(customUserDetail.getUserId());
        } else {
            throw new RuntimeException("Printcipal not implemeted yet");
        }
    }

    @PreAuthorize("isAnonymous()")
    @PostMapping("login")
    public AuthRes login(@Valid @RequestBody LoginReq loginReq) {
        return authService.login(loginReq);
    }

    @PreAuthorize("isAnonymous()")
    @PostMapping("register")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthRes register(@Valid @RequestBody RegisterReq registerReq) {
        return authService.register(registerReq);
    }

    @PreAuthorize("permitAll()")
    @PostMapping("refresh")
    public AuthRes refresh(@Valid @RequestBody RefreshTokenReq req) {
        return authService.refreshToken(req);
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("change-password")
    public void changePassword(@Valid @RequestBody ChangePasswordReq req, Authentication authentication) {
        if (authentication.getPrincipal() instanceof CustomUserDetail customUserDetail) {
            authService.changePassword(req, customUserDetail.getUserId());
        } else {
            throw new RuntimeException("Printcipal not implemeted yet");
        }
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/revoke")
    public void revoke(@Valid @RequestBody RevokeJwtReq req, Authentication authentication) {
        if (authentication.getPrincipal() instanceof CustomUserDetail customUserDetail) {
            authService.revoke(req, customUserDetail.getUserId());
        } else {
            throw new RuntimeException("Printcipal not implemeted yet");
        }
    }

}
