package com.juansecu.openfusion.openfusionopenapiplugin.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.juansecu.openfusion.openfusionopenapiplugin.accounts.repositories.IAccountsRepository;

@Configuration
@RequiredArgsConstructor
public class BeansProvider {
    private final IAccountsRepository accountsRepository;

    @Bean
    protected UserDetailsService userDetailsService() {
        return (final String username) -> this.accountsRepository
            .findByUsernameIgnoreCase(username)
            .orElseThrow(
                () -> new UsernameNotFoundException("Account not found")
            );
    }
}
