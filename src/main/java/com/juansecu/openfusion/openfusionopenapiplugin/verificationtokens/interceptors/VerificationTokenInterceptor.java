package com.juansecu.openfusion.openfusionopenapiplugin.verificationtokens.interceptors;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.juansecu.openfusion.openfusionopenapiplugin.accounts.AccountsService;
import com.juansecu.openfusion.openfusionopenapiplugin.accounts.events.AccountDeleteRequestVerifiedEvent;
import com.juansecu.openfusion.openfusionopenapiplugin.accounts.models.entities.AccountEntity;
import com.juansecu.openfusion.openfusionopenapiplugin.shared.utils.RedirectUtil;
import com.juansecu.openfusion.openfusionopenapiplugin.verificationtokens.VerificationTokensService;
import com.juansecu.openfusion.openfusionopenapiplugin.verificationtokens.enums.EVerificationTokenType;
import com.juansecu.openfusion.openfusionopenapiplugin.verificationtokens.models.entities.VerificationTokenEntity;

@Component
@RequiredArgsConstructor
public class VerificationTokenInterceptor implements HandlerInterceptor {
    private static final Logger CONSOLE_LOGGER = LogManager.getLogger(VerificationTokenInterceptor.class);

    private final AccountsService accountsService;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void postHandle(
        final HttpServletRequest request,
        final HttpServletResponse response,
        final Object handler,
        final ModelAndView modelAndView
    ) throws Exception {
        if (
            request.getAttribute(
                VerificationTokensService.IS_INVALID_TOKEN_ATTRIBUTE_KEY
            ) == null
        )
            return;

        VerificationTokenInterceptor.CONSOLE_LOGGER.info(
            "Post-intercepting token verification request..."
        );

        final AccountEntity account = (AccountEntity) request.getAttribute("account");
        final boolean isAuthenticated = (boolean) request.getAttribute("isAuthenticated");
        final boolean isInvalidToken = (boolean) request.getAttribute(
            VerificationTokensService.IS_INVALID_TOKEN_ATTRIBUTE_KEY
        );
        final VerificationTokenEntity verificationToken = (VerificationTokenEntity) request.getAttribute(
            VerificationTokensService.VERIFICATION_TOKEN_ATTRIBUTE_KEY
        );
        final EVerificationTokenType verificationTokenType = EVerificationTokenType.valueOf(
            request.getParameter("type")
        );

        if (isInvalidToken) {
            RedirectUtil.redirect(
                isAuthenticated,
                false,
                "Invalid token",
                response
            );

            return;
        }

        switch (verificationTokenType) {
            case DELETE_ACCOUNT_TOKEN -> {
                this.applicationEventPublisher.publishEvent(
                    new AccountDeleteRequestVerifiedEvent(account)
                );

                RedirectUtil.redirect(
                    false,
                    true,
                    "Your account has been deleted successfully",
                    response
                );
            }
            case EMAIL_VERIFICATION_TOKEN -> {
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

                RedirectUtil.redirect(
                    isAuthenticated,
                    true,
                    "Your account has been verified successfully",
                    response
                );
            }
            case RESET_PASSWORD_TOKEN -> {
                if (isAuthenticated) {
                    response.sendRedirect(
                        "/accounts/change-password"
                    );

                    return;
                }

                if (verificationToken.getUsesCount() == 1) {
                    VerificationTokenInterceptor.CONSOLE_LOGGER.info(
                        "Redirecting user {} to reset their password...",
                        account.getUsername()
                    );

                    response.sendRedirect(
                        "/auth/reset-password?token=" +
                            request.getParameter("token") +
                            "&type=" +
                            verificationTokenType +
                            "&username=" +
                            account.getUsername()
                    );
                }
            }
            default -> {
                VerificationTokenInterceptor.CONSOLE_LOGGER.error(
                    "Token handling for {} not implemented yet, redirecting...",
                    verificationTokenType
                );

                RedirectUtil.redirect(
                    isAuthenticated,
                    false,
                    "Invalid token",
                    response
                );
            }
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

        final AccountEntity account = (AccountEntity) request.getAttribute("account");
        final boolean isAuthenticated = (boolean) request.getAttribute("isAuthenticated");
        final EVerificationTokenType verificationTokenType = (EVerificationTokenType) request.getAttribute(
            "verificationTokenType"
        );

        if (
            account.isVerified() &&
            verificationTokenType == EVerificationTokenType.EMAIL_VERIFICATION_TOKEN
        ) {
            VerificationTokenInterceptor.CONSOLE_LOGGER.info(
                "{}'s account is already verified...",
                account.getUsername()
            );

            RedirectUtil.redirect(
                isAuthenticated,
                false,
                "Your account is already verified",
                response
            );

            return false;
        }

        return true;
    }
}
