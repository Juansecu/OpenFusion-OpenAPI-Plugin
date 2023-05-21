package com.juansecu.openfusion.openfusionopenapiplugin.auth;

import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.juansecu.openfusion.openfusionopenapiplugin.accounts.AccountsService;
import com.juansecu.openfusion.openfusionopenapiplugin.accounts.enums.EAccountServiceError;
import com.juansecu.openfusion.openfusionopenapiplugin.accounts.models.entities.AccountEntity;
import com.juansecu.openfusion.openfusionopenapiplugin.auth.models.dtos.requests.LoginReqDto;
import com.juansecu.openfusion.openfusionopenapiplugin.auth.models.dtos.requests.RegisterReqDto;
import com.juansecu.openfusion.openfusionopenapiplugin.auth.models.dtos.responses.AuthenticationDataResDto;
import com.juansecu.openfusion.openfusionopenapiplugin.shared.adapters.JwtAdapter;
import com.juansecu.openfusion.openfusionopenapiplugin.shared.models.dtos.responses.BasicResDto;

@RequiredArgsConstructor
@Service
public class AuthenticationService {
    private static final Logger CONSOLE_LOGGER = LogManager.getLogger(AuthenticationService.class);

    @Value("${ISSUER:OpenFusion}")
    private String issuer;

    private final AccountsService accountsService;
    private final AuthenticationManager authenticationManager;
    private final JwtAdapter jwtProvider;
    private final PasswordEncoder passwordEncoder;
    private final UserDetailsService userDetailsService;

    public BasicResDto authenticate(final LoginReqDto loginReqDto) {
        UserDetails account;

        this.authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                loginReqDto.getUsername(),
                loginReqDto.getPassword()
            )
        );

        account = this.userDetailsService.loadUserByUsername(loginReqDto.getUsername());

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

    public ResponseEntity<BasicResDto> register(final RegisterReqDto registerReqDto) {
        final AccountEntity accountWithGivenEmail = this.accountsService.getAccountByEmail(
            registerReqDto.getEmail()
        );
        final AccountEntity accountWithGivenUsername = this.accountsService.getAccountByUsername(
            registerReqDto.getUsername()
        );
        final AccountEntity newAccount = new AccountEntity(
            registerReqDto.getUsername(),
            registerReqDto.getEmail(),
            registerReqDto.getPassword()
        );

        if (accountWithGivenEmail != null) {
            AuthenticationService.CONSOLE_LOGGER.error(
                "Email is already in use"
            );

            return new ResponseEntity<>(
                new BasicResDto(
                    false,
                    EAccountServiceError.EMAIL_IN_USE,
                    "Email is already in use",
                    null
                ),
                HttpStatus.CONFLICT
            );
        }

        if (accountWithGivenUsername != null) {
            AuthenticationService.CONSOLE_LOGGER.error(
                "Username is already in use"
            );

            return new ResponseEntity<>(
                new BasicResDto(
                    false,
                    EAccountServiceError.USERNAME_IN_USE,
                    "Username is already in use",
                    null
                ),
                HttpStatus.CONFLICT
            );
        }

        newAccount.setPassword(this.passwordEncoder.encode(newAccount.getPassword()));

        AuthenticationService.CONSOLE_LOGGER.info(
            "Registering user: {}...",
            registerReqDto.getUsername()
        );

        this.accountsService.saveAccount(newAccount);

        AuthenticationService.CONSOLE_LOGGER.info(
            "User registered successfully: {}",
            registerReqDto.getUsername()
        );

        return new ResponseEntity<>(
            new BasicResDto(
                true,
                null,
                "User registered successfully",
                null
            ),
            HttpStatus.CREATED
        );
    }
}
