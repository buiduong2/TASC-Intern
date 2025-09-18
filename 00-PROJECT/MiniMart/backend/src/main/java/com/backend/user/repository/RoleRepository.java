package com.backend.user.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.backend.user.dto.res.RoleAdminDTO;
import com.backend.user.model.Role;
import com.backend.user.model.RoleName;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByName(RoleName name);

    List<RoleAdminDTO> findBy();
}
