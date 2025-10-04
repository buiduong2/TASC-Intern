package com.admin_bff.config;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import io.github.resilience4j.core.ContextPropagator;

public class RequestHeaderContextPropagator implements ContextPropagator<RequestAttributes> {

    @Override
    public Supplier<Optional<RequestAttributes>> retrieve() {
        return () -> Optional.ofNullable(RequestContextHolder.getRequestAttributes());
    }

    @Override
    public Consumer<Optional<RequestAttributes>> copy() {
        return raOpt -> {
            raOpt.ifPresent(RequestContextHolder::setRequestAttributes);
        };
    }

    @Override
    public Consumer<Optional<RequestAttributes>> clear() {
        return raOpt -> RequestContextHolder.resetRequestAttributes();
    }

}
