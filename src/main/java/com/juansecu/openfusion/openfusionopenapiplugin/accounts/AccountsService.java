package com.juansecu.openfusion.openfusionopenapiplugin.accounts;

import java.util.Objects;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import com.juansecu.openfusion.openfusionopenapiplugin.accounts.enums.EAccountServiceError;
import com.juansecu.openfusion.openfusionopenapiplugin.accounts.models.dtos.requests.UpdateEmailReqDto;
import com.juansecu.openfusion.openfusionopenapiplugin.accounts.models.dtos.requests.UpdatePasswordReqDto;
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
    private final PasswordEncoder passwordEncoder;
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

        boolean passwordMatches;
        String verificationEmailMessage;
        VerificationTokenEntity verificationToken;

        final AccountEntity account = (AccountEntity) request.getAttribute("account");
        final AccountEntity accountWithGivenEmail = this.getAccountByEmail(
            updateEmailReqDto.getEmail()
        );

        if (accountWithGivenEmail != null) {
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

        if (
            account.getEmail().equalsIgnoreCase(updateEmailReqDto.getEmail()) &&
            account.isVerified()
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

        passwordMatches = this.passwordEncoder.matches(
            updateEmailReqDto.getPassword(),
            account.getPassword()
        );

        if (!passwordMatches) {
            AccountsService.CONSOLE_LOGGER.info(
                "Password does not match"
            );

            return new ResponseEntity<>(
                new BasicResDto(
                    false,
                    EAccountServiceError.PASSWORD_DOES_NOT_MATCH,
                    "Incorrect password",
                    null
                ),
                HttpStatus.UNAUTHORIZED
            );
        }

        if (!account.getEmail().equalsIgnoreCase(updateEmailReqDto.getEmail())) {
            AccountsService.CONSOLE_LOGGER.info(
                "Updating email address..."
            );

            account.setEmail(updateEmailReqDto.getEmail());

            this.setAccountAsVerified(false, account);

            AccountsService.CONSOLE_LOGGER.info(
                "Email address updated successfully"
            );
        }

        verificationToken = this.verificationTokensService.generateEmailVerificationToken(
            account
        );
        verificationEmailMessage = this.replaceUpdateEmailParameters(
            this.verificationTokensService.getEncryptedToken(
                verificationToken.getToken().toString()
            ),
            account.getUsername()
        );

        if (
            !this.emailService.sendSimpleMessage(
                verificationEmailMessage,
                this.updateAccountEmailMessageSubject,
                account.getEmail()
            )
        ) {
            AccountsService.CONSOLE_LOGGER.info(
                "Verification email could not be sent"
            );

            return new ResponseEntity<>(
                new BasicResDto(
                    false,
                    EAccountServiceError.COULD_NOT_SEND_VERIFICATION_EMAIL,
                    "Internal server error occurred. Please try again later",
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

    public String updateEmail(
        final UpdateEmailReqDto updateEmailReqDto,
        final BindingResult bindingResult,
        final Model model,
        final HttpServletRequest request
    ) {
        if (bindingResult.hasErrors())
            return "email-preferences";

        final ResponseEntity<BasicResDto> updateEmailResponse = this.updateEmail(
            updateEmailReqDto,
            request
        );
        final BasicResDto updateEmailResponseBody = updateEmailResponse.getBody();

        if (!Objects.requireNonNull(updateEmailResponseBody).success()) {
            model.addAttribute(
                "error",
                updateEmailResponseBody.message()
            );

            return "email-preferences";
        }

        model.addAttribute(
            "success",
            "Your email address has been updated successfully. Please check your inbox for the verification email"
        );

        return "email-preferences";
    }

    public ResponseEntity<BasicResDto> updatePassword(
        final UpdatePasswordReqDto updatePasswordReqDto,
        final HttpServletRequest request
    ) {
        AccountsService.CONSOLE_LOGGER.info(
            "Updating password for user {}...",
            ((AccountEntity) request.getAttribute("account")).getUsername()
        );

        final AccountEntity account = (AccountEntity) request.getAttribute("account");
        final boolean isSamePassword = this.passwordEncoder.matches(
            updatePasswordReqDto.getNewPassword(),
            account.getPassword()
        );
        final boolean passwordMatches = this.passwordEncoder.matches(
            updatePasswordReqDto.getCurrentPassword(),
            account.getPassword()
        );

        if (!passwordMatches) {
            AccountsService.CONSOLE_LOGGER.info(
                "Current password does not match"
            );

            return new ResponseEntity<>(
                new BasicResDto(
                    false,
                    EAccountServiceError.PASSWORD_DOES_NOT_MATCH,
                    "Wrong current password",
                    null
                ),
                HttpStatus.UNAUTHORIZED
            );
        }

        if (isSamePassword) {
            AccountsService.CONSOLE_LOGGER.info(
                "New password is the same than the current one"
            );

            return new ResponseEntity<>(
                new BasicResDto(
                    false,
                    EAccountServiceError.SAME_PASSWORD,
                    "New password cannot be the same than the current one",
                    null
                ),
                HttpStatus.CONFLICT
            );
        }

        account.setPassword(
            this.passwordEncoder.encode(
                updatePasswordReqDto.getNewPassword()
            )
        );

        this.accountsRepository.save(account);

        AccountsService.CONSOLE_LOGGER.info(
            "Password updated successfully"
        );

        return ResponseEntity.ok(
            new BasicResDto(
                true,
                null,
                "Password updated successfully",
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
