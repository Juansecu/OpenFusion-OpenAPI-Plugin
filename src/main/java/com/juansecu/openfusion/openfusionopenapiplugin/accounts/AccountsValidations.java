package com.juansecu.openfusion.openfusionopenapiplugin.accounts;

public final class AccountsValidations {
    public static final String EMAIL_PATTERN = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
    public static final int MAX_EMAIL_LENGTH = 50;
    public static final int MAX_PASSWORD_LENGTH = 32;
    public static final int MAX_USERNAME_LENGTH = 32;
    public static final int MIN_PASSWORD_LENGTH = 8;
    public static final int MIN_USERNAME_LENGTH = 4;
    public static final String USERNAME_PATTERN = "^[0-9a-z_-]+$";

    private AccountsValidations() {}
}
