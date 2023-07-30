package com.juansecu.openfusion.openfusionopenapiplugin.auth.filters;

import java.io.IOException;
import java.util.Arrays;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.juansecu.openfusion.openfusionopenapiplugin.accounts.models.entities.AccountEntity;
import com.juansecu.openfusion.openfusionopenapiplugin.auth.models.AuthenticationDetails;
import com.juansecu.openfusion.openfusionopenapiplugin.auth.utils.JwtAuthenticationValidationUtil;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private static final Logger CONSOLE_LOGGER = LogManager.getLogger(JwtAuthenticationFilter.class);
    private final JwtAuthenticationValidationUtil jwtAuthenticationValidationUtil;

    @Override
    protected void doFilterInternal(
        final HttpServletRequest request,
        final HttpServletResponse response,
        final FilterChain filterChain
    ) throws IOException, ServletException {
        AccountEntity account;
        UsernamePasswordAuthenticationToken authenticationToken;
        String username;

        final AuthenticationDetails authenticationDetails = this.jwtAuthenticationValidationUtil.validateAuthentication(
            request
        );

        if (authenticationDetails == null) {
            JwtAuthenticationFilter.CONSOLE_LOGGER.error(
                "Invalid JSON Web Token"
            );

            filterChain.doFilter(request, response);

            return;
        }

        username = authenticationDetails.account().getUsername();

        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            JwtAuthenticationFilter.CONSOLE_LOGGER.info(
                "User {} is already authenticated",
                username
            );

            filterChain.doFilter(request, response);

            return;
        }

        account = authenticationDetails.account();

        JwtAuthenticationFilter.CONSOLE_LOGGER.info(
            "Authenticating user {}...",
            username
        );

        authenticationToken = new UsernamePasswordAuthenticationToken(
            username,
            null,
            account.getAuthorities()
        );

        request.setAttribute("account", account);

        authenticationToken.setDetails(
            new WebAuthenticationDetailsSource().buildDetails(request)
        );

        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(final HttpServletRequest request) {
        final AntPathRequestMatcher[] excludedMatchers = {
            new AntPathRequestMatcher("/api-docs/**"),
            new AntPathRequestMatcher("/api/auth/**"),
            new AntPathRequestMatcher("/auth/forgot-password"),
            new AntPathRequestMatcher("/auth/login"),
            new AntPathRequestMatcher("/auth/register"),
            new AntPathRequestMatcher("/auth/reset-password"),
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
