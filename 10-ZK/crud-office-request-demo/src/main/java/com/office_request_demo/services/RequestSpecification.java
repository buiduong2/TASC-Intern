package com.office_request_demo.services;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.Predicate;

import org.springframework.data.jpa.domain.Specification;

import com.office_request_demo.entities.Request;
import com.office_request_demo.ui.model.RequestFilter;

public class RequestSpecification {

    public static Specification<Request> filter(RequestFilter filter) {

        return (root, query, cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            if (filter != null) {

                if (filter.getKeyword() != null && !filter.getKeyword().isBlank()) {
                    predicates.add(
                            cb.like(
                                    cb.lower(root.get("title")),
                                    "%" + filter.getKeyword().toLowerCase() + "%"));
                }

                if (filter.getStatus() != null) {
                    predicates.add(
                            cb.equal(root.get("status"), filter.getStatus()));
                }
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}