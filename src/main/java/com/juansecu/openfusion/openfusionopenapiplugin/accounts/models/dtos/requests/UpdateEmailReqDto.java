package com.juansecu.openfusion.openfusionopenapiplugin.accounts.models.dtos.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

import com.juansecu.openfusion.openfusionopenapiplugin.accounts.AccountsValidationMessages;
import com.juansecu.openfusion.openfusionopenapiplugin.accounts.AccountsValidations;

@Data
public class UpdateEmailReqDto {
    @Email(
        message = AccountsValidationMessages.VALID_EMAIL,
        regexp = AccountsValidations.EMAIL_PATTERN
    )
    @NotEmpty(
        message = AccountsValidationMessages.NOT_EMPTY_EMAIL
    )
    @Size(
        max = AccountsValidations.MAX_EMAIL_LENGTH,
        message = AccountsValidationMessages.TOO_LARGE_EMAIL
    )
    private String email;
    @Size(
        max = AccountsValidations.MAX_PASSWORD_LENGTH,
        message = AccountsValidationMessages.INVALID_PASSWORD_LENGTH,
        min = AccountsValidations.MIN_PASSWORD_LENGTH
    )
    private String password;
}
