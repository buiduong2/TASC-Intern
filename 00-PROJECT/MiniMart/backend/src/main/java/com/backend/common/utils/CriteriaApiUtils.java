package com.backend.common.utils;

import java.util.Optional;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.metamodel.Attribute;
import jakarta.persistence.metamodel.ListAttribute;
import jakarta.persistence.metamodel.SingularAttribute;

public class CriteriaApiUtils {

    @SuppressWarnings("unchecked")
    public static <X, Y> Join<X, Y> getOrCreateJoin(Root<X> root, SingularAttribute<X, Y> attr,
            JoinType type) {
        return (Join<X, Y>) findJoin(root, attr, type)
                .orElseGet(() -> root.join(attr, type));
    }

    @SuppressWarnings("unchecked")
    public static <X, Y> Join<X, Y> getOrCreateJoin(Root<X> root, ListAttribute<X, Y> attr,
            JoinType type) {
        return (Join<X, Y>) findJoin(root, attr, type)
                .orElseGet(() -> root.join(attr, type));
    }

    private static <X, Y> Optional<Join<X, ?>> findJoin(Root<X> root, Attribute<X, Y> attr, JoinType type) {
        return root.getJoins()
                .stream()
                .filter(j -> j.getAttribute().getName().equals(attr.getName()) && j.getJoinType().equals(type))
                .findFirst();
    }

}
