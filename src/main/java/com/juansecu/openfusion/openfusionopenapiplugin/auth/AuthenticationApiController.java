package com.juansecu.openfusion.openfusionopenapiplugin.auth;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
public class AuthenticationApiController {
    private final AuthenticationService authenticationService;

    @PostMapping("/login")
    public BasicResDto authenticate(
        @RequestBody
        @Valid
        final LoginReqDto loginReqDto
    ) {
        return this.authenticationService.authenticate(loginReqDto);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<BasicResDto> forgotPassword(
        @RequestBody
        @Valid
        final ForgotPasswordReqDto forgotPasswordReqDto
    ) {
        return this.authenticationService.forgotPassword(forgotPasswordReqDto);
    }

    @PostMapping("/register")
    public ResponseEntity<BasicResDto> register(
        @RequestBody
        @Valid
        final RegisterReqDto registerReqDto
    ) {
        return this.authenticationService.register(registerReqDto);
    }
}
