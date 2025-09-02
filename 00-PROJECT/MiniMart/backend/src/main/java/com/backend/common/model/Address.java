package com.backend.common.model;

import com.backend.user.model.Customer;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Address {
    @Id
    @GeneratedValue
    private Long id;

    @Embedded
    private Profile profile;

    private String details;

    private String city;

    private String area;

    private boolean isDefault;

    @Embedded
    private Audit audit;

    @ManyToOne(fetch = FetchType.LAZY)
    private Customer customer;
}
