package com.juansecu.openfusion.openfusionopenapiplugin.verificationtokens.interceptors;

import java.io.IOException;
import java.util.UUID;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.juansecu.openfusion.openfusionopenapiplugin.accounts.AccountsService;
import com.juansecu.openfusion.openfusionopenapiplugin.accounts.events.AccountDeleteRequestVerifiedEvent;
import com.juansecu.openfusion.openfusionopenapiplugin.accounts.models.entities.AccountEntity;
import com.juansecu.openfusion.openfusionopenapiplugin.auth.models.AuthenticationDetails;
import com.juansecu.openfusion.openfusionopenapiplugin.auth.utils.JwtAuthenticationValidationUtil;
import com.juansecu.openfusion.openfusionopenapiplugin.shared.utils.CryptoUtil;
import com.juansecu.openfusion.openfusionopenapiplugin.verificationtokens.VerificationTokensService;
import com.juansecu.openfusion.openfusionopenapiplugin.verificationtokens.enums.EVerificationTokenType;

@Component
@RequiredArgsConstructor
public class VerificationTokenInterceptor implements HandlerInterceptor {
    private static final Logger CONSOLE_LOGGER = LogManager.getLogger(VerificationTokenInterceptor.class);

    private final AccountsService accountsService;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final CryptoUtil cryptoUtil;
    private final JwtAuthenticationValidationUtil jwtAuthenticationValidationUtil;
    private final UserDetailsService userDetailsService;

    @Override
    public void postHandle(
        final HttpServletRequest request,
        final HttpServletResponse response,
        final Object handler,
        final ModelAndView modelAndView
    ) throws Exception {
        VerificationTokenInterceptor.CONSOLE_LOGGER.info(
            "Post-intercepting token verification request..."
        );

        final AccountEntity account = (AccountEntity) request.getAttribute("account");
        final boolean isAuthenticated = (boolean) request.getAttribute("isAuthenticated");
        final boolean isInvalidToken = (boolean) request.getAttribute(
            VerificationTokensService.IS_INVALID_TOKEN_ATTRIBUTE_KEY
        );
        final EVerificationTokenType verificationTokenType = EVerificationTokenType.valueOf(
            request.getParameter("type")
        );

        if (isInvalidToken) {
            this.redirect(
                isAuthenticated,
                true,
                "Invalid token",
                response
            );

            return;
        }

        if (verificationTokenType == EVerificationTokenType.DELETE_ACCOUNT_TOKEN) {
            this.applicationEventPublisher.publishEvent(
                new AccountDeleteRequestVerifiedEvent(account)
            );

            this.redirect(
                isAuthenticated,
                false,
                "Your account has been deleted successfully",
                response
            );
        } else if (verificationTokenType == EVerificationTokenType.EMAIL_VERIFICATION_TOKEN) {
            VerificationTokenInterceptor.CONSOLE_LOGGER.info(
                "Setting {}'s account as verified...",
                account.getUsername()
            );

            account.setVerified(true);

            this.accountsService.setAccountAsVerified(true, account);

            VerificationTokenInterceptor.CONSOLE_LOGGER.info(
                "{}'s account has been verified successfully...",
                account.getUsername()
            );

            this.redirect(
                isAuthenticated,
                false,
                "Your account has been verified successfully",
                response
            );
        }
    }

    @Override
    public boolean preHandle(
        final HttpServletRequest request,
        final HttpServletResponse response,
        final Object handler
    ) throws Exception {
        VerificationTokenInterceptor.CONSOLE_LOGGER.info(
            "Pre-intercepting token verification request..."
        );

        AccountEntity account;
        UUID decryptedToken;
        EVerificationTokenType verificationTokenType;

        final AuthenticationDetails authenticationDetails = this.jwtAuthenticationValidationUtil.validateAuthentication(
            request
        );
        final boolean isAuthenticationValid = authenticationDetails != null;
        final String username = request.getParameter("username");

        request.setAttribute("isAuthenticated", isAuthenticationValid);

        if (isAuthenticationValid) {
            if (!authenticationDetails.account().getUsername().equals(username)) {
                VerificationTokenInterceptor.CONSOLE_LOGGER.error(
                    "Account username does not match with the username in the request, redirecting..."
                );

                response.sendRedirect(
                    "/accounts/email-preferences?error=Invalid token"
                );

                return false;
            }

            account = authenticationDetails.account();
        } else
            account = (AccountEntity) this.userDetailsService.loadUserByUsername(
                username
            );

        try {
            decryptedToken = UUID.fromString(
                this.cryptoUtil.decrypt(
                    request.getParameter("token")
                )
            );
            verificationTokenType = EVerificationTokenType.valueOf(
                request.getParameter("type")
            );
        } catch (final IllegalArgumentException illegalArgumentException) {
            VerificationTokenInterceptor.CONSOLE_LOGGER.error(
                "Token type or encrypted token is invalid, redirecting..."
            );

            this.redirect(
                isAuthenticationValid,
                true,
                "Invalid token",
                response
            );

            return false;
        }

        if (
            account.isVerified() &&
            verificationTokenType == EVerificationTokenType.EMAIL_VERIFICATION_TOKEN
        ) {
            VerificationTokenInterceptor.CONSOLE_LOGGER.info(
                "{}'s account is already verified...",
                account.getUsername()
            );

            this.redirect(
                isAuthenticationValid,
                true,
                "Your account is already verified",
                response
            );

            return false;
        }

        request.setAttribute("account", account);
        request.setAttribute("decryptedToken", decryptedToken);

        return true;
    }

    private void redirect(
        final boolean isAuthenticated,
        final boolean isInvalidToken,
        final String message,
        final HttpServletResponse response
    ) throws IOException {
        if (!isAuthenticated) {
            if (isInvalidToken) {
                response.sendRedirect(
                    "/auth/login?error=" + message
                );

                return;
            }

            response.sendRedirect(
                "/auth/login?success=" + message
            );

            return;
        }

        if (isInvalidToken) {
            response.sendRedirect(
                "/accounts/email-preferences?error=" + message
            );

            return;
        }

        response.sendRedirect(
            "/accounts/email-preferences?success=" + message
        );
    }
}
