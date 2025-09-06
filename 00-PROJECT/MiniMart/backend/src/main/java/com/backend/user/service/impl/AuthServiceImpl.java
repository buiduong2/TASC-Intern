package com.backend.user.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.backend.user.model.User;
import com.backend.user.service.AuthService;

@Service
public class AuthServiceImpl implements AuthService {

    @Value("${custom.security.enabled}")
    private boolean securityEnable;

    @Override
    public User getAuthUser() {
        return null;
    }

}
