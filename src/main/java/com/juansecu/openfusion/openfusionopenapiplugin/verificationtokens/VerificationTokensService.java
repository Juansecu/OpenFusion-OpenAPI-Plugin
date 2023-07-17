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

        final AccountEntity account = (AccountEntity) request.getAttribute("account");
        final VerificationTokenEntity verificationToken = this.verificationTokensRepository
            .findByTokenAndTypeAndAccount(
                (UUID) request.getAttribute("decryptedToken"),
                verificationTokenType,
                account
            )
            .orElse(null);

        if (verificationToken == null) {
            VerificationTokensService.CONSOLE_LOGGER.info(
                "Verification token not found"
            );

            request.setAttribute(
                VerificationTokensService.IS_INVALID_TOKEN_ATTRIBUTE_KEY,
                true
            );

            return;
        }

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

        VerificationTokensService.CONSOLE_LOGGER.info(
            "Verification token validated successfully"
        );

        request.setAttribute(
            VerificationTokensService.IS_INVALID_TOKEN_ATTRIBUTE_KEY,
            false
        );
    }
}
