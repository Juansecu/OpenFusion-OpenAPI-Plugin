package com.juansecu.openfusion.openfusionopenapiplugin.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.juansecu.openfusion.openfusionopenapiplugin.auth.models.dtos.requests.ForgotPasswordReqDto;
import com.juansecu.openfusion.openfusionopenapiplugin.auth.models.dtos.requests.LoginReqDto;
import com.juansecu.openfusion.openfusionopenapiplugin.auth.models.dtos.requests.RegisterReqDto;
import com.juansecu.openfusion.openfusionopenapiplugin.auth.models.dtos.requests.ResetPasswordReqDto;
import com.juansecu.openfusion.openfusionopenapiplugin.verificationtokens.enums.EVerificationTokenType;

@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationViewController {
    private final AuthenticationService authenticationService;

    @GetMapping("/forgot-password")
    public String forgotPassword(
        final ForgotPasswordReqDto forgotPasswordReqDto
    ) {
        return "forgot-password";
    }

    @PostMapping(
        consumes = "application/x-www-form-urlencoded",
        path = "/forgot-password"
    )
    public String forgotPassword(
        @Valid
        final ForgotPasswordReqDto forgotPasswordReqDto,
        final BindingResult bindingResult,
        final Model model
    ) {
        return this.authenticationService.forgotPassword(
            forgotPasswordReqDto,
            bindingResult,
            model
        );
    }

    @GetMapping("/login")
    public String login(final LoginReqDto loginReqDto) {
        return "login";
    }

    @PostMapping(
        consumes = "application/x-www-form-urlencoded",
        path = "/login"
    )
    public String login(
        @Valid
        final LoginReqDto loginReqDto,
        final BindingResult bindingResult,
        final Model model,
        final HttpServletResponse response
    ) {
        return this.authenticationService.authenticate(
            loginReqDto,
            bindingResult,
            model,
            response
        );
    }

    @GetMapping("/logout")
    public String logout(
        final HttpServletResponse response
    ) {
        return this.authenticationService.logout(response);
    }

    @GetMapping("/register")
    public String register(final RegisterReqDto registerReqDto) {
        return "register";
    }

    @PostMapping(
        consumes = "application/x-www-form-urlencoded",
        path = "/register"
    )
    public String register(
        @Valid
        final RegisterReqDto registerReqDto,
        final BindingResult bindingResult,
        final Model model
    ) {
        return this.authenticationService.register(
            registerReqDto,
            bindingResult,
            model
        );
    }

    @GetMapping("/reset-password")
    public String resetPassword(
        final ResetPasswordReqDto resetPasswordReqDto,
        @RequestParam("token") final String token,
        @RequestParam("type") final EVerificationTokenType verificationTokenType,
        @RequestParam("username") final String username,
        final HttpServletRequest request
    ) {
        return this.authenticationService.resetPassword(request);
    }

    @PostMapping(
        consumes = "application/x-www-form-urlencoded",
        path = "/reset-password"
    )
    public String resetPassword(
        @Valid
        final ResetPasswordReqDto resetPasswordReqDto,
        @RequestParam("token") final String token,
        @RequestParam("type") final EVerificationTokenType verificationTokenType,
        @RequestParam("username") final String username,
        final BindingResult bindingResult,
        final Model model,
        final HttpServletRequest request
    ) {
        return this.authenticationService.resetPassword(
            resetPasswordReqDto,
            verificationTokenType,
            username,
            bindingResult,
            model,
            request
        );
    }
}
