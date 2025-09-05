package com.backend.common.utils;

@FunctionalInterface
public interface TetraFunction<A, B, C, D, R> {
    R apply(A a, B b, C c, D d);
}