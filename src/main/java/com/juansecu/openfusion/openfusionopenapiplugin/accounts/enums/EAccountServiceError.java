package com.juansecu.openfusion.openfusionopenapiplugin.accounts.enums;

public enum EAccountServiceError {
    ACCOUNT_NOT_VERIFIED("ACCOUNT_NOT_VERIFIED"),
    EMAIL_IN_USE("EMAIL_IN_USE"),
    EMPTY_EMAIL_AND_USERNAME("EMPTY_EMAIL_AND_USERNAME"),
    NOT_LINKED_EMAIL("NOT_LINKED_EMAIL"),
    PASSWORD_DOES_NOT_MATCH("PASSWORD_DOES_NOT_MATCH"),
    SAME_EMAIL("SAME_EMAIL"),
    SAME_PASSWORD("SAME_PASSWORD"),
    USER_NOT_FOUND("USER_NOT_FOUND"),
    USERNAME_IN_USE("USERNAME_IN_USE");

    private final String accountServiceError;

    EAccountServiceError(final String accountServiceError) {
        this.accountServiceError = accountServiceError;
    }
}
