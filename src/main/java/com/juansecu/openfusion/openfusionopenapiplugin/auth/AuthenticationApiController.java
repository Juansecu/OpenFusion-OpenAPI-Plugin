package com.juansecu.openfusion.openfusionopenapiplugin.auth;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.juansecu.openfusion.openfusionopenapiplugin.auth.models.dtos.requests.ForgotPasswordReqDto;
import com.juansecu.openfusion.openfusionopenapiplugin.auth.models.dtos.requests.LoginReqDto;
import com.juansecu.openfusion.openfusionopenapiplugin.auth.models.dtos.requests.RegisterReqDto;
import com.juansecu.openfusion.openfusionopenapiplugin.shared.models.dtos.responses.BasicResDto;

@RequestMapping("/api/auth")
@RequiredArgsConstructor
@RestController
@Tag(
    description = "Authentication related operations",
    name = "Authentication"
)
public class AuthenticationApiController {
    private final AuthenticationService authenticationService;

    @ApiResponse(
        content = {
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(
                    implementation = BasicResDto.class
                )
            )
        },
        description = "The account was authenticated successfully.",
        responseCode = "200"
    )
    @ApiResponse(
        content = {
            @Content(
                examples = {
                    @ExampleObject(
                        name = "Wrong credentials",
                        value = "<!-- The login page -->"
                    )
                },
                mediaType = MediaType.TEXT_HTML_VALUE
            )
        },
        description = "The credentials were wrong and the login page was returned.",
        responseCode = "302"
    )
    @ApiResponse(
        content = {
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(
                    implementation = BasicResDto.class
                )
            )
        },
        description = "The credentials were invalid",
        responseCode = "400"
    )
    @Operation(
        description = "Authenticates an account by their given credentials.",
        summary = "Authenticates an account"
    )
    @PostMapping("/login")
    public BasicResDto authenticate(
        @RequestBody
        @Valid
        final LoginReqDto loginReqDto
    ) {
        return this.authenticationService.authenticate(loginReqDto);
    }

    @ApiResponse(
        content = {
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(
                    implementation = BasicResDto.class
                )
            )
        },
        description = "Reset password email verification was sent successfully.",
        responseCode = "200"
    )
    @ApiResponse(
        content = {
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(
                    implementation = BasicResDto.class
                )
            )
        },
        description = "Even the email address or username were invalid.",
        responseCode = "400"
    )
    @ApiResponse(
        content = {
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(
                    implementation = BasicResDto.class
                )
            )
        },
        description = "The account was not found.",
        responseCode = "404"
    )
    @ApiResponse(
        content = {
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(
                    implementation = BasicResDto.class
                )
            )
        },
        description = "The account was not verified.",
        responseCode = "422"
    )
    @Operation(
        description = "Starts the process to reset the password of an account by sending an email to the account's email address with a link to confirm the password reset.",
        summary = "Send an email to reset the password of an account"
    )
    @PostMapping("/forgot-password")
    public ResponseEntity<BasicResDto> forgotPassword(
        @RequestBody
        @Valid
        final ForgotPasswordReqDto forgotPasswordReqDto
    ) {
        return this.authenticationService.forgotPassword(forgotPasswordReqDto);
    }

    @ApiResponse(
        content = {
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(
                    implementation = BasicResDto.class
                )
            )
        },
        description = "The account was registered successfully.",
        responseCode = "200"
    )
    @ApiResponse(
        content = {
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(
                    implementation = BasicResDto.class
                )
            )
        },
        description = "The given information was invalid",
        responseCode = "400"
    )
    @ApiResponse(
        content = {
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(
                    implementation = BasicResDto.class
                )
            )
        },
        description = "The username or email address is already in use.",
        responseCode = "409"
    )
    @Operation(
        description = "Registers an account with the given information and send a verification email.",
        summary = "Registers an account"
    )
    @PostMapping("/register")
    public ResponseEntity<BasicResDto> register(
        @RequestBody
        @Valid
        final RegisterReqDto registerReqDto
    ) {
        return this.authenticationService.register(registerReqDto);
    }
}
