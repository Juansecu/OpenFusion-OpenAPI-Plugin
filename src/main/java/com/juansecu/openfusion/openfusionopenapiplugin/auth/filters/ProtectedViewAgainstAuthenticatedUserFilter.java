package com.juansecu.openfusion.openfusionopenapiplugin.auth.filters;

import java.io.IOException;
import java.util.Arrays;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.juansecu.openfusion.openfusionopenapiplugin.auth.models.AuthenticationDetails;
import com.juansecu.openfusion.openfusionopenapiplugin.auth.utils.JwtAuthenticationValidationUtil;

@Component
@RequiredArgsConstructor
public class ProtectedViewAgainstAuthenticatedUserFilter extends OncePerRequestFilter {
    private final JwtAuthenticationValidationUtil jwtAuthenticationValidationUtil;

    @Override
    protected void doFilterInternal(
        final HttpServletRequest request,
        final HttpServletResponse response,
        final FilterChain filterChain
    ) throws IOException, ServletException {
        final AuthenticationDetails authenticationDetails = this.jwtAuthenticationValidationUtil.validateAuthentication(
            request
        );

        if (authenticationDetails != null) {
            response.sendRedirect("/accounts/email-preferences");
            return;
        }

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(final HttpServletRequest request) {
        final AntPathRequestMatcher[] excludedMatchers = {
            new AntPathRequestMatcher("/auth/login"),
            new AntPathRequestMatcher("/auth/register")
        };

        return Arrays
            .stream(excludedMatchers)
            .noneMatch(
                (AntPathRequestMatcher matcher) -> matcher.matches(request)
            );
    }
}
