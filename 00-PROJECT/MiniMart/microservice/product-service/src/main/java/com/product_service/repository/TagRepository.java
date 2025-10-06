package com.product_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.product_service.model.Tag;

public interface TagRepository extends JpaRepository<Tag, Long> {

    @Modifying
    @Query(value = "DELETE FROM product_tags WHERE tags_id = ?1", nativeQuery = true)
    int deleteProductTagByTagId(long id);

}
