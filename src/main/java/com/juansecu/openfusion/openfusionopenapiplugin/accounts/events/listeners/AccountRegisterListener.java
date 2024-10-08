package com.juansecu.openfusion.openfusionopenapiplugin.accounts.events.listeners;

import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import com.juansecu.openfusion.openfusionopenapiplugin.accounts.events.AccountRegisterEvent;
import com.juansecu.openfusion.openfusionopenapiplugin.accounts.models.entities.AccountEntity;
import com.juansecu.openfusion.openfusionopenapiplugin.shared.providers.HostDetailsProvider;
import com.juansecu.openfusion.openfusionopenapiplugin.shared.services.EmailService;
import com.juansecu.openfusion.openfusionopenapiplugin.verificationtokens.VerificationTokensService;
import com.juansecu.openfusion.openfusionopenapiplugin.verificationtokens.enums.EVerificationTokenType;
import com.juansecu.openfusion.openfusionopenapiplugin.verificationtokens.models.entities.VerificationTokenEntity;

@Component
@RequiredArgsConstructor
public class AccountRegisterListener implements ApplicationListener<AccountRegisterEvent> {
    private static final Logger CONSOLE_LOGGER = LogManager.getLogger(AccountRegisterListener.class);

    @Value("${mail.new-account-email-verification-message.message}")
    private String newAccountEmailVerificationMessage;
    @Value("${mail.new-account-email-verification-message.subject}")
    private String newAccountEmailVerificationSubject;

    private final EmailService emailService;
    private final HostDetailsProvider hostDetailsProvider;
    private final VerificationTokensService verificationTokensService;

    @Override
    public void onApplicationEvent(final AccountRegisterEvent accountRegisterEvent) {
        final AccountEntity newAccount = accountRegisterEvent.getAccount();
        final VerificationTokenEntity verificationToken = this.verificationTokensService.generateVerificationToken(
            newAccount,
            EVerificationTokenType.EMAIL_VERIFICATION_TOKEN
        );
        final String verificationEmailMessage = this.replaceEmailVerificationParameters(
            this.verificationTokensService.getEncryptedToken(
                verificationToken.getToken().toString()
            ),
            newAccount.getUsername()
        );

        AccountRegisterListener.CONSOLE_LOGGER.info(
            "Sending verification email..."
        );

        if (
            !this.emailService.sendSimpleAccountManagementMessage(
                verificationEmailMessage,
                this.newAccountEmailVerificationSubject,
                newAccount.getEmail()
            )
        ) {
            AccountRegisterListener.CONSOLE_LOGGER.error(
                "Verification email could not be sent"
            );

            return;
        }

        AccountRegisterListener.CONSOLE_LOGGER.info(
            "Verification email sent"
        );
    }

    private String replaceEmailVerificationParameters(
        final String encryptedToken,
        final String username
    ) {
        return this.newAccountEmailVerificationMessage
            .replace(
                "{hours_to_expire}",
                String.valueOf(
                    VerificationTokensService.HOURS_OF_EXPIRING_EMAIL_VERIFICATION_TOKEN
                )
            )
            .replace("{username}", username)
            .replace(
                "{verify_account_link}",
                this.hostDetailsProvider.getPublicAddress() +
                    "/api/verification-tokens/verify?token=" +
                    encryptedToken +
                    "&type=" +
                    EVerificationTokenType.EMAIL_VERIFICATION_TOKEN +
                    "&username=" +
                    username
            );
    }
}
