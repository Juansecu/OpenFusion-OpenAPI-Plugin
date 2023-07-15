package com.juansecu.openfusion.openfusionopenapiplugin.accounts.events;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import com.juansecu.openfusion.openfusionopenapiplugin.accounts.models.entities.AccountEntity;

@Getter
public class AccountDeleteRequestProcessedEvent extends ApplicationEvent {
    private final AccountEntity account;

    public AccountDeleteRequestProcessedEvent(final AccountEntity account) {
        super(account);
        this.account = account;
    }
}
