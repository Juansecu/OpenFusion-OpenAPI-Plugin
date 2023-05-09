package com.juansecu.openfusion.openfusionopenapiplugin.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.juansecu.openfusion.openfusionopenapiplugin.accounts.AccountsService;
import com.juansecu.openfusion.openfusionopenapiplugin.shared.utils.CryptoUtil;
import com.juansecu.openfusion.openfusionopenapiplugin.verificationtokens.interceptors.VerificationTokenInterceptor;

@Configuration
@EnableWebMvc
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {
    private final AccountsService accountsService;
    private final CryptoUtil cryptoUtil;
    private final UserDetailsService userDetailsService;

    @Override
    public void addInterceptors(final InterceptorRegistry registry) {
        registry
            .addInterceptor(
                new VerificationTokenInterceptor(
                    this.accountsService,
                    this.cryptoUtil,
                    this.userDetailsService
                )
            )
            .addPathPatterns(
                "/verification-tokens/verify"
            );
    }
}