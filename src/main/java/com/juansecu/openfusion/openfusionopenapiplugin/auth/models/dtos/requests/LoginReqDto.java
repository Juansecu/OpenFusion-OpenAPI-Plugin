package com.juansecu.openfusion.openfusionopenapiplugin.auth.models.dtos.requests;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import com.juansecu.openfusion.openfusionopenapiplugin.accounts.AccountsValidationMessages;
import com.juansecu.openfusion.openfusionopenapiplugin.accounts.AccountsValidations;

@Data
public class LoginReqDto {
    @NotEmpty(message = AccountsValidationMessages.NOT_EMPTY_USERNAME)
    @Pattern(
        flags = {Pattern.Flag.CASE_INSENSITIVE},
        message = AccountsValidationMessages.INVALID_USERNAME,
        regexp = AccountsValidations.USERNAME_PATTERN
    )
    @Size(
        max = AccountsValidations.MAX_USERNAME_LENGTH,
        message = AccountsValidationMessages.INVALID_USERNAME_LENGTH,
        min = AccountsValidations.MIN_USERNAME_LENGTH
    )
    private String username;
    @NotEmpty(message = AccountsValidationMessages.NOT_EMPTY_PASSWORD)
    @Size(
        max = AccountsValidations.MAX_PASSWORD_LENGTH,
        message = AccountsValidationMessages.INVALID_PASSWORD_LENGTH,
        min = AccountsValidations.MIN_PASSWORD_LENGTH
    )
    private String password;
}
