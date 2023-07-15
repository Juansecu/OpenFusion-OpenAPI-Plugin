package com.juansecu.openfusion.openfusionopenapiplugin.accounts;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.juansecu.openfusion.openfusionopenapiplugin.accounts.models.dtos.requests.DeleteAccountReqDto;
import com.juansecu.openfusion.openfusionopenapiplugin.accounts.models.dtos.requests.UpdateEmailReqDto;
import com.juansecu.openfusion.openfusionopenapiplugin.accounts.models.dtos.requests.UpdatePasswordReqDto;
import com.juansecu.openfusion.openfusionopenapiplugin.shared.models.dtos.responses.BasicResDto;

@RequestMapping("/api/accounts")
@RequiredArgsConstructor
@RestController
public class AccountsApiController {
    private final AccountsService accountsService;

    @DeleteMapping
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

    @PutMapping("/update-email")
    public ResponseEntity<BasicResDto> updateEmail(
        @RequestBody
        @Valid
        final UpdateEmailReqDto updateEmailReqDto,
        final HttpServletRequest request
    ) {
        return this.accountsService.updateEmail(updateEmailReqDto, request);
    }

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
