package com.authentication_service.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.authentication_service.enums.OAUthProvider;
import com.authentication_service.model.OAuthUser;
import com.authentication_service.model.User;

public interface OAuthUserRepository extends JpaRepository<OAuthUser, Long> {
    @EntityGraph(value = User.NamedGraph_Auth)
    Optional<OAuthUser> findByProviderUserIdAndProvider(String providerUserId, OAUthProvider provider);
}
