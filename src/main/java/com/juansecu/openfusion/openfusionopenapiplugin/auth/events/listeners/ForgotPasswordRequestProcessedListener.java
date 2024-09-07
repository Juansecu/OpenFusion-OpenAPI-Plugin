package com.juansecu.openfusion.openfusionopenapiplugin.auth.events.listeners;

import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import com.juansecu.openfusion.openfusionopenapiplugin.accounts.models.entities.AccountEntity;
import com.juansecu.openfusion.openfusionopenapiplugin.auth.events.ForgotPasswordRequestProcessedEvent;
import com.juansecu.openfusion.openfusionopenapiplugin.shared.providers.HostDetailsProvider;
import com.juansecu.openfusion.openfusionopenapiplugin.shared.services.EmailService;
import com.juansecu.openfusion.openfusionopenapiplugin.verificationtokens.VerificationTokensService;
import com.juansecu.openfusion.openfusionopenapiplugin.verificationtokens.enums.EVerificationTokenType;
import com.juansecu.openfusion.openfusionopenapiplugin.verificationtokens.models.entities.VerificationTokenEntity;

@Component
@RequiredArgsConstructor
public class ForgotPasswordRequestProcessedListener implements ApplicationListener<ForgotPasswordRequestProcessedEvent> {
    private static final Logger CONSOLE_LOGGER = LogManager.getLogger(ForgotPasswordRequestProcessedListener.class);

    @Value("${mail.reset-password-message.message}")
    private String resetPasswordEmailMessage;
    @Value("${mail.reset-password-message.subject}")
    private String resetPasswordEmailMessageSubject;

    private final EmailService emailService;
    private final HostDetailsProvider hostDetailsProvider;
    private final VerificationTokensService verificationTokensService;

    @Override
    public void onApplicationEvent(
        final ForgotPasswordRequestProcessedEvent forgotPasswordRequestProcessedEvent
    ) {
        final AccountEntity account = forgotPasswordRequestProcessedEvent.getAccount();
        final VerificationTokenEntity verificationToken = this.verificationTokensService.generateVerificationToken(
            account,
            EVerificationTokenType.RESET_PASSWORD_TOKEN
        );
        final String verificationEmailMessage = this.replaceResetPasswordEmailParameters(
            this.verificationTokensService.getEncryptedToken(
                verificationToken.getToken().toString()
            ),
            account.getUsername()
        );

        ForgotPasswordRequestProcessedListener.CONSOLE_LOGGER.info(
            "Sending verification email..."
        );

        if (
            !this.emailService.sendSimpleAccountManagementMessage(
                verificationEmailMessage,
                this.resetPasswordEmailMessageSubject,
                account.getEmail()
            )
        ) {
            ForgotPasswordRequestProcessedListener.CONSOLE_LOGGER.error(
                "Verification email could not be sent"
            );

            return;
        }

        ForgotPasswordRequestProcessedListener.CONSOLE_LOGGER.info(
            "Verification email sent"
        );
    }

    private String replaceResetPasswordEmailParameters(
        final String encryptedToken,
        final String username
    ) {
        return this.resetPasswordEmailMessage
            .replace(
                "{minutes_to_expire}",
                String.valueOf(
                    VerificationTokensService.MINUTES_OF_EXPIRING_RESET_PASSWORD_TOKEN
                )
            )
            .replace(
                "{reset_password_link}",
                this.hostDetailsProvider.getHostPath() +
                    "/api/verification-tokens/verify?token=" +
                    encryptedToken +
                    "&type=" +
                    EVerificationTokenType.RESET_PASSWORD_TOKEN +
                    "&username=" +
                    username
            )
            .replace("{username}", username);
    }
}
