package com.backend.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.backend.user.model.User;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    @Query("""
            SELECT tokenVersion FROM User WHERE id = ?1
            """)
    long getTokenVersionByUserId(Long userId);
}
