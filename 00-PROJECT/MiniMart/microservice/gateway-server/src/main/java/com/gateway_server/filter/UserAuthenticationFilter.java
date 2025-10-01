package com.gateway_server.filter;

import java.util.Set;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.annotation.Order;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import com.gateway_server.security.CustomJwtAuthenticationToken;

import reactor.core.publisher.Mono;

@Order(1)
@Component
public class UserAuthenticationFilter implements GlobalFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        return ReactiveSecurityContextHolder.getContext().map(ctx -> ctx.getAuthentication())
                .flatMap(auth -> {
                    ServerWebExchange mutated = exchange;
                    if (auth != null && auth.isAuthenticated() && auth instanceof CustomJwtAuthenticationToken token) {
                        Long userId = token.getUser().getId();
                        Set<String> roles = token.getUser().getRoles();

                        mutated = exchange.mutate()
                                .request(r -> r.headers(h -> {
                                    h.add("x-user-id", String.valueOf(userId));
                                    h.add("x-user-roles", String.join(",", roles));
                                }))
                                .build();
                    }

                    return chain.filter(mutated);
                }).switchIfEmpty(Mono.defer(() -> {
                    ServerWebExchange mutated = exchange;
                    return chain.filter(mutated);
                }));

    }

}
