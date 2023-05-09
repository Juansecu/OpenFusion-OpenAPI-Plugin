package com.juansecu.openfusion.openfusionopenapiplugin.core.checkers;

import java.security.KeyException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class TextEncryptorKeysChecker implements InitializingBean {
    private static final Logger CONSOLE_LOGGER = LogManager.getLogger(TextEncryptorKeysChecker.class);

    @Value("${VERIFICATION_TOKEN_SALT_KEY}")
    private String verificationTokenSalt;
    @Value("${VERIFICATION_TOKEN_SECURITY_KEY}")
    private String verificationTokenSecurityKey;

    @Override
    public void afterPropertiesSet() throws Exception {
        TextEncryptorKeysChecker.CONSOLE_LOGGER.info(
            "Checking text encryptor keys..."
        );

        if (this.verificationTokenSalt.equals(this.verificationTokenSecurityKey)) {
            TextEncryptorKeysChecker.CONSOLE_LOGGER.error(
                "The verification token salt and security key must be different"
            );

            throw new KeyException(
                "The verification token salt and security key must be different"
            );
        }

        TextEncryptorKeysChecker.CONSOLE_LOGGER.info(
            "Text encryptor keys checked successfully"
        );
    }
}
