package com.juansecu.openfusion.openfusionopenapiplugin.accounts.enums;

public enum EAccountServiceError {
    COULD_NOT_SEND_VERIFICATION_EMAIL("COULD_NOT_SEND_VERIFICATION_EMAIL"),
    EMAIL_IN_USE("EMAIL_IN_USE"),
    SAME_EMAIL("SAME_EMAIL"),
    USER_NOT_FOUND("USER_NOT_FOUND");

    public final String accountServiceError;

    EAccountServiceError(final String accountServiceError) {
        this.accountServiceError = accountServiceError;
    }
}
