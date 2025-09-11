package com.backend.product.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.backend.product.dto.res.TagAdminDTO;
import com.backend.product.model.Tag;

public interface TagRepository extends JpaRepository<Tag, Long> {
    @Query("""
                SELECT new com.backend.product.dto.res.TagAdminDTO(
                    t.id,
                    t.name,
                    t.audit.createdAt,
                    t.audit.updatedAt,
                    COUNT(p.id)
                )
                FROM Tag t
                LEFT JOIN t.products p
                GROUP BY t
            """)
    Page<TagAdminDTO> findAllAdmin(Pageable pageable);
}
