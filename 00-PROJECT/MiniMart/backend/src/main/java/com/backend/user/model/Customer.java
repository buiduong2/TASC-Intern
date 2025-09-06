package com.backend.user.model;

import java.util.List;

import com.backend.common.model.Audit;
import com.backend.common.model.Profile;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Customer {

    @Id
    @GeneratedValue
    private Long id;

    @Embedded
    private Profile profile;

    @OneToOne
    private User user;

    @OneToMany(mappedBy = "customer")
    private List<CustomerAddress> addresses;

    @Embedded
    private Audit audit;
}
