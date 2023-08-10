package com.juansecu.openfusion.openfusionopenapiplugin.accounts.events.listeners;

import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import com.juansecu.openfusion.openfusionopenapiplugin.accounts.events.PasswordChangeEvent;
import com.juansecu.openfusion.openfusionopenapiplugin.accounts.models.entities.AccountEntity;
import com.juansecu.openfusion.openfusionopenapiplugin.shared.services.EmailService;

@Component
@RequiredArgsConstructor
public class PasswordChangeListener implements ApplicationListener<PasswordChangeEvent> {
    private static final Logger CONSOLE_LOGGER = LogManager.getLogger(PasswordChangeListener.class);

    @Value("${mail.change-password-message.message}")
    private String changePasswordMessage;
    @Value("${mail.change-password-message.subject}")
    private String changePasswordMessageSubject;

    private final EmailService emailService;

    @Override
    public void onApplicationEvent(
        final PasswordChangeEvent passwordChangeEvent
    ) {
        final AccountEntity account = passwordChangeEvent.getAccount();
        final String passwordChangeNotificationMessage = this.replaceChangePasswordParameters(
            account.getUsername()
        );

        PasswordChangeListener.CONSOLE_LOGGER.info(
            "Sending password changed email notification..."
        );

        if (
            !this.emailService.sendSimpleMessage(
                passwordChangeNotificationMessage,
                this.changePasswordMessageSubject,
                account.getEmail()
            )
        ) {
            PasswordChangeListener.CONSOLE_LOGGER.error(
                "Password changed email notification could not be sent"
            );

            return;
        }

        PasswordChangeListener.CONSOLE_LOGGER.info(
            "Password changed email notification sent successfully"
        );
    }

    private String replaceChangePasswordParameters(final String username) {
        return this.changePasswordMessage.replace("{username}", username);
    }
}
