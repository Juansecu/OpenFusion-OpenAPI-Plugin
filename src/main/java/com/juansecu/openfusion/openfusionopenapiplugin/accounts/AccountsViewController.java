package com.juansecu.openfusion.openfusionopenapiplugin.accounts;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.juansecu.openfusion.openfusionopenapiplugin.accounts.models.dtos.requests.UpdateEmailReqDto;

@Controller
@RequestMapping("/accounts")
@RequiredArgsConstructor
public class AccountsViewController {
    private final AccountsService accountsService;

    @GetMapping("/email-preferences")
    public String emailPreferences(
        final UpdateEmailReqDto updateEmailReqDto,
        final Model model,
        final HttpServletRequest request
    ) {
        model.addAttribute(
            "requestUri",
            request.getRequestURI()
        );

        return "email-preferences";
    }

    @PostMapping(
        consumes = "application/x-www-form-urlencoded",
        path = "/email-preferences"
    )
    public String updateEmail(
        final UpdateEmailReqDto updateEmailReqDto,
        final BindingResult bindingResult,
        final Model model,
        final HttpServletRequest request
    ) {
        model.addAttribute(
            "requestUri",
            request.getRequestURI()
        );

        return this.accountsService.updateEmail(
            updateEmailReqDto,
            bindingResult,
            model,
            request
        );
    }
}
