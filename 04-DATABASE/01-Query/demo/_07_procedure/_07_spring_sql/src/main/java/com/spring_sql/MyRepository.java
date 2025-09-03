package com.spring_sql;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;

public interface MyRepository extends JpaRepository<MyEntity, Long> {
    // Chỉ có thể OUT 1 PAram mà thôi
    @Procedure(procedureName = "calc_max")
    Integer max(@Param("p1") Integer a, @Param("p2") Integer b);
}
