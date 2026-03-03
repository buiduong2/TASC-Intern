package com.office_request_demo.repositories;

import com.office_request_demo.entities.Request;
import com.office_request_demo.entities.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

import javax.persistence.Tuple;

public interface RequestRepository extends JpaRepository<Request, Long>, JpaSpecificationExecutor<Request> {

    /* ================= USER VIEW ================= */

    // Lấy request theo user
    List<Request> findByCreatedBy(String username);

    // Lấy request theo user + status (filter)
    List<Request> findByCreatedByAndStatus(String username, Status status);

    /* ================= ADMIN VIEW ================= */

    // Lấy theo status
    List<Request> findByStatus(Status status);

    /* ================= DASHBOARD ================= */

    long countByStatus(Status status);

    /* ================= OPTIONAL FILTER DATE ================= */

    List<Request> findByStartDateBetween(LocalDate from, LocalDate to);

    @Query(value = """
            SELECT status, COUNT(*) as count
            FROM leave_requests
            GROUP BY status
            """, nativeQuery = true)
    List<Tuple> countGroupByStatus();

    boolean existsByIdAndCreatedBy(long requestId, String name);

}