package com.common.security;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class InternalHeaderAuthenticationFilter extends OncePerRequestFilter {

    private final String USER_ID_HEADER = "x-user-id";

    private final String ROLES_HEADER = "x-user-roles";

    @SuppressWarnings("null")
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String userIdHeader = request.getHeader(USER_ID_HEADER);
        String userRoleHeader = request.getHeader(ROLES_HEADER);

        if (isUserIdValid(userIdHeader) && isRoleValid(userRoleHeader)) {
            UserDetails userDetails = new InternalHeaderUserDetails(userIdHeader, userRoleHeader);

            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());
            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        }

        filterChain.doFilter(request, response);

    }

    private boolean isUserIdValid(String value) {
        if (value == null || value.trim().isEmpty()) {
            return false;
        }
        try {
            Long.parseLong(value.trim());
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean isRoleValid(String roleValue) {
        if (roleValue == null || roleValue.trim().isEmpty()) {
            return false;
        }

        return roleValue.trim().matches("^[a-zA-Z0-9_]+(,[a-zA-Z0-9_]+)*$");
    }

}
