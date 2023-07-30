package com.juansecu.openfusion.openfusionopenapiplugin.verificationtokens;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import com.juansecu.openfusion.openfusionopenapiplugin.accounts.models.entities.AccountEntity;
import com.juansecu.openfusion.openfusionopenapiplugin.shared.utils.CryptoUtil;
import com.juansecu.openfusion.openfusionopenapiplugin.shared.utils.TimeUtil;
import com.juansecu.openfusion.openfusionopenapiplugin.verificationtokens.enums.EVerificationTokenType;
import com.juansecu.openfusion.openfusionopenapiplugin.verificationtokens.models.entities.VerificationTokenEntity;
import com.juansecu.openfusion.openfusionopenapiplugin.verificationtokens.repositories.IVerificationTokensRepository;

@RequiredArgsConstructor
@Service
public class VerificationTokensService {
    public static final int HOURS_OF_EXPIRING_EMAIL_VERIFICATION_TOKEN = 24;
    public static final String IS_INVALID_TOKEN_ATTRIBUTE_KEY = "isInvalidToken";
    public static final int MINUTES_OF_EXPIRING_DELETE_ACCOUNT_TOKEN = 10;
    public static final int MINUTES_OF_EXPIRING_RESET_PASSWORD_TOKEN = 10;
    public static final int RESET_PASSWORD_TOKEN_MAX_USES = 2;
    public static final String VERIFICATION_TOKEN_ATTRIBUTE_KEY = "verificationToken";

    private static final Logger CONSOLE_LOGGER = LogManager.getLogger(VerificationTokensService.class);

    private final CryptoUtil cryptoUtil;
    private final IVerificationTokensRepository verificationTokensRepository;

    public VerificationTokenEntity generateVerificationToken(
        final AccountEntity account,
        final EVerificationTokenType verificationTokenType
    ) {
        VerificationTokensService.CONSOLE_LOGGER.info(
            "Generating {}...",
            verificationTokenType
        );

        LocalDateTime tokenExpirationDateTime;
        long tokenExpirationSeconds;

        final UUID token = UUID.randomUUID();

        tokenExpirationDateTime = switch (verificationTokenType) {
            case DELETE_ACCOUNT_TOKEN -> LocalDateTime
                .now()
                .plusMinutes(
                    VerificationTokensService.MINUTES_OF_EXPIRING_DELETE_ACCOUNT_TOKEN
                );
            case EMAIL_VERIFICATION_TOKEN -> LocalDateTime
                .now()
                .plusHours(
                    VerificationTokensService.HOURS_OF_EXPIRING_EMAIL_VERIFICATION_TOKEN
                );
            case RESET_PASSWORD_TOKEN -> LocalDateTime
                .now()
                .plusMinutes(
                    VerificationTokensService.MINUTES_OF_EXPIRING_RESET_PASSWORD_TOKEN
                );
        };

        tokenExpirationSeconds = TimeUtil.localDateTimeToSeconds(
            tokenExpirationDateTime
        );

        VerificationTokensService.CONSOLE_LOGGER.info(
            "Saving {}...",
            verificationTokenType
        );

        return this.verificationTokensRepository.save(
            new VerificationTokenEntity(
                token,
                verificationTokenType,
                account,
                tokenExpirationSeconds
            )
        );
    }

    public String getEncryptedToken(final String decryptedToken) {
        return this.cryptoUtil.encrypt(decryptedToken);
    }

    public VerificationTokenEntity getVerificationToken(
        final String encryptedToken,
        final EVerificationTokenType verificationTokenType,
        final AccountEntity account
    ) {
        VerificationTokensService.CONSOLE_LOGGER.info(
            "Getting {} for user {}...",
            verificationTokenType,
            account.getUsername()
        );

        UUID decryptedToken;

        try {
            decryptedToken = UUID.fromString(
                this.cryptoUtil.decrypt(encryptedToken)
            );
        } catch (final IllegalArgumentException illegalArgumentException) {
            VerificationTokensService.CONSOLE_LOGGER.info(
                "Invalid token"
            );

            return null;
        }

        return this.verificationTokensRepository
            .findByTokenAndTypeAndAccount(
                decryptedToken,
                verificationTokenType,
                account
            )
            .orElse(null);
    }

    public void verifyToken(
        final EVerificationTokenType verificationTokenType,
        final String username,
        final HttpServletRequest request
    ) {
        VerificationTokensService.CONSOLE_LOGGER.info(
            "Verifying {} for user {}...",
            verificationTokenType,
            username
        );

        final VerificationTokenEntity verificationToken = (VerificationTokenEntity) request.getAttribute(
            VerificationTokensService.VERIFICATION_TOKEN_ATTRIBUTE_KEY
        );

        if (
            verificationToken.getExpiresAt() < TimeUtil.localDateTimeToSeconds(
                LocalDateTime.now()
            )
        ) {
            VerificationTokensService.CONSOLE_LOGGER.info(
                "Verification token expired"
            );

            request.setAttribute(
                VerificationTokensService.IS_INVALID_TOKEN_ATTRIBUTE_KEY,
                true
            );

            return;
        }

        if (
            verificationTokenType == EVerificationTokenType.RESET_PASSWORD_TOKEN &&
            verificationToken.getUsesCount() == VerificationTokensService.RESET_PASSWORD_TOKEN_MAX_USES
        ) {
            VerificationTokensService.CONSOLE_LOGGER.info(
                "Verification token already used"
            );

            request.setAttribute(
                VerificationTokensService.IS_INVALID_TOKEN_ATTRIBUTE_KEY,
                true
            );

            return;
        }

        VerificationTokensService.CONSOLE_LOGGER.info(
            "Verification token validated successfully. Updating uses count..."
        );

        verificationToken.setUsesCount(verificationToken.getUsesCount() + 1);

        this.verificationTokensRepository.save(verificationToken);

        request.setAttribute(
            VerificationTokensService.IS_INVALID_TOKEN_ATTRIBUTE_KEY,
            false
        );
    }
}
