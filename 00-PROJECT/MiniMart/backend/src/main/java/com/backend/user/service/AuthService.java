package com.backend.user.service;

import com.backend.user.dto.req.LoginReq;
import com.backend.user.dto.req.RegisterReq;
import com.backend.user.dto.res.AuthRes;
import com.backend.user.model.User;

public interface AuthService {
    User getAuthUser();

    AuthRes login(LoginReq loginReq);

    AuthRes register(RegisterReq registerReq);
}
