package com.juansecu.openfusion.openfusionopenapiplugin.shared.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.stereotype.Component;

@Component
public class CryptoUtil {
    @Autowired
    private TextEncryptor textEncryptor;

    public String decrypt(final String encryptedText) {
        return this.textEncryptor.decrypt(encryptedText);
    }

    public String encrypt(final String text) {
        return this.textEncryptor.encrypt(text);
    }
}
