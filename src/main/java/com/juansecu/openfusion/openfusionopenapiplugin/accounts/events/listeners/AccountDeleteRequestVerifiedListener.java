package com.juansecu.openfusion.openfusionopenapiplugin.accounts.events.listeners;

import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import com.juansecu.openfusion.openfusionopenapiplugin.accounts.events.AccountDeleteRequestVerifiedEvent;
import com.juansecu.openfusion.openfusionopenapiplugin.accounts.models.entities.AccountEntity;
import com.juansecu.openfusion.openfusionopenapiplugin.accounts.repositories.IAccountsRepository;
import com.juansecu.openfusion.openfusionopenapiplugin.shared.services.EmailService;

@Component
@RequiredArgsConstructor
public class AccountDeleteRequestVerifiedListener implements ApplicationListener<AccountDeleteRequestVerifiedEvent> {
    private static final Logger CONSOLE_LOGGER = LogManager.getLogger(AccountDeleteRequestVerifiedListener.class);

    @Value("${mail.delete-account.confirmation-message.message}")
    private String deleteAccountConfirmationMessage;
    @Value("${mail.delete-account.confirmation-message.subject}")
    private String deleteAccountConfirmationSubject;

    private final IAccountsRepository accountsRepository;
    private final EmailService emailService;

    @Override
    public void onApplicationEvent(AccountDeleteRequestVerifiedEvent accountDeleteRequestVerifiedEvent) {
        final AccountEntity account = accountDeleteRequestVerifiedEvent.getAccount();
        final String confirmationEmailMessage = this.replaceEmailConfirmationParameters(
            account.getUsername()
        );

        AccountDeleteRequestVerifiedListener.CONSOLE_LOGGER.info(
            "Deleting {}'s account...",
            account.getUsername()
        );

        this.accountsRepository.delete(account);

        AccountDeleteRequestVerifiedListener.CONSOLE_LOGGER.info(
            "{}'s account deleted successfully. Sending confirmation email...",
            account.getUsername()
        );

        if (
            !this.emailService.sendSimpleMessage(
                confirmationEmailMessage,
                this.deleteAccountConfirmationSubject,
                account.getEmail()
            )
        ) {
            AccountDeleteRequestVerifiedListener.CONSOLE_LOGGER.info(
                "Confirmation email could not be sent"
            );

            return;
        }

        AccountDeleteRequestVerifiedListener.CONSOLE_LOGGER.info(
            "Confirmation email sent successfully"
        );
    }

    private String replaceEmailConfirmationParameters(
        final String username
    ) {
        return this.deleteAccountConfirmationMessage.replace(
            "{username}",
            username
        );
    }
}
