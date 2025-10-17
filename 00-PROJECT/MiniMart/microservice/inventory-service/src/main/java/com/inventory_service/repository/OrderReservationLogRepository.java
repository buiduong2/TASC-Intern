package com.inventory_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.inventory_service.model.OrderReservationLog;

public interface OrderReservationLogRepository extends JpaRepository<OrderReservationLog, Long> {

}
