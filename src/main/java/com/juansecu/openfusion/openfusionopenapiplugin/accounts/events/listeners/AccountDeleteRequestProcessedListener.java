package com.juansecu.openfusion.openfusionopenapiplugin.accounts.events.listeners;

import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import com.juansecu.openfusion.openfusionopenapiplugin.accounts.events.AccountDeleteRequestProcessedEvent;
import com.juansecu.openfusion.openfusionopenapiplugin.accounts.models.entities.AccountEntity;
import com.juansecu.openfusion.openfusionopenapiplugin.shared.providers.HostDetailsProvider;
import com.juansecu.openfusion.openfusionopenapiplugin.shared.services.EmailService;
import com.juansecu.openfusion.openfusionopenapiplugin.verificationtokens.VerificationTokensService;
import com.juansecu.openfusion.openfusionopenapiplugin.verificationtokens.enums.EVerificationTokenType;
import com.juansecu.openfusion.openfusionopenapiplugin.verificationtokens.models.entities.VerificationTokenEntity;

@Component
@RequiredArgsConstructor
public class AccountDeleteRequestProcessedListener implements ApplicationListener<AccountDeleteRequestProcessedEvent> {
    private static final Logger CONSOLE_LOGGER = LogManager.getLogger(AccountDeleteRequestProcessedListener.class);

    @Value("${mail.delete-account.request-message.message}")
    private String deleteAccountRequestMessage;
    @Value("${mail.delete-account.request-message.subject}")
    private String deleteAccountRequestSubject;

    private final EmailService emailService;
    private final HostDetailsProvider hostDetailsProvider;
    private final VerificationTokensService verificationTokensService;

    @Override
    public void onApplicationEvent(
        final AccountDeleteRequestProcessedEvent accountDeleteRequestProcessedEvent
    ) {
        final AccountEntity account = accountDeleteRequestProcessedEvent.getAccount();
        final VerificationTokenEntity verificationToken = this.verificationTokensService.generateVerificationToken(
            account,
            EVerificationTokenType.DELETE_ACCOUNT_TOKEN
        );
        final String verificationEmailMessage = this.replaceEmailVerificationParameters(
            this.verificationTokensService.getEncryptedToken(
                verificationToken.getToken().toString()
            ),
            account.getUsername()
        );

        AccountDeleteRequestProcessedListener.CONSOLE_LOGGER.info(
            "Sending verification email..."
        );

        if (
            !this.emailService.sendSimpleMessage(
                verificationEmailMessage,
                this.deleteAccountRequestSubject,
                account.getEmail()
            )
        ) {
            AccountDeleteRequestProcessedListener.CONSOLE_LOGGER.error(
                "Verification email could not be sent"
            );

            return;
        }

        AccountDeleteRequestProcessedListener.CONSOLE_LOGGER.info(
            "Verification email sent"
        );
    }

    private String replaceEmailVerificationParameters(
        final String encryptedToken,
        final String username
    ) {
        return this.deleteAccountRequestMessage
            .replace(
                "{delete_account_link}",
                this.hostDetailsProvider.getHostPath() +
                    "/api/verification-tokens/verify?token=" +
                    encryptedToken +
                    "&type=" +
                    EVerificationTokenType.DELETE_ACCOUNT_TOKEN +
                    "&username=" +
                    username
            )
            .replace(
                "{minutes_to_expire}",
                String.valueOf(
                    VerificationTokensService.MINUTES_OF_EXPIRING_DELETE_ACCOUNT_TOKEN
                )
            )
            .replace("{username}", username);
    }
}
