package com.backend.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.EntityGraph.EntityGraphType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import com.backend.user.model.User;

public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

    @EntityGraph(value = User.NamedGraph_Auth, type = EntityGraphType.FETCH)
    Optional<User> findByUsername(String username);

    @EntityGraph(value = User.NamedGraph_Auth, type = EntityGraphType.FETCH)
    @Query("""
            FROM User WHERE id = ?1
            """)
    Optional<User> findByIdForDTO(long userId);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    @Query("""
            SELECT tokenVersion FROM User WHERE id = ?1
            """)
    long getTokenVersionByUserId(Long userId);
}
