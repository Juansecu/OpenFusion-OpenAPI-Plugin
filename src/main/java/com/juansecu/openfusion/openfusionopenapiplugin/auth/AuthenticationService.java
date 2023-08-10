package com.juansecu.openfusion.openfusionopenapiplugin.auth;

import java.util.Objects;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
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
import com.juansecu.openfusion.openfusionopenapiplugin.accounts.events.PasswordChangeEvent;
import com.juansecu.openfusion.openfusionopenapiplugin.accounts.models.entities.AccountEntity;
import com.juansecu.openfusion.openfusionopenapiplugin.accounts.repositories.IAccountsRepository;
import com.juansecu.openfusion.openfusionopenapiplugin.auth.events.ForgotPasswordRequestProcessedEvent;
import com.juansecu.openfusion.openfusionopenapiplugin.auth.models.dtos.requests.ForgotPasswordReqDto;
import com.juansecu.openfusion.openfusionopenapiplugin.auth.models.dtos.requests.LoginReqDto;
import com.juansecu.openfusion.openfusionopenapiplugin.auth.models.dtos.requests.RegisterReqDto;
import com.juansecu.openfusion.openfusionopenapiplugin.auth.models.dtos.requests.ResetPasswordReqDto;
import com.juansecu.openfusion.openfusionopenapiplugin.auth.models.dtos.responses.AuthenticationDataResDto;
import com.juansecu.openfusion.openfusionopenapiplugin.shared.adapters.JwtAdapter;
import com.juansecu.openfusion.openfusionopenapiplugin.shared.models.dtos.responses.BasicResDto;
import com.juansecu.openfusion.openfusionopenapiplugin.verificationtokens.VerificationTokensService;
import com.juansecu.openfusion.openfusionopenapiplugin.verificationtokens.enums.EVerificationTokenType;
import com.juansecu.openfusion.openfusionopenapiplugin.verificationtokens.models.entities.VerificationTokenEntity;

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
    private final VerificationTokensService verificationTokensService;

    public BasicResDto authenticate(final LoginReqDto loginReqDto) {
        final UserDetails account = this.userDetailsService.loadUserByUsername(
            loginReqDto.getUsername()
        );

        this.authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                loginReqDto.getUsername(),
                loginReqDto.getPassword()
            )
        );

        return new BasicResDto(
            true,
            null,
            "Account authenticated successfully",
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
                    "User {} does not have a associated e-mail address",
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

    public String forgotPassword(
        final ForgotPasswordReqDto forgotPasswordReqDto,
        final BindingResult bindingResult,
        final Model model
    ) {
        if (bindingResult.hasErrors())
            return "forgot-password";

        final ResponseEntity<BasicResDto> forgotPasswordResponse = this.forgotPassword(
            forgotPasswordReqDto
        );
        final BasicResDto forgotPasswordResponseBody = forgotPasswordResponse.getBody();

        if (!Objects.requireNonNull(forgotPasswordResponseBody).success()) {
            if (forgotPasswordResponseBody.error() == EAccountServiceError.NOT_LINKED_EMAIL) {
                model.addAttribute(
                    "error",
                    "Service unavailable due you don't have an associated e-mail address"
                );

                return "forgot-password";
            }

            model.addAttribute("error", forgotPasswordResponseBody.message());

            return "forgot-password";
        }

        return "redirect:/auth/login?success=We+have+sent+you+an+e-mail+to+reset+your+password.+Check+your+inbox";
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
        } else if (accountWithGivenUsername != null) {
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

    public String resetPassword(final HttpServletRequest request) {
        final VerificationTokenEntity verificationToken = (VerificationTokenEntity) request.getAttribute(
            VerificationTokensService.VERIFICATION_TOKEN_ATTRIBUTE_KEY
        );

        if (verificationToken.getUsesCount() != 1)
            return "redirect:/auth/login?error=Invalid+token";

        return "reset-password";
    }

    public String resetPassword(
        final ResetPasswordReqDto resetPasswordReqDto,
        final EVerificationTokenType verificationTokenType,
        final String username,
        final BindingResult bindingResult,
        final Model model,
        final HttpServletRequest request
    ) {
        if (verificationTokenType != EVerificationTokenType.RESET_PASSWORD_TOKEN)
            return "redirect:/auth/login?error=Invalid+token+type";

        if (bindingResult.hasErrors())
            return "reset-password";

        AccountEntity account;
        boolean isInvalidToken;

        final VerificationTokenEntity verificationToken = (VerificationTokenEntity) request.getAttribute(
            VerificationTokensService.VERIFICATION_TOKEN_ATTRIBUTE_KEY
        );

        if (verificationToken.getUsesCount() != 1) {
            model.addAttribute(
                "error",
                "Invalid token"
            );

            return "forgot-password";
        }

        AuthenticationService.CONSOLE_LOGGER.info(
            "Verifying {} token for user {}...",
            verificationTokenType,
            username
        );

        this.verificationTokensService.verifyToken(
            verificationTokenType,
            username,
            request
        );

        isInvalidToken = (Boolean) request.getAttribute(
            VerificationTokensService.IS_INVALID_TOKEN_ATTRIBUTE_KEY
        );

        if (isInvalidToken) {
            model.addAttribute(
                "error",
                "Invalid token"
            );

            return "forgot-password";
        }

        account = (AccountEntity) request.getAttribute("account");

        AuthenticationService.CONSOLE_LOGGER.info(
            "Resetting password for user {}...",
            account.getUsername()
        );

        account.setPassword(
            this.passwordEncoder.encode(
                resetPasswordReqDto.getNewPassword()
            )
        );

        this.accountsRepository.save(account);

        AuthenticationService.CONSOLE_LOGGER.info(
            "Password reset for user {} successful",
            account.getUsername()
        );

        this.applicationEventPublisher.publishEvent(
            new PasswordChangeEvent(account)
        );

        return "redirect:/auth/login?success=You+have+reset+your+password+successfully%21";
    }
}
