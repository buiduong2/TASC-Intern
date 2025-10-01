package com.authentication_service.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.authentication_service.model.SystemUser;
import com.authentication_service.model.User;

public interface SystemUserRepository extends JpaRepository<SystemUser, Long> {

    @EntityGraph(User.NamedGraph_Auth)
    Optional<SystemUser> findByUsername(String username);

    boolean existsByEmail(String value);

    boolean existsByUsername(String value);

}
