package com.juansecu.openfusion.openfusionopenapiplugin.verificationtokens.enums;

public enum EVerificationTokenType {
    EMAIL_VERIFICATION_TOKEN("EMAIL_VERIFICATION_TOKEN"),
    RESET_PASSWORD_TOKEN("RESET_PASSWORD_TOKEN");

    public final String verificationTokenType;

    EVerificationTokenType(final String tokenType) {
        this.verificationTokenType = tokenType;
    }
}
