package com.backend.user.model;

import com.backend.common.model.BaseAddress;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class CustomerAddress extends BaseAddress {
    @ManyToOne(fetch = FetchType.LAZY)
    private Customer customer;

    private boolean isDefault;
}
