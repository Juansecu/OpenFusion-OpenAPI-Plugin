package com.juansecu.openfusion.openfusionopenapiplugin.verificationtokens.filters;

import java.io.IOException;
import java.util.Arrays;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.juansecu.openfusion.openfusionopenapiplugin.accounts.models.entities.AccountEntity;
import com.juansecu.openfusion.openfusionopenapiplugin.auth.models.AuthenticationDetails;
import com.juansecu.openfusion.openfusionopenapiplugin.auth.utils.JwtAuthenticationValidationUtil;
import com.juansecu.openfusion.openfusionopenapiplugin.shared.utils.RedirectUtil;
import com.juansecu.openfusion.openfusionopenapiplugin.verificationtokens.VerificationTokensService;
import com.juansecu.openfusion.openfusionopenapiplugin.verificationtokens.enums.EVerificationTokenType;
import com.juansecu.openfusion.openfusionopenapiplugin.verificationtokens.models.entities.VerificationTokenEntity;

@Component
@RequiredArgsConstructor
public class VerificationTokenFinderFilter extends OncePerRequestFilter {
    private static final Logger CONSOLE_LOGGER = LogManager.getLogger(VerificationTokenFinderFilter.class);

    private final JwtAuthenticationValidationUtil jwtAuthenticationValidationUtil;
    private final UserDetailsService userDetailsService;
    private final VerificationTokensService verificationTokensService;

    @Override
    protected void doFilterInternal(
        final HttpServletRequest request,
        final HttpServletResponse response,
        final FilterChain filterChain
    ) throws IOException, ServletException {
        AccountEntity account;
        VerificationTokenEntity verificationToken;
        EVerificationTokenType verificationTokenType;

        final AuthenticationDetails authenticationDetails = this.jwtAuthenticationValidationUtil.validateAuthentication(
            request
        );
        final String encryptedToken = request.getParameter("token");
        final boolean isAuthenticated = authenticationDetails != null;
        final String username = request.getParameter("username");

        request.setAttribute("isAuthenticated", isAuthenticated);

        if (isAuthenticated) {
            if (!authenticationDetails.account().getUsername().equals(username)) {
                VerificationTokenFinderFilter.CONSOLE_LOGGER.error(
                    "The username does not match with the authenticated account"
                );

                response.sendRedirect(
                    "/accounts/email-preferences?error=Invalid token"
                );

                return;
            }

            account = authenticationDetails.account();
        } else
            account = (AccountEntity) this.userDetailsService.loadUserByUsername(
                username
            );

        try {
            verificationTokenType = EVerificationTokenType.valueOf(
                request.getParameter("type")
            );
        } catch (final IllegalArgumentException illegalArgumentException) {
            VerificationTokenFinderFilter.CONSOLE_LOGGER.error(
                "The verification token type is not valid"
            );

            RedirectUtil.redirect(
                isAuthenticated,
                false,
                "Invalid token",
                response
            );

            return;
        }

        verificationToken = this.verificationTokensService.getVerificationToken(
            encryptedToken,
            verificationTokenType,
            account
        );

        if (verificationToken == null) {
            VerificationTokenFinderFilter.CONSOLE_LOGGER.error(
                "Verification token not found"
            );

            RedirectUtil.redirect(
                isAuthenticated,
                false,
                "Invalid token",
                response
            );

            return;
        }

        request.setAttribute("account", account);
        request.setAttribute("isAuthenticated", isAuthenticated);
        request.setAttribute(
            VerificationTokensService.VERIFICATION_TOKEN_ATTRIBUTE_KEY,
            verificationToken
        );
        request.setAttribute(
            "verificationTokenType",
            verificationTokenType
        );

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(final HttpServletRequest request) {
        final AntPathRequestMatcher[] excludedMatchers = {
            new AntPathRequestMatcher("/api/verification-tokens/verify"),
            new AntPathRequestMatcher("/auth/reset-password")
        };

        return Arrays
            .stream(excludedMatchers)
            .noneMatch(
                (AntPathRequestMatcher matcher) -> matcher.matches(request)
            );
    }
}
