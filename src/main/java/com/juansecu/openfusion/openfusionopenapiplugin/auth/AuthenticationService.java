package com.juansecu.openfusion.openfusionopenapiplugin.auth;

import java.util.Objects;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import com.juansecu.openfusion.openfusionopenapiplugin.accounts.AccountsService;
import com.juansecu.openfusion.openfusionopenapiplugin.accounts.enums.EAccountServiceError;
import com.juansecu.openfusion.openfusionopenapiplugin.accounts.events.AccountRegisterEvent;
import com.juansecu.openfusion.openfusionopenapiplugin.accounts.models.entities.AccountEntity;
import com.juansecu.openfusion.openfusionopenapiplugin.accounts.repositories.IAccountsRepository;
import com.juansecu.openfusion.openfusionopenapiplugin.auth.events.ForgotPasswordRequestProcessedEvent;
import com.juansecu.openfusion.openfusionopenapiplugin.auth.models.dtos.requests.ForgotPasswordReqDto;
import com.juansecu.openfusion.openfusionopenapiplugin.auth.models.dtos.requests.LoginReqDto;
import com.juansecu.openfusion.openfusionopenapiplugin.auth.models.dtos.requests.RegisterReqDto;
import com.juansecu.openfusion.openfusionopenapiplugin.auth.models.dtos.responses.AuthenticationDataResDto;
import com.juansecu.openfusion.openfusionopenapiplugin.shared.adapters.JwtAdapter;
import com.juansecu.openfusion.openfusionopenapiplugin.shared.models.dtos.responses.BasicResDto;

@RequiredArgsConstructor
@Service
public class AuthenticationService {
    public static final String AUTHENTICATION_COOKIE_NAME = "token";

    private static final Logger CONSOLE_LOGGER = LogManager.getLogger(AuthenticationService.class);

    @Value("${issuer.name}")
    private String issuer;

