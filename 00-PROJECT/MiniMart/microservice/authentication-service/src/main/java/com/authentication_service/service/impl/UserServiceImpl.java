package com.authentication_service.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.authentication_service.dto.res.UserAuthSummary;
import com.authentication_service.mapper.UserMapper;
import com.authentication_service.repository.UserRepository;
import com.authentication_service.service.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final UserMapper userMapper;

    @Override
    public Page<UserAuthSummary> getUsers(Pageable pageable) {
        return userRepository.findAll(pageable).map(userMapper::toSummary);
    }

}
