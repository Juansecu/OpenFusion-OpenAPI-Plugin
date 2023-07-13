package com.juansecu.openfusion.openfusionopenapiplugin.verificationtokens;

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
public class VerificationTokensApiController {
    private final VerificationTokensService verificationTokensService;

    @GetMapping("/verify")
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
