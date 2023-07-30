package com.juansecu.openfusion.openfusionopenapiplugin.shared.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CryptoUtil {
    private final TextEncryptor textEncryptor;

    public String decrypt(final String encryptedText) {
        return this.textEncryptor.decrypt(encryptedText);
    }

    public String encrypt(final String text) {
        return this.textEncryptor.encrypt(text);
    }
}
