package com.juansecu.openfusion.openfusionopenapiplugin.auth.events;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import com.juansecu.openfusion.openfusionopenapiplugin.accounts.models.entities.AccountEntity;

@Getter
public class ForgotPasswordRequestProcessedEvent extends ApplicationEvent {
    private final AccountEntity account;

    public ForgotPasswordRequestProcessedEvent(final AccountEntity account) {
        super(account);
        this.account = account;
    }
}
