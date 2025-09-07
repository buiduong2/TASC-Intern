package com.backend.common.model;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Embedded;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

@MappedSuperclass
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseAddress implements GetIdAble<Long> {

    @Id
    @GeneratedValue
    private Long id;

    @Embedded
    private Profile profile;

    private String details;

    private String city;

    private String area;

    @Embedded
    private Audit audit;

}
