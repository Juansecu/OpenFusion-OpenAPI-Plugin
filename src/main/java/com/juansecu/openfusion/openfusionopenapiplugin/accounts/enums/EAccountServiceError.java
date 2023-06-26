package com.juansecu.openfusion.openfusionopenapiplugin.accounts.enums;

public enum EAccountServiceError {
    ACCOUNT_ALREADY_VERIFIED("ACCOUNT_ALREADY_VERIFIED"),
    COULD_NOT_SEND_VERIFICATION_EMAIL("COULD_NOT_SEND_VERIFICATION_EMAIL"),
    EMAIL_IN_USE("EMAIL_IN_USE"),
    PASSWORD_DOES_NOT_MATCH("PASSWORD_DOES_NOT_MATCH"),
    SAME_EMAIL("SAME_EMAIL"),
    USERNAME_IN_USE("USERNAME_IN_USE");

    public final String accountServiceError;

    EAccountServiceError(final String accountServiceError) {
        this.accountServiceError = accountServiceError;
    }
}
