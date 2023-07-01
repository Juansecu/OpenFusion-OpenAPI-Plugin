package com.juansecu.openfusion.openfusionopenapiplugin.auth.utils;

import java.util.Arrays;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import com.juansecu.openfusion.openfusionopenapiplugin.accounts.models.entities.AccountEntity;
import com.juansecu.openfusion.openfusionopenapiplugin.auth.AuthenticationService;
import com.juansecu.openfusion.openfusionopenapiplugin.auth.models.AuthenticationDetails;
import com.juansecu.openfusion.openfusionopenapiplugin.shared.adapters.JwtAdapter;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationValidationUtil {
    private static final String AUTHENTICATION_HEADER_NAME = "Authorization";
    private static final String AUTHENTICATION_HEADER_PREFIX = "Bearer ";
    private static final Logger CONSOLE_LOGGER = LogManager.getLogger(JwtAuthenticationValidationUtil.class);
    private static final String TOKEN_PATTERN = "([0-9a-z_=]+)\\.([0-9a-z_=]+)\\.([0-9a-z_\\-+/=]+)";

    private final JwtAdapter jwtAdapter;
    private final UserDetailsService userDetailsService;

    public AuthenticationDetails validateAuthentication(final HttpServletRequest request) {
        AccountEntity account;
        String token;
        String username;

        final Cookie authenticationCookie = Arrays
            .stream(
                Objects.requireNonNullElse(
                    request.getCookies(),
                    new Cookie[0]
                )
            )
            .filter(cookie -> cookie.getName().equals(AuthenticationService.AUTHENTICATION_COOKIE_NAME))
            .findFirst()
            .orElse(null);
        final String authenticationHeaderValue = request.getHeader(
            JwtAuthenticationValidationUtil.AUTHENTICATION_HEADER_NAME
        );
        final boolean isValidAuthenticationCookie = this.isValidToken(
            authenticationCookie
        );
        final boolean isValidAuthenticationHeaderValue = this.isValidToken(
            authenticationHeaderValue
        );

        if (!isValidAuthenticationCookie && !isValidAuthenticationHeaderValue) {
            JwtAuthenticationValidationUtil.CONSOLE_LOGGER.error(
                "Invalid JSON Web Token"
            );

            return null;
        }

        if (isValidAuthenticationCookie) {
            token = Objects.requireNonNull(authenticationCookie).getValue();
        } else {
            token = authenticationHeaderValue.substring(
                JwtAuthenticationValidationUtil.AUTHENTICATION_HEADER_PREFIX.length()
            );
        }

        username = this.jwtAdapter.getSubject(token);

        if (username == null) {
            JwtAuthenticationValidationUtil.CONSOLE_LOGGER.error(
                "Invalid JSON Web Token"
            );

            return null;
        }

        account = (AccountEntity) this.userDetailsService.loadUserByUsername(
            username
        );

        return new AuthenticationDetails(account, token);
    }

    private boolean isValidToken(final Cookie authenticationCookie) {
        if (authenticationCookie == null) {
            JwtAuthenticationValidationUtil.CONSOLE_LOGGER.error(
                "Authentication cookie is not present"
            );

            return false;
        }

        final String token = authenticationCookie.getValue();
        final Pattern pattern = Pattern.compile(
            "^" +
                JwtAuthenticationValidationUtil.TOKEN_PATTERN +
                "$",
            Pattern.CASE_INSENSITIVE
        );
        final Matcher matcher = pattern.matcher(token);

        if(!matcher.find()) {
            JwtAuthenticationValidationUtil.CONSOLE_LOGGER.error(
                "Invalid authentication cookie value"
            );

            return false;
        }

        return this.jwtAdapter.isValidJsonWebToken(token);
    }

    private boolean isValidToken(final String authenticationHeaderValue) {
        if (authenticationHeaderValue == null) {
            JwtAuthenticationValidationUtil.CONSOLE_LOGGER.error(
                "Authentication header is not present"
            );

            return false;
        }

        String token;

        final Pattern pattern = Pattern.compile(
            "^" +
                JwtAuthenticationValidationUtil.AUTHENTICATION_HEADER_PREFIX +
                JwtAuthenticationValidationUtil.TOKEN_PATTERN +
                "$",
            Pattern.CASE_INSENSITIVE
        );
        final Matcher matcher = pattern.matcher(authenticationHeaderValue);

        if(!matcher.find()) {
            JwtAuthenticationValidationUtil.CONSOLE_LOGGER.error(
                "Invalid authentication header value"
            );

            return false;
        }

        token = authenticationHeaderValue.substring(
            AUTHENTICATION_HEADER_PREFIX.length()
        );

        return this.jwtAdapter.isValidJsonWebToken(token);
    }
}
