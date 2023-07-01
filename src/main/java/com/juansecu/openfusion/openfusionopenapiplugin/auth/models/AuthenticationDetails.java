package com.juansecu.openfusion.openfusionopenapiplugin.auth.models;

import com.juansecu.openfusion.openfusionopenapiplugin.accounts.models.entities.AccountEntity;

public record AuthenticationDetails (
    AccountEntity account,
    String token
) {}
