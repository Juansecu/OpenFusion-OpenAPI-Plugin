package com.juansecu.openfusion.openfusionopenapiplugin.auth.filters;

import java.io.IOException;
import java.util.Arrays;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class ProtectedViewAgainstAuthenticationFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(
        final HttpServletRequest request,
        final HttpServletResponse response,
        final FilterChain filterChain
    ) throws IOException, ServletException {
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            filterChain.doFilter(request, response);
        } else {
            response.sendRedirect("/auth/login");
        }
    }

    @Override
    protected boolean shouldNotFilter(final HttpServletRequest request) {
        final AntPathRequestMatcher[] excludedMatchers = {
            new AntPathRequestMatcher("/api-docs/**"),
            new AntPathRequestMatcher("/api/auth/**"),
            new AntPathRequestMatcher("/auth/forgot-password"),
            new AntPathRequestMatcher("/auth/login"),
            new AntPathRequestMatcher("/auth/register"),
            new AntPathRequestMatcher("/docs"),
            new AntPathRequestMatcher("/favicon.ico"),
            new AntPathRequestMatcher("/static/**"),
            new AntPathRequestMatcher("/swagger-ui/**"),
            new AntPathRequestMatcher("/api/verification-tokens/**")
        };

        return Arrays
            .stream(excludedMatchers)
            .anyMatch(
                (AntPathRequestMatcher matcher) -> matcher.matches(request)
            );
    }
}
