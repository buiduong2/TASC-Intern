package com.profile_service.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.profile_service.dto.res.AddressInfo;
import com.profile_service.model.Address;

public interface AddressRepository extends JpaRepository<Address, Long> {

    @Query(value = """
            SELECT NEW com.profile_service.dto.res.AddressInfo(
                a.id, a.details, a.city, a.area, a.firstName, a.lastName, a.phone, p.id, a.createdAt, a.updatedAt
            )
            FROM Address a
            JOIN a.profile p
            WHERE  p.userId = :userId
            """, countQuery = """
            SELECT COUNT(a)
            FROM Address a
            JOIN a.profile p
            WHERE p.userId = :userId
            """)
    Page<AddressInfo> findByProfileIdAndProfileUserId(Long userId, Pageable pageable);

    @Query("""
            SELECT NEW com.profile_service.dto.res.AddressInfo(
                a.id, a.details, a.city, a.area, a.firstName, a.lastName, a.phone, p.id, a.createdAt, a.updatedAt
            )
            FROM Address a
            JOIN a.profile p
            WHERE a.id = :id AND p.userId = :userId
            """)
    Optional<AddressInfo> findInfoByIdAndProfileUserId(long id, Long userId);

    Optional<Address> findByIdAndProfileUserId(long id, Long userId);

    @Modifying
    @Query("DELETE FROM Address AS a WHERE  a.id = ?1 AND a.profile.userId = ?2 ")
    int deleteByIdAndProfileUserId(long id, long userId);

}
