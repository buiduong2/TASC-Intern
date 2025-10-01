package com.authentication_service.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.EntityGraph.EntityGraphType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.authentication_service.model.User;

public interface UserRepository extends JpaRepository<User, Long> {

    @EntityGraph(value = User.NamedGraph_Auth, type = EntityGraphType.FETCH)
    @Query("FROM User WHERE id = ?1")
    Optional<User> findByIdForAuth(long id);
}
