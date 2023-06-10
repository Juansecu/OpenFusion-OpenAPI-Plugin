package com.juansecu.openfusion.openfusionopenapiplugin.auth.filters;

import java.io.IOException;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.juansecu.openfusion.openfusionopenapiplugin.shared.adapters.JwtAdapter;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private static final Logger CONSOLE_LOGGER = LogManager.getLogger(JwtAuthenticationFilter.class);
    private static final String JWT_AUTHENTICATION_HEADER = "Authorization";
    private static final String JWT_AUTHENTICATION_HEADER_PREFIX = "Bearer ";
    private static final String TOKEN_PATTERN =
        "^" +
        JwtAuthenticationFilter.JWT_AUTHENTICATION_HEADER_PREFIX +
        "([0-9a-z_=]+)\\.([0-9a-z_=]+)\\.([0-9a-z_\\-+/=]+)$";

    private final JwtAdapter jwtAdapter;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
        final HttpServletRequest request,
        final HttpServletResponse response,
        final FilterChain filterChain
    ) throws IOException, ServletException {
        UserDetails account;
        UsernamePasswordAuthenticationToken authenticationToken;
        String token;
        String username;

        final String authenticationHeaderValue = request.getHeader(
            JwtAuthenticationFilter.JWT_AUTHENTICATION_HEADER
        );

        if (!this.isValidToken(authenticationHeaderValue)) {
            JwtAuthenticationFilter.CONSOLE_LOGGER.error(
                "Invalid JSON Web Token"
            );

            filterChain.doFilter(request, response);

            return;
        }

        token = authenticationHeaderValue.substring(
            JWT_AUTHENTICATION_HEADER_PREFIX.length()
        );
        username = this.jwtAdapter.getSubject(token);

        if (username == null) {
            JwtAuthenticationFilter.CONSOLE_LOGGER.error(
                "Invalid JSON Web Token"
            );

            filterChain.doFilter(request, response);

            return;
        }

        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            JwtAuthenticationFilter.CONSOLE_LOGGER.info(
                "User {} is already authenticated",
                username
            );

            filterChain.doFilter(request, response);

            return;
        }

        account = this.userDetailsService.loadUserByUsername(username);

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
            new AntPathRequestMatcher("/auth/**"),
            new AntPathRequestMatcher("/docs"),
            new AntPathRequestMatcher("/static/**"),
            new AntPathRequestMatcher("/swagger-ui/**"),
            new AntPathRequestMatcher("/verification-tokens/**"),
        };

        return Arrays
            .stream(excludedMatchers)
            .anyMatch(
                (AntPathRequestMatcher matcher) -> matcher.matches(request)
            );
    }

    private boolean isValidToken(final String authenticationHeaderValue) {
        if (authenticationHeaderValue == null) {
            JwtAuthenticationFilter.CONSOLE_LOGGER.error(
                "Authentication header is not present"
            );

            return false;
        }

        String token;

        final Pattern pattern = Pattern.compile(JwtAuthenticationFilter.TOKEN_PATTERN, Pattern.CASE_INSENSITIVE);
        final Matcher matcher = pattern.matcher(authenticationHeaderValue);

        if(!matcher.find()) {
            JwtAuthenticationFilter.CONSOLE_LOGGER.error(
                "Invalid authentication header value"
            );

            return false;
        }

        token = authenticationHeaderValue.substring(
            JWT_AUTHENTICATION_HEADER_PREFIX.length()
        );

        return this.jwtAdapter.isValidJsonWebToken(token);
    }
}
