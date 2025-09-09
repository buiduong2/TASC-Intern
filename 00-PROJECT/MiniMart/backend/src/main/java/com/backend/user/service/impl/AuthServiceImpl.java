package com.backend.user.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.backend.user.dto.req.LoginReq;
import com.backend.user.dto.req.RegisterReq;
import com.backend.user.dto.res.AuthRes;
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

    @Override
    public AuthRes login(LoginReq loginReq) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'login'");
    }

    @Override
    public AuthRes register(RegisterReq registerReq) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'register'");
    }

}
