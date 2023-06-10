package com.juansecu.openfusion.openfusionopenapiplugin.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.servlet.config.annotation.*;

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
    public void addCorsMappings(final CorsRegistry registry) {
        registry
            .addMapping("/**")
            .allowedHeaders("*")
            .allowedMethods(
                "DELETE",
                "GET",
                "OPTIONS",
                "POST",
                "PUT"
            )
            .allowedOrigins("*");
    }

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

    @Override
    public void addResourceHandlers(final ResourceHandlerRegistry registry) {
        registry
            .addResourceHandler("/static/**")
            .addResourceLocations("classpath:/static/")
            .resourceChain(false);
    }
}
