package com.juansecu.openfusion.openfusionopenapiplugin.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.juansecu.openfusion.openfusionopenapiplugin.accounts.AccountsService;
import com.juansecu.openfusion.openfusionopenapiplugin.auth.models.dtos.requests.LoginReqDto;
import com.juansecu.openfusion.openfusionopenapiplugin.auth.models.dtos.responses.AuthenticationDataResDto;
import com.juansecu.openfusion.openfusionopenapiplugin.shared.adapters.JwtAdapter;
import com.juansecu.openfusion.openfusionopenapiplugin.shared.models.dtos.responses.BasicResDto;

@RequiredArgsConstructor
@Service
public class AuthenticationService {
    @Value("${ISSUER:OpenFusion}")
    private String issuer;

    private final AccountsService accountsService;
    private final AuthenticationManager authenticationManager;
    private final JwtAdapter jwtProvider;

    public BasicResDto authenticate(final LoginReqDto loginReqDto) {
        UserDetails account;

        this.authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                loginReqDto.getUsername(),
                loginReqDto.getPassword()
            )
        );

        account = this.accountsService.loadUserByUsername(loginReqDto.getUsername());

        return new BasicResDto(
            true,
            null,
            "User authenticated successfully",
            new AuthenticationDataResDto(
                this.jwtProvider.generateJsonWebToken(
                    account.getUsername(),
                    this.issuer
                )
            )
        );
    }
}
