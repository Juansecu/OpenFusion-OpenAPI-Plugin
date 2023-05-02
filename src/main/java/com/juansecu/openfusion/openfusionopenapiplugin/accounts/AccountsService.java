package com.juansecu.openfusion.openfusionopenapiplugin.accounts;

import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.juansecu.openfusion.openfusionopenapiplugin.accounts.repositories.IAccountsRepository;

@RequiredArgsConstructor
@Service
public class AccountsService implements UserDetailsService {
    private static final Logger CONSOLE_LOGGER = LogManager.getLogger(AccountsService.class);

    private final IAccountsRepository accountsRepository;

    @Override
    public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
        AccountsService.CONSOLE_LOGGER.info(
            String.format("Finding user %s%s", username, "...")
        );

        return this.accountsRepository
            .findByUsernameIgnoreCase(username)
            .orElseThrow(
                () -> new UsernameNotFoundException("Account not found")
            );
    }
}
