package com.authentication_service.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.authentication_service.dto.res.UserAuthSummary;

public interface UserService {

    Page<UserAuthSummary> getUsers(Pageable pageable);
    
}
