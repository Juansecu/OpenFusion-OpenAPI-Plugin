package com.juansecu.openfusion.openfusionopenapiplugin.verificationtokens;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.juansecu.openfusion.openfusionopenapiplugin.verificationtokens.enums.EVerificationTokenType;

@RequestMapping("/api/verification-tokens")
@RequiredArgsConstructor
@RestController
@Tag(
    description = "Verification tokens related operations",
    name = "Verification Tokens"
)
public class VerificationTokensApiController {
    private final VerificationTokensService verificationTokensService;

    @ApiResponse(
        description = "Even the token is invalid or not, there will be a redirection to the client's URL.",
        responseCode = "302"
    )
    @GetMapping("/verify")
    @Operation(
        description = "Verifies a token and redirects to the client's URL.",
        summary = "Verifies a token"
    )
    public void verifyToken(
        @RequestParam("token") final String token,
        @RequestParam("type") final EVerificationTokenType verificationTokenType,
        @RequestParam("username") final String username,
        final HttpServletRequest request
    ) {
        this.verificationTokensService.verifyToken(
            verificationTokenType,
            username,
            request
        );
    }
}
