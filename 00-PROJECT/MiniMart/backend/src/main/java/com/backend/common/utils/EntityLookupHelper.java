package com.backend.common.utils;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.data.jpa.repository.JpaRepository;

import com.backend.common.exception.ResourceNotFoundException;
import com.backend.common.model.GetIdAble;

public class EntityLookupHelper {

    public static <R extends JpaRepository<E, Long>, E extends GetIdAble<Long>> E findById(R r, long id, String name) {

        return r.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(name + " id = " + id + " Not found"));
    }

    public static <R extends JpaRepository<E, Long>, E extends GetIdAble<Long>> List<E> findByIdIn(R r, List<Long> ids,
            String name) {
        List<E> result = r.findAllById(ids);
        if (result.size() != ids.size()) {
            throw new ResourceNotFoundException(name + " some ids is Not found");
        }

        return result;
    }

    public static <R extends JpaRepository<E, Long>, E extends GetIdAble<Long>> Map<Long, E> findMapByIdIn(R r,
            List<Long> ids,
            String name) {
        return findByIdIn(r, ids, name).stream().collect(Collectors.toMap(E::getId, Function.identity()));

    }
}