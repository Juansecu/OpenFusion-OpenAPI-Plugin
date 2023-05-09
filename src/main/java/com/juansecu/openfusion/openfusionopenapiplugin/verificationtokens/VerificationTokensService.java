package com.juansecu.openfusion.openfusionopenapiplugin.verificationtokens;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.juansecu.openfusion.openfusionopenapiplugin.accounts.models.entities.AccountEntity;
import com.juansecu.openfusion.openfusionopenapiplugin.shared.models.dtos.responses.BasicResDto;
import com.juansecu.openfusion.openfusionopenapiplugin.shared.utils.CryptoUtil;
import com.juansecu.openfusion.openfusionopenapiplugin.shared.utils.TimeUtil;
import com.juansecu.openfusion.openfusionopenapiplugin.verificationtokens.enums.EVerificationTokenServiceError;
import com.juansecu.openfusion.openfusionopenapiplugin.verificationtokens.enums.EVerificationTokenType;
import com.juansecu.openfusion.openfusionopenapiplugin.verificationtokens.models.entities.VerificationTokenEntity;
import com.juansecu.openfusion.openfusionopenapiplugin.verificationtokens.repositories.IVerificationTokensRepository;

@RequiredArgsConstructor
@Service
public class VerificationTokensService {
    public static final int HOURS_OF_EXPIRING_EMAIL_VERIFICATION_TOKEN = 24;
    public static final String IS_INVALID_TOKEN_ATTRIBUTE_KEY = "isInvalidToken";

    private static final Logger CONSOLE_LOGGER = LogManager.getLogger(VerificationTokensService.class);

    private final CryptoUtil cryptoUtil;
    private final IVerificationTokensRepository verificationTokensRepository;

    public VerificationTokenEntity generateEmailVerificationToken(
        final AccountEntity account
    ) {
        VerificationTokensService.CONSOLE_LOGGER.info(
            "Generating reset password verification token..."
        );

        final UUID token = UUID.randomUUID();
        final LocalDateTime tokenExpirationDateTime = LocalDateTime
            .now()
            .plusHours(
                VerificationTokensService.HOURS_OF_EXPIRING_EMAIL_VERIFICATION_TOKEN
            );
        final long tokenExpirationSeconds = TimeUtil.localDateTimeToSeconds(
            tokenExpirationDateTime
        );
        final VerificationTokenEntity verificationToken = new VerificationTokenEntity(
            token,
            EVerificationTokenType.EMAIL_VERIFICATION_TOKEN,
            account,
            tokenExpirationSeconds
        );

        VerificationTokensService.CONSOLE_LOGGER.info(
            "Saving reset password verification token..."
        );

        return this.verificationTokensRepository.save(verificationToken);
    }

    public String getEncryptedToken(final String decryptedToken) {
        return this.cryptoUtil.encrypt(decryptedToken);
    }

    public ResponseEntity<BasicResDto> verifyToken(
        final EVerificationTokenType verificationTokenType,
        final String username,
        final HttpServletRequest request
    ) {
        VerificationTokensService.CONSOLE_LOGGER.info(
            String.format(
                "Verifying %s for user %s%s",
                verificationTokenType,
                username,
                "..."
            )
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

            return new ResponseEntity<>(
                new BasicResDto(
                    false,
                    EVerificationTokenServiceError.TOKEN_NOT_FOUND,
                    "Verification token not found",
                    null
                ),
                HttpStatus.NOT_FOUND
            );
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

            return new ResponseEntity<>(
                new BasicResDto(
                    false,
                    EVerificationTokenServiceError.TOKEN_EXPIRED,
                    "Verification token expired",
                    null
                ),
                HttpStatus.BAD_REQUEST
            );
        }

        VerificationTokensService.CONSOLE_LOGGER.info(
            "Verification token validated successfully"
        );

        request.setAttribute(
            VerificationTokensService.IS_INVALID_TOKEN_ATTRIBUTE_KEY,
            false
        );

        return new ResponseEntity<>(
            new BasicResDto(
                true,
                null,
                "Verification token validated successfully",
                null
            ),
            HttpStatus.OK
        );
    }
}
