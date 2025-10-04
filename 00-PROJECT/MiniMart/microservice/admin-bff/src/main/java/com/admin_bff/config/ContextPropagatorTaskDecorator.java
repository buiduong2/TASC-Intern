package com.admin_bff.config;

import org.springframework.core.task.TaskDecorator;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

public class ContextPropagatorTaskDecorator implements TaskDecorator {
    @SuppressWarnings("null")
    @Override
    public Runnable decorate(Runnable runnable) {
        RequestAttributes requestContext = RequestContextHolder.currentRequestAttributes();
        SecurityContext securityContext = SecurityContextHolder.getContext();

        return () -> {
            try {
                if (requestContext != null) {
                    RequestContextHolder.setRequestAttributes(requestContext);
                }

                if (securityContext != null) {
                    SecurityContextHolder.setContext(securityContext);
                }
                runnable.run();
            } finally {
                RequestContextHolder.resetRequestAttributes();
                SecurityContextHolder.clearContext();

            }
        };
    }
}
