package com.juansecu.openfusion.openfusionopenapiplugin.accounts.events.listeners;

import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import com.juansecu.openfusion.openfusionopenapiplugin.accounts.events.EmailUpdateEvent;
import com.juansecu.openfusion.openfusionopenapiplugin.accounts.models.entities.AccountEntity;
import com.juansecu.openfusion.openfusionopenapiplugin.shared.providers.HostDetailsProvider;
import com.juansecu.openfusion.openfusionopenapiplugin.shared.services.EmailService;
import com.juansecu.openfusion.openfusionopenapiplugin.verificationtokens.VerificationTokensService;
import com.juansecu.openfusion.openfusionopenapiplugin.verificationtokens.enums.EVerificationTokenType;
import com.juansecu.openfusion.openfusionopenapiplugin.verificationtokens.models.entities.VerificationTokenEntity;

@Component
@RequiredArgsConstructor
public class EmailUpdateListener implements ApplicationListener<EmailUpdateEvent> {
    private static final Logger CONSOLE_LOGGER = LogManager.getLogger(EmailUpdateListener.class);

    @Value("${mail.update-account-email-verification-message.message}")
    private String updateAccountEmailMessage;
    @Value("${mail.update-account-email-verification-message.subject}")
    private String updateAccountEmailMessageSubject;

    private final EmailService emailService;
    private final HostDetailsProvider hostDetailsProvider;
    private final VerificationTokensService verificationTokensService;

    @Override
    public void onApplicationEvent(final EmailUpdateEvent emailUpdateEvent) {
        final AccountEntity account = emailUpdateEvent.getAccount();
        final VerificationTokenEntity verificationToken = this.verificationTokensService.generateVerificationToken(
            account,
            EVerificationTokenType.EMAIL_VERIFICATION_TOKEN
        );
        final String verificationEmailMessage = this.replaceUpdateEmailParameters(
            this.verificationTokensService.getEncryptedToken(
                verificationToken.getToken().toString()
            ),
            account.getUsername()
        );

        EmailUpdateListener.CONSOLE_LOGGER.info(
            "Sending verification email..."
        );

        if (
            !this.emailService.sendSimpleAccountManagementMessage(
                verificationEmailMessage,
                this.updateAccountEmailMessageSubject,
                account.getEmail()
            )
        ) {
            EmailUpdateListener.CONSOLE_LOGGER.error(
                "Verification email could not be sent"
            );

            return;
        }

        EmailUpdateListener.CONSOLE_LOGGER.info(
            "Verification email sent"
        );
    }

    private String replaceUpdateEmailParameters(
        final String encryptedToken,
        final String username
    ) {
        return this.updateAccountEmailMessage
            .replace(
                "{hours_to_expire}",
                String.valueOf(
                    VerificationTokensService.HOURS_OF_EXPIRING_EMAIL_VERIFICATION_TOKEN
                )
            )
            .replace(
                "{update_email_link}",
                this.hostDetailsProvider.getPublicAddress() +
                    "/api/verification-tokens/verify?token=" +
                    encryptedToken +
                    "&type=" +
                    EVerificationTokenType.EMAIL_VERIFICATION_TOKEN +
                    "&username=" +
                    username
            )
            .replace("{username}", username);
    }
}