    private final IAccountsRepository accountsRepository;
    private final AccountsService accountsService;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final AuthenticationManager authenticationManager;
    private final JwtAdapter jwtAdapter;
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
                this.jwtAdapter.generateJsonWebToken(
                    account.getUsername(),
                    this.issuer
                )
            )
        );
    }

    public String authenticate(
        final LoginReqDto loginReqDto,
        final BindingResult bindingResult,
        final Model model,
        final HttpServletResponse response
    ) {
        if (bindingResult.hasErrors())
            return "login";

        Cookie authenticationCookie;
        String token;

        final BasicResDto loginResponse = this.authenticate(
            loginReqDto
        );

        if (!loginResponse.success()) {
            model.addAttribute("error", loginResponse.message());
            return "login";
        }

        token = ((AuthenticationDataResDto) loginResponse.data()).token();

        authenticationCookie = new Cookie(
            AuthenticationService.AUTHENTICATION_COOKIE_NAME,
            token
        );

        authenticationCookie.setHttpOnly(true);
        authenticationCookie.setMaxAge(-(JwtAdapter.MINUTES_OF_VALID_TOKEN * 60));
        authenticationCookie.setPath("/");
        authenticationCookie.setSecure(true);

        response.addCookie(authenticationCookie);

        return "redirect:/accounts/email-preferences";
    }

    public ResponseEntity<BasicResDto> forgotPassword(
        final ForgotPasswordReqDto forgotPasswordReqDto
    ) {
        AuthenticationService.CONSOLE_LOGGER.info("Initializing forgot password service...");

        AccountEntity account;

        if (
            forgotPasswordReqDto.getEmail() == null &&
            forgotPasswordReqDto.getUsername() == null
        ) {
            AuthenticationService.CONSOLE_LOGGER.error("User email and username are empty");

            return new ResponseEntity<>(
                new BasicResDto(
                    false,
                    EAccountServiceError.EMPTY_EMAIL_AND_USERNAME,
                    "User email and username are empty",
                    null
                ),
                HttpStatus.BAD_REQUEST
            );
        }

        if (forgotPasswordReqDto.getEmail() != null) {
            AuthenticationService.CONSOLE_LOGGER.info("Searching by user email...");

            account = this.accountsRepository
                .findByEmailIgnoreCase(forgotPasswordReqDto.getEmail())
                .orElse(null);

            if (account == null) {
                AuthenticationService.CONSOLE_LOGGER.error(
                    "User not found"
                );

                return new ResponseEntity<>(
                    new BasicResDto(
                        false,
                        EAccountServiceError.USER_NOT_FOUND,
                        "User not found",
                        null
                    ),
                    HttpStatus.NOT_FOUND
                );
            }

            if (!account.isVerified()) {
                AuthenticationService.CONSOLE_LOGGER.error(
                    "{}'s account is not verified",
                    account.getUsername()
                );

                return new ResponseEntity<>(
                    new BasicResDto(
                        false,
                        EAccountServiceError.ACCOUNT_NOT_VERIFIED,
                        "Account is not verified",
                        null
                    ),
                    HttpStatus.UNPROCESSABLE_ENTITY
                );
            }
        } else {
            AuthenticationService.CONSOLE_LOGGER.info("Searching by username...");

            account = this.accountsRepository
                .findByUsernameIgnoreCase(forgotPasswordReqDto.getUsername())
                .orElse(null);

            if (account == null) {
                AuthenticationService.CONSOLE_LOGGER.error(
                    "User {} does not exist",
                    forgotPasswordReqDto.getUsername()
                );

                return new ResponseEntity<>(
                    new BasicResDto(
                        false,
                        EAccountServiceError.USER_NOT_FOUND,
                        "User not found",
                        null
                    ),
                    HttpStatus.NOT_FOUND
                );
            }

            if (account.getEmail().isBlank()) {
                AuthenticationService.CONSOLE_LOGGER.error(
                    "User {} does not have a associated e-mail address. Redirecting user to ask them to associate one...",
                    forgotPasswordReqDto.getUsername()
                );

                return new ResponseEntity<>(
                    new BasicResDto(
                        false,
                        EAccountServiceError.NOT_LINKED_EMAIL,
                        "User does not have an associated e-mail address",
                        null
                    ),
                    HttpStatus.UNPROCESSABLE_ENTITY
                );
            }

            if (!account.isVerified()) {
                AuthenticationService.CONSOLE_LOGGER.error(
                    "{}'s account is not verified",
                    account.getUsername()
                );

                return new ResponseEntity<>(
                    new BasicResDto(
                        false,
                        EAccountServiceError.ACCOUNT_NOT_VERIFIED,
                        "Account is not verified",
                        null
                    ),
                    HttpStatus.UNPROCESSABLE_ENTITY
                );
            }
        }

        AuthenticationService.CONSOLE_LOGGER.info(
            "Reset password request for {}'s account processed successfully",
            account.getUsername()
        );

        this.applicationEventPublisher.publishEvent(
            new ForgotPasswordRequestProcessedEvent(account)
        );

        return new ResponseEntity<>(
            new BasicResDto(
                true,
                null,
                "Reset password request processed successfully. Verification e-mail sent",
                null
            ),
            HttpStatus.OK
        );
    }

    public String logout(final HttpServletResponse response) {
        Cookie authenticationCookie;

        authenticationCookie = new Cookie(
            AuthenticationService.AUTHENTICATION_COOKIE_NAME,
            null
        );

        authenticationCookie.setHttpOnly(true);
        authenticationCookie.setMaxAge(0);
        authenticationCookie.setPath("/");
        authenticationCookie.setSecure(true);

        response.addCookie(authenticationCookie);

        return "redirect:/auth/login?logout=true";
    }

    public ResponseEntity<BasicResDto> register(final RegisterReqDto registerReqDto) {
        AccountEntity newAccount;

        final AccountEntity accountWithGivenEmail = this.accountsService.getAccountByEmail(
            registerReqDto.getEmail()
        );
        final AccountEntity accountWithGivenUsername = this.accountsService.getAccountByUsername(
            registerReqDto.getUsername()
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

        newAccount = new AccountEntity(
            registerReqDto.getUsername(),
            registerReqDto.getEmail(),
            registerReqDto.getPassword()
        );

        newAccount.setPassword(this.passwordEncoder.encode(newAccount.getPassword()));

        AuthenticationService.CONSOLE_LOGGER.info(
            "Registering user: {}...",
            registerReqDto.getUsername()
        );

        newAccount = this.accountsRepository.save(newAccount);

        this.applicationEventPublisher.publishEvent(
            new AccountRegisterEvent(newAccount)
        );

        AuthenticationService.CONSOLE_LOGGER.info(
            "User registered successfully: {}. Generating verification token...",
            registerReqDto.getUsername()
        );

        return new ResponseEntity<>(
            new BasicResDto(
                true,
                null,
                "User registered successfully. Verification email sent",
                null
            ),
            HttpStatus.CREATED
        );
    }

    public String register(
        final RegisterReqDto registerReqDto,
        final BindingResult bindingResult,
        final Model model
    ) {
        if (bindingResult.hasErrors())
            return "register";

        final ResponseEntity<BasicResDto> registerResponse = this.register(
            registerReqDto
        );
        final BasicResDto registerResponseBody = registerResponse.getBody();

        if (!Objects.requireNonNull(registerResponseBody).success()) {
            model.addAttribute("error", registerResponseBody.message());
            return "register";
        }

        return "redirect:/auth/login?success=You+have+successfully+signed+up%21+Check+your+e-mail+to+verify+your+account";
    }
}
