package com.juansecu.openfusion.openfusionopenapiplugin.accounts;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.juansecu.openfusion.openfusionopenapiplugin.accounts.models.dtos.requests.DeleteAccountReqDto;
import com.juansecu.openfusion.openfusionopenapiplugin.accounts.models.dtos.requests.UpdateEmailReqDto;
import com.juansecu.openfusion.openfusionopenapiplugin.accounts.models.dtos.requests.UpdatePasswordReqDto;

@Controller
@RequestMapping("/accounts")
@RequiredArgsConstructor
public class AccountsViewController {
    private static final String REQUEST_URI_REQUEST_ATTRIBUTE = "requestUri";

    private final AccountsService accountsService;

    @GetMapping("/delete-account")
    public String deleteAccount(
        final DeleteAccountReqDto deleteAccountReqDto,
        final Model model,
        final HttpServletRequest request
    ) {
        model.addAttribute(
            AccountsViewController.REQUEST_URI_REQUEST_ATTRIBUTE,
            request.getRequestURI()
        );

        return "delete-account";
    }

    @PostMapping(
        consumes = "application/x-www-form-urlencoded",
        path = "/delete-account"
    )
    public String deleteAccount(
        @Valid
        final DeleteAccountReqDto deleteAccountReqDto,
        final BindingResult bindingResult,
        final Model model,
        final HttpServletRequest request
    ) {
        model.addAttribute(
            AccountsViewController.REQUEST_URI_REQUEST_ATTRIBUTE,
            request.getRequestURI()
        );

        return this.accountsService.deleteAccount(
            deleteAccountReqDto,
            bindingResult,
            model,
            request
        );
    }

    @GetMapping("/email-preferences")
    public String emailPreferences(
        final UpdateEmailReqDto updateEmailReqDto,
        final Model model,
        final HttpServletRequest request
    ) {
        model.addAttribute(
            AccountsViewController.REQUEST_URI_REQUEST_ATTRIBUTE,
            request.getRequestURI()
        );

        return "email-preferences";
    }

    @PostMapping(
        consumes = "application/x-www-form-urlencoded",
        path = "/email-preferences"
    )
    public String updateEmail(
        @Valid
        final UpdateEmailReqDto updateEmailReqDto,
        final BindingResult bindingResult,
        final Model model,
        final HttpServletRequest request
    ) {
        model.addAttribute(
            AccountsViewController.REQUEST_URI_REQUEST_ATTRIBUTE,
            request.getRequestURI()
        );

        return this.accountsService.updateEmail(
            updateEmailReqDto,
            bindingResult,
            model,
            request
        );
    }

    @GetMapping("/change-password")
    public String updatePassword(
        final UpdatePasswordReqDto updatePasswordReqDto,
        final Model model,
        final HttpServletRequest request
    ) {
        model.addAttribute(
            AccountsViewController.REQUEST_URI_REQUEST_ATTRIBUTE,
            request.getRequestURI()
        );

        return "change-password";
    }

    @PostMapping(
        consumes = "application/x-www-form-urlencoded",
        path = "/change-password"
    )
    public String updatePassword(
        @Valid
        final UpdatePasswordReqDto updatePasswordReqDto,
        final BindingResult bindingResult,
        final Model model,
        final HttpServletRequest request
    ) {
        model.addAttribute(
            AccountsViewController.REQUEST_URI_REQUEST_ATTRIBUTE,
            request.getRequestURI()
        );

        return this.accountsService.updatePassword(
            updatePasswordReqDto,
            bindingResult,
            model,
            request
        );
    }
}
