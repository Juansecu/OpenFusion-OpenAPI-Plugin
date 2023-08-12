package com.juansecu.openfusion.openfusionopenapiplugin.verificationtokens.enums;

public enum EVerificationTokenType {
    DELETE_ACCOUNT_TOKEN("DELETE_ACCOUNT_TOKEN"),
    EMAIL_VERIFICATION_TOKEN("EMAIL_VERIFICATION_TOKEN"),
    RESET_PASSWORD_TOKEN("RESET_PASSWORD_TOKEN");

    private final String verificationTokenType;

    EVerificationTokenType(final String tokenType) {
        this.verificationTokenType = tokenType;
    }
}
