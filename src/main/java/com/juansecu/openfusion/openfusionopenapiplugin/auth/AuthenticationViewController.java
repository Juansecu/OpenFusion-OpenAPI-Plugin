package com.juansecu.openfusion.openfusionopenapiplugin.auth;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.juansecu.openfusion.openfusionopenapiplugin.auth.models.dtos.requests.RegisterReqDto;
import com.juansecu.openfusion.openfusionopenapiplugin.shared.models.dtos.responses.BasicResDto;

@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationViewController {
    private final AuthenticationService authenticationService;

    @GetMapping("/register")
    public String register(RegisterReqDto registerReqDto) {
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
        return this.authenticationService.register(registerReqDto, bindingResult, model);
    }
}
