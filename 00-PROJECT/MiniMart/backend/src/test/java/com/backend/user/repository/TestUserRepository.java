package com.backend.user.repository;

import java.util.Optional;

import org.springframework.context.annotation.Primary;

import com.backend.user.model.User;

@Primary
public interface TestUserRepository extends UserRepository {
    Optional<User> findFirstByOrderByIdAsc();
}
