package com.juansecu.openfusion.openfusionopenapiplugin.accounts;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.juansecu.openfusion.openfusionopenapiplugin.accounts.enums.EAccountServiceError;
import com.juansecu.openfusion.openfusionopenapiplugin.accounts.models.dtos.requests.UpdateEmailReqDto;
import com.juansecu.openfusion.openfusionopenapiplugin.accounts.models.entities.AccountEntity;
import com.juansecu.openfusion.openfusionopenapiplugin.accounts.repositories.IAccountsRepository;
import com.juansecu.openfusion.openfusionopenapiplugin.shared.models.dtos.responses.BasicResDto;
import com.juansecu.openfusion.openfusionopenapiplugin.shared.providers.HostDetailsProvider;
import com.juansecu.openfusion.openfusionopenapiplugin.shared.services.EmailService;
import com.juansecu.openfusion.openfusionopenapiplugin.verificationtokens.VerificationTokensService;
import com.juansecu.openfusion.openfusionopenapiplugin.verificationtokens.enums.EVerificationTokenType;
import com.juansecu.openfusion.openfusionopenapiplugin.verificationtokens.models.entities.VerificationTokenEntity;

@RequiredArgsConstructor
@Service
public class AccountsService {
    private static final Logger CONSOLE_LOGGER = LogManager.getLogger(AccountsService.class);

    @Value("${mail.update-account-email-verification-message.message}")
    private String updateAccountEmailMessage;
    @Value("${mail.update-account-email-verification-message.subject}")
    private String updateAccountEmailMessageSubject;

    private final IAccountsRepository accountsRepository;
    private final EmailService emailService;
    private final HostDetailsProvider hostDetailsProvider;
    private final VerificationTokensService verificationTokensService;

    public AccountEntity getAccountByEmail(final String email) {
        return this.accountsRepository
            .findByEmailIgnoreCase(email)
            .orElse(null);
    }

    public AccountEntity getAccountByUsername(final String username) {
        return this.accountsRepository
            .findByUsernameIgnoreCase(username)
            .orElse(null);
    }

    public void setAccountAsVerified(final boolean shouldVerifyAccount, final AccountEntity account) {
        account.setVerified(shouldVerifyAccount);
        this.accountsRepository.save(account);
    }

    public ResponseEntity<BasicResDto> updateEmail(
        final UpdateEmailReqDto updateEmailReqDto,
        final HttpServletRequest request
    ) {
        AccountsService.CONSOLE_LOGGER.info(
            "Updating email for user {}...",
            ((AccountEntity) request.getAttribute("account")).getUsername()
        );

        AccountEntity user;
        String verificationEmailMessage;
        VerificationTokenEntity verificationToken;

        final AccountEntity account = (AccountEntity) request.getAttribute("account");
        final AccountEntity accountWithGivenEmail = this.getAccountByEmail(
            updateEmailReqDto.getEmail()
        );

        if (
            accountWithGivenEmail != null &&
            !accountWithGivenEmail.getUsername().equalsIgnoreCase(
                account.getUsername()
            )
        ) {
            AccountsService.CONSOLE_LOGGER.info(
                "Email is already in use"
            );

            return new ResponseEntity<>(
                new BasicResDto(
                    false,
                    EAccountServiceError.EMAIL_IN_USE,
                    "Email is already in use",
                    null
                ),
                HttpStatus.CONFLICT
            );
        }

        user = this.getAccountByUsername(
            ((AccountEntity) request.getAttribute("account")).getUsername()
        );

        if (user == null) {
            AccountsService.CONSOLE_LOGGER.info(
                "User not found"
            );

            return new ResponseEntity<>(
                new BasicResDto(
                    false,
                    EAccountServiceError.USER_NOT_FOUND,
                    "User not found",
                    null
                ),
                HttpStatus.NOT_FOUND
            );
        }

        if (
            user.getEmail().equalsIgnoreCase(updateEmailReqDto.getEmail()) &&
            user.isVerified()
        ) {
            AccountsService.CONSOLE_LOGGER.info(
                "Email is the same than the current one"
            );

            return new ResponseEntity<>(
                new BasicResDto(
                    false,
                    EAccountServiceError.SAME_EMAIL,
                    "Email address cannot be the same than the current one",
                    null
                ),
                HttpStatus.CONFLICT
            );
        }

        if (!user.getEmail().equalsIgnoreCase(updateEmailReqDto.getEmail())) {
            AccountsService.CONSOLE_LOGGER.info(
                "Updating email address..."
            );

            user.setEmail(updateEmailReqDto.getEmail());

            this.setAccountAsVerified(false, user);

            AccountsService.CONSOLE_LOGGER.info(
                "Email address updated successfully"
            );
        }

        verificationToken = this.verificationTokensService.generateEmailVerificationToken(
            user
        );
        verificationEmailMessage = this.replaceUpdateEmailParameters(
            this.verificationTokensService.getEncryptedToken(
                verificationToken.getToken().toString()
            ),
            user.getUsername()
        );

        if (
            !this.emailService.sendSimpleMessage(
                verificationEmailMessage,
                this.updateAccountEmailMessageSubject,
                user.getEmail()
            )
        ) {
            AccountsService.CONSOLE_LOGGER.info(
                "Verification email could not be sent"
            );

            return new ResponseEntity<>(
                new BasicResDto(
                    false,
                    EAccountServiceError.COULD_NOT_SEND_VERIFICATION_EMAIL,
                    "Verification email could not be sent",
                    null
                ),
                HttpStatus.INTERNAL_SERVER_ERROR
            );
        }

        return ResponseEntity.ok(
            new BasicResDto(
                true,
                null,
                "Email updated successfully. Verification email sent",
                null
            )
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
                this.hostDetailsProvider.getHostPath() +
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
