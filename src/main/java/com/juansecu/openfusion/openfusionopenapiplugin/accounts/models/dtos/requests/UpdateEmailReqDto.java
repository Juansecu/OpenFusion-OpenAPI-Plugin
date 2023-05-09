package com.juansecu.openfusion.openfusionopenapiplugin.accounts.models.dtos.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateEmailReqDto {
    @Email(
        message = "User e-mail must be a valid e-mail address",
        regexp = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$"
    )
    @NotEmpty(
        message = "E-mail cannot be empty"
    )
    @Size(
        max = 50,
        message = "E-mail too large! Max allowed length is 50 characters"
    )
    private String email;
}
