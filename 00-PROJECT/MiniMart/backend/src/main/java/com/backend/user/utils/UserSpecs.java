package com.backend.user.utils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import com.backend.common.model.Audit_;
import com.backend.common.utils.CriteriaApiUtils;
import com.backend.user.dto.req.UserFilter;
import com.backend.user.model.Role;
import com.backend.user.model.Role_;
import com.backend.user.model.User;
import com.backend.user.model.User_;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;

@Component
public class UserSpecs {

    public Specification<User> afterCreatedAt(LocalDateTime createdAtFrom) {
        return (root, query, builder) -> {
            return builder.greaterThanOrEqualTo(root.get(User_.audit).get(Audit_.createdAt), createdAtFrom);
        };
    }

    public Specification<User> beforeCreatedAt(LocalDateTime createdAtTo) {
        return (root, query, builder) -> {
            return builder.lessThanOrEqualTo(root.get(User_.audit).get(Audit_.createdAt), createdAtTo);
        };
    }

    public Specification<User> hasAnyRole(List<String> roleNames) {
        return (root, query, builder) -> {
            Join<User, Role> roles = CriteriaApiUtils.getOrCreateJoin(root, User_.roles, JoinType.LEFT);
            return roles.get(Role_.name).in(roleNames);
        };
    }

    public Specification<User> hasAnyStatus(List<String> userStatuses) {
        return (root, query, builder) -> {
            return root.get(User_.status).in(userStatuses);
        };
    }

    public Specification<User> containIgnoreCaseKeyword(String keyword) {
        String wildcard = "%" + keyword.toUpperCase() + "%";
        return (root, query, builder) -> {
            return builder.or(
                    builder.like(builder.upper(root.get(User_.email)), wildcard),
                    builder.like(builder.upper(root.get(User_.username)), wildcard));
        };
    }

    public Specification<User> byFilter(UserFilter filter) {
        if (filter == null) {
            return null;
        }
        List<Specification<User>> specs = new ArrayList<>();

        List<String> status = filter.getStatus();
        if (status != null && !status.isEmpty()) {
            specs.add(hasAnyStatus(status));
        }

        List<String> roleNames = filter.getRoleName();
        if (roleNames != null && !roleNames.isEmpty()) {
            specs.add(hasAnyRole(roleNames));
        }

        LocalDateTime createdAtFrom = filter.getCreatedDateFrom();
        if (createdAtFrom != null) {
            specs.add(afterCreatedAt(createdAtFrom));
        }

        LocalDateTime createdAtTo = filter.getCreatedDateTo();
        if (createdAtTo != null) {
            specs.add(beforeCreatedAt(createdAtTo));
        }

        String keyword = filter.getKeyword();
        if (keyword != null && !keyword.isBlank()) {
            specs.add(containIgnoreCaseKeyword(keyword));
        }

        if (!specs.isEmpty()) {
            return null;
        }

        return Specification.allOf(specs);
    }
}
