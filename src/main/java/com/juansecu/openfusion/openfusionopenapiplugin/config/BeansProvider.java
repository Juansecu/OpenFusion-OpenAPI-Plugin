package com.juansecu.openfusion.openfusionopenapiplugin.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.encrypt.TextEncryptor;

import com.juansecu.openfusion.openfusionopenapiplugin.accounts.repositories.IAccountsRepository;

@Configuration
@RequiredArgsConstructor
public class BeansProvider {
    @Value("${VERIFICATION_TOKEN_SALT_KEY}")
    private String verificationTokenSalt;
    @Value("${VERIFICATION_TOKEN_SECURITY_KEY}")
    private String verificationTokenSecurityKey;

    private final IAccountsRepository accountsRepository;

    @Bean
    protected ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Bean
    protected TextEncryptor textEncryptor() {
        return Encryptors.text(
            this.verificationTokenSecurityKey,
            this.verificationTokenSalt
        );
    }

    @Bean
    protected UserDetailsService userDetailsService() {
        return (final String username) -> this.accountsRepository
            .findByUsernameIgnoreCase(username)
            .orElseThrow(
                () -> new UsernameNotFoundException("Account not found")
            );
    }
}
