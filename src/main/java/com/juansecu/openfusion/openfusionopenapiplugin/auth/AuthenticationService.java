package com.juansecu.openfusion.openfusionopenapiplugin.auth;

import java.util.Objects;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
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
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import com.juansecu.openfusion.openfusionopenapiplugin.accounts.AccountsService;
import com.juansecu.openfusion.openfusionopenapiplugin.accounts.enums.EAccountServiceError;
import com.juansecu.openfusion.openfusionopenapiplugin.accounts.models.entities.AccountEntity;
import com.juansecu.openfusion.openfusionopenapiplugin.accounts.repositories.IAccountsRepository;
import com.juansecu.openfusion.openfusionopenapiplugin.auth.models.dtos.requests.LoginReqDto;
import com.juansecu.openfusion.openfusionopenapiplugin.auth.models.dtos.requests.RegisterReqDto;
import com.juansecu.openfusion.openfusionopenapiplugin.auth.models.dtos.responses.AuthenticationDataResDto;
import com.juansecu.openfusion.openfusionopenapiplugin.shared.adapters.JwtAdapter;
import com.juansecu.openfusion.openfusionopenapiplugin.shared.models.dtos.responses.BasicResDto;
import com.juansecu.openfusion.openfusionopenapiplugin.shared.providers.HostDetailsProvider;
import com.juansecu.openfusion.openfusionopenapiplugin.shared.services.EmailService;
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
    @Value("${mail.new-account-email-verification-message.message}")
    private String newAccountEmailVerificationMessage;
    @Value("${mail.new-account-email-verification-message.subject}")
    private String newAccountEmailVerificationSubject;

    private final IAccountsRepository accountsRepository;
    private final AccountsService accountsService;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;
    private final HostDetailsProvider hostDetailsProvider;
    private final JwtAdapter jwtAdapter;
    private final PasswordEncoder passwordEncoder;
    private final UserDetailsService userDetailsService;
    private final VerificationTokensService verificationTokensService;

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
        String verificationEmailMessage;
        VerificationTokenEntity verificationToken;

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

        AuthenticationService.CONSOLE_LOGGER.info(
            "User registered successfully: {}. Generating verification token...",
            registerReqDto.getUsername()
        );

        verificationToken = this.verificationTokensService.generateEmailVerificationToken(
            newAccount
        );
        verificationEmailMessage = this.replaceEmailVerificationParameters(
            this.verificationTokensService.getEncryptedToken(
                verificationToken.getToken().toString()
            ),
            newAccount.getUsername()
        );

        if (
            !this.emailService.sendSimpleMessage(
                verificationEmailMessage,
                this.newAccountEmailVerificationSubject,
                newAccount.getEmail()
            )
        ) {
            AuthenticationService.CONSOLE_LOGGER.info(
                "Verification email could not be sent"
            );

            return new ResponseEntity<>(
                new BasicResDto(
                    false,
                    EAccountServiceError.COULD_NOT_SEND_VERIFICATION_EMAIL,
                    "Verification email could not be sent",
                    null
                ),
                HttpStatus.INTERNAL_SERVER_ERROR
            );
        }

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

    private String replaceEmailVerificationParameters(
        final String encryptedToken,
        final String username
    ) {
        return this.newAccountEmailVerificationMessage
            .replace(
                "{hours_to_expire}",
                String.valueOf(
                    VerificationTokensService.HOURS_OF_EXPIRING_EMAIL_VERIFICATION_TOKEN
                )
            )
            .replace("{username}", username)
            .replace(
                "{verify_account_link}",
                this.hostDetailsProvider.getHostPath() +
                    "/api/verification-tokens/verify?token=" +
                    encryptedToken +
                    "&type=" +
                    EVerificationTokenType.EMAIL_VERIFICATION_TOKEN +
                    "&username=" +
                    username
            );
    }
}
