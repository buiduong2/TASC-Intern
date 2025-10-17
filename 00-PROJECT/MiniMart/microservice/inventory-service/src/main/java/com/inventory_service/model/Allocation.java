package com.inventory_service.model;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.inventory_service.enums.AllocationStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
public class Allocation {

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false, unique = true)
    private Long orderId;

    @Column(nullable = false)
    private Long userId;

    private int totalAllocatedQuantity;

    @OneToMany(mappedBy = "allocation")
    private List<AllocationItem> allocationItems;

    @Enumerated(EnumType.STRING)
    private AllocationStatus status;

    @CreatedDate
    private LocalDateTime createdAt;
}
