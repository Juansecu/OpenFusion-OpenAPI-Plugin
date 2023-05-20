package com.juansecu.openfusion.openfusionopenapiplugin.auth.models.dtos.requests;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LoginReqDto {
    @Pattern(
        flags = {Pattern.Flag.CASE_INSENSITIVE},
        message = "Invalid username. Username can only contain alphanumeric characters, including - and _ chars",
        regexp = "^[0-9a-z_-]+$"
    )
    @Size(
        max = 32,
        message = "Invalid username length! It must be between 4 and 32 characters",
        min = 4
    )
    private String username;
    @Size(
        max = 32,
        message = "Invalid password length! It must be between 8 and 32 characters",
        min = 8
    )
    private String password;
}
