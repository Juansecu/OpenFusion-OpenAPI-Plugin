package com.juansecu.openfusion.openfusionopenapiplugin.verificationtokens.interceptors;

import java.util.UUID;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.juansecu.openfusion.openfusionopenapiplugin.accounts.AccountsService;
import com.juansecu.openfusion.openfusionopenapiplugin.accounts.models.entities.AccountEntity;
import com.juansecu.openfusion.openfusionopenapiplugin.shared.utils.CryptoUtil;
import com.juansecu.openfusion.openfusionopenapiplugin.verificationtokens.VerificationTokensService;
import com.juansecu.openfusion.openfusionopenapiplugin.verificationtokens.enums.EVerificationTokenType;

@RequiredArgsConstructor
public class VerificationTokenInterceptor implements HandlerInterceptor {
    private static final Logger CONSOLE_LOGGER = LogManager.getLogger(VerificationTokenInterceptor.class);

    private final AccountsService accountsService;
    private final CryptoUtil cryptoUtil;
    private final UserDetailsService userDetailsService;

    @Override
    public void postHandle(
        final HttpServletRequest request,
        final HttpServletResponse response,
        final Object handler,
        final ModelAndView modelAndView
    ) {
        VerificationTokenInterceptor.CONSOLE_LOGGER.info(
            "Post-intercepting token verification request..."
        );

        final AccountEntity account = (AccountEntity) request.getAttribute("account");
        final boolean isInvalidToken = (boolean) request.getAttribute(
            VerificationTokensService.IS_INVALID_TOKEN_ATTRIBUTE_KEY
        );
        final EVerificationTokenType verificationTokenType = EVerificationTokenType.valueOf(
            request.getParameter("type")
        );

        if (isInvalidToken) {
            // TODO: Create a view for invalid token
            //modelAndView.setViewName("redirect:/verification-tokens/invalid");
            return;
        }

        if (verificationTokenType == EVerificationTokenType.EMAIL_VERIFICATION_TOKEN) {
            VerificationTokenInterceptor.CONSOLE_LOGGER.info(
                "Setting account as verified..."
            );

            account.setVerified(true);

            VerificationTokenInterceptor.CONSOLE_LOGGER.info(
                "Setting {}'s account as verified...",
                account.getUsername()
            );

            this.accountsService.setAccountAsVerified(true, account);

            VerificationTokenInterceptor.CONSOLE_LOGGER.info(
                "{}'s account has been verified successfully...",
                account.getUsername()
            );

            // TODO: Create a view for verified token
            //modelAndView.setViewName("redirect:/verification-tokens/verified");
        }
    }

    @Override
    public boolean preHandle(
        final HttpServletRequest request,
        final HttpServletResponse response,
        final Object handler
    ) {
        VerificationTokenInterceptor.CONSOLE_LOGGER.info(
            "Pre-intercepting token verification request..."
        );

        final AccountEntity account = (AccountEntity) this.userDetailsService.loadUserByUsername(
            request.getParameter("username")
        );
        final UUID decryptedToken = UUID.fromString(
            this.cryptoUtil.decrypt(request.getParameter("token"))
        );

        request.setAttribute("account", account);
        request.setAttribute("decryptedToken", decryptedToken);

        return true;
    }
}
