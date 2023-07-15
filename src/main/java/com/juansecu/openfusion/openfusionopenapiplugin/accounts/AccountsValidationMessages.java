package com.juansecu.openfusion.openfusionopenapiplugin.accounts;

public final class AccountsValidationMessages {
    public static final String INVALID_PASSWORD_LENGTH =
        "Invalid password length! It must be between " +
        AccountsValidations.MIN_PASSWORD_LENGTH +
        " and " +
        AccountsValidations.MAX_PASSWORD_LENGTH +
        " characters";
    public static final String INVALID_USERNAME =
        "Invalid username. Username can only contain alphanumeric characters, including - and _ chars";
    public static final String INVALID_USERNAME_LENGTH =
        "Invalid username length! It must be between " +
        AccountsValidations.MIN_USERNAME_LENGTH +
        " and " +
        AccountsValidations.MAX_USERNAME_LENGTH +
        " characters";
    public static final String NOT_EMPTY_EMAIL = "E-mail cannot be empty";
    public static final String NOT_EMPTY_PASSWORD = "Password cannot be empty";
    public static final String NOT_EMPTY_USERNAME = "Username cannot be empty";
    public static final String TOO_LARGE_EMAIL =
        "E-mail too large! Max allowed length is " +
        AccountsValidations.MAX_EMAIL_LENGTH +
        " characters";
    public static final String VALID_EMAIL = "E-mail must be a valid e-mail address";

    private AccountsValidationMessages() {}
}
