package com.backend.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.backend.user.model.JwtBlacklist;

public interface JwtBlackListRepository extends JpaRepository<JwtBlacklist, String> {

}
