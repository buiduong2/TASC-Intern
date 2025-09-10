package com.backend.user.repository;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.backend.user.model.JwtBlacklist;

public interface JwtBlackListRepository extends JpaRepository<JwtBlacklist, String> {
    @Modifying
    @Query("DELETE FROM JwtBlacklist b WHERE b.expiredAt < :now")
    void deleteAllExpired(@Param("now") LocalDateTime now);

}
