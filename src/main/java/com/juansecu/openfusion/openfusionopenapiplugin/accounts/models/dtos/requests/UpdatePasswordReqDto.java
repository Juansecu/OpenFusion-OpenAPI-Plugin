package com.juansecu.openfusion.openfusionopenapiplugin.accounts.models.dtos.requests;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

import com.juansecu.openfusion.openfusionopenapiplugin.accounts.AccountsValidationMessages;
import com.juansecu.openfusion.openfusionopenapiplugin.accounts.AccountsValidations;

@Data
public class UpdatePasswordReqDto {
    @NotEmpty(message = AccountsValidationMessages.NOT_EMPTY_PASSWORD)
    @Size(
        max = AccountsValidations.MAX_PASSWORD_LENGTH,
        message = AccountsValidationMessages.INVALID_PASSWORD_LENGTH,
        min = AccountsValidations.MIN_PASSWORD_LENGTH
    )
    private String currentPassword;
    @NotEmpty(message = AccountsValidationMessages.NOT_EMPTY_PASSWORD)
    @Size(
        max = AccountsValidations.MAX_PASSWORD_LENGTH,
        message = AccountsValidationMessages.INVALID_PASSWORD_LENGTH,
        min = AccountsValidations.MIN_PASSWORD_LENGTH
    )
    private String newPassword;
}
