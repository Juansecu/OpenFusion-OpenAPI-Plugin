package com.juansecu.openfusion.openfusionopenapiplugin.accounts;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.juansecu.openfusion.openfusionopenapiplugin.accounts.models.dtos.requests.DeleteAccountReqDto;
import com.juansecu.openfusion.openfusionopenapiplugin.accounts.models.dtos.requests.UpdateEmailReqDto;
import com.juansecu.openfusion.openfusionopenapiplugin.accounts.models.dtos.requests.UpdatePasswordReqDto;
import com.juansecu.openfusion.openfusionopenapiplugin.shared.models.dtos.responses.BasicResDto;

@RequestMapping("/api/accounts")
@RequiredArgsConstructor
@RestController
@Tag(
    description = "Accounts related operations",
    name = "Accounts"
)
public class AccountsApiController {
    private final AccountsService accountsService;

    @ApiResponse(
        content = {
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(
                    implementation = BasicResDto.class
                )
            )
        },
        description = "The deletion process was started successfully and an email was sent to the account's email address with a link to confirm the deletion.",
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
        description = "The account was not found and the login page was returned.",
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
        description = "The body of the request was not valid.",
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
        description = "Even the account was not verified or the current password was wrong.",
        responseCode = "401"
    )
    @DeleteMapping
    @Operation(
        description = "Starts the process to delete an account by sending an email to the account's email address with a link to confirm the deletion.",
        summary = "Deletes an account"
    )
    @Parameters(
        @Parameter(
            description = "Authorization header with the JSON Web Token.",
            example = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJzandpIiwiaXNzIjoiT3BlbkZ1c2lvbiIsImV4cCI6MTY4OTM5MTUxMn0.4wetQ5YHE_yv3sGwDJdDDdgMP8KFX4Sqwi180jgfpZ8",
            in = ParameterIn.HEADER,
            name = "Authorization",
            required = true,
            schema = @Schema(
                pattern = "Bearer ([0-9a-z_=]+)\\.([0-9a-z_=]+)\\.([0-9a-z_\\-+/=]+)"
            )
        )
    )
    public ResponseEntity<BasicResDto> delete(
        @RequestBody
        @Valid
        final DeleteAccountReqDto deleteAccountReqDto,
        final HttpServletRequest request
    ) {
        return this.accountsService.deleteAccount(
            deleteAccountReqDto,
            request
        );
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
        description = "The email address was updated successfully and an email was sent to the new email address with a link to verify it.",
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
        description = "The account was not found and the login page was returned.",
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
        description = "The body of the request was not valid.",
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
        description = "The current password was wrong.",
        responseCode = "401"
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
        description = "Email address already in use.",
        responseCode = "409"
    )
    @Operation(
        description = "Updates the email address of an account and send a verification email.",
        summary = "Updates the email address of an account"
    )
    @Parameters(
        @Parameter(
            description = "Authorization header with the JSON Web Token.",
            example = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJzandpIiwiaXNzIjoiT3BlbkZ1c2lvbiIsImV4cCI6MTY4OTM5MTUxMn0.4wetQ5YHE_yv3sGwDJdDDdgMP8KFX4Sqwi180jgfpZ8",
            in = ParameterIn.HEADER,
            name = "Authorization",
            required = true,
            schema = @Schema(
                pattern = "Bearer ([0-9a-z_=]+)\\.([0-9a-z_=]+)\\.([0-9a-z_\\-+/=]+)"
            )
        )
    )
    @PutMapping("/update-email")
    public ResponseEntity<BasicResDto> updateEmail(
        @RequestBody
        @Valid
        final UpdateEmailReqDto updateEmailReqDto,
        final HttpServletRequest request
    ) {
        return this.accountsService.updateEmail(updateEmailReqDto, request);
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
        description = "The password was updated successfully.",
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
        description = "The account was not found and the login page was returned.",
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
        description = "The body of the request was not valid.",
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
        description = "The current password was wrong.",
        responseCode = "401"
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
        description = "The new password is the same as the old one",
        responseCode = "409"
    )
    @Operation(
        description = "Updates the password of an account.",
        summary = "Updates the password of an account"
    )
    @Parameters(
        @Parameter(
            description = "Authorization header with the JSON Web Token.",
            example = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJzandpIiwiaXNzIjoiT3BlbkZ1c2lvbiIsImV4cCI6MTY4OTM5MTUxMn0.4wetQ5YHE_yv3sGwDJdDDdgMP8KFX4Sqwi180jgfpZ8",
            in = ParameterIn.HEADER,
            name = "Authorization",
            required = true,
            schema = @Schema(
                pattern = "Bearer ([0-9a-z_=]+)\\.([0-9a-z_=]+)\\.([0-9a-z_\\-+/=]+)"
            )
        )
    )
    @PutMapping("/update-password")
    public ResponseEntity<BasicResDto> updatePassword(
        @RequestBody
        @Valid
        final UpdatePasswordReqDto updatePasswordReqDto,
        final HttpServletRequest request
    ) {
        return this.accountsService.updatePassword(updatePasswordReqDto, request);
    }
}
