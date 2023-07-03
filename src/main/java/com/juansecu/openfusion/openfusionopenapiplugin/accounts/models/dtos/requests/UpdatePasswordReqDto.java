package com.juansecu.openfusion.openfusionopenapiplugin.accounts.models.dtos.requests;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import com.juansecu.openfusion.openfusionopenapiplugin.accounts.AccountsValidationMessages;
import com.juansecu.openfusion.openfusionopenapiplugin.accounts.AccountsValidations;

@Data
public class UpdatePasswordReqDto {
    @Length(
        max = AccountsValidations.MAX_PASSWORD_LENGTH,
        message = AccountsValidationMessages.INVALID_PASSWORD_LENGTH,
        min = AccountsValidations.MIN_PASSWORD_LENGTH
    )
    private String currentPassword;
    @Length(
        max = AccountsValidations.MAX_PASSWORD_LENGTH,
        message = AccountsValidationMessages.INVALID_PASSWORD_LENGTH,
        min = AccountsValidations.MIN_PASSWORD_LENGTH
    )
    private String newPassword;
}
