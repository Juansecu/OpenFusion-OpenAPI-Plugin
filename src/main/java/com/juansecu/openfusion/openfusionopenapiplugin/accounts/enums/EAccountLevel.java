package com.juansecu.openfusion.openfusionopenapiplugin.accounts.enums;

public enum EAccountLevel {
    CLOSED_BETA_USER(80),
    CUSTOMER_SERVICE(40),
    DEVELOPER(50),
    FREE_USER(48),
    GAME_MASTER(30),
    MASTER(1),
    OPEN_BETA_USER(85),
    PAY_USER(49),
    POWER_DEVELOPER(10),
    QA(20),
    USER(99);

    public final int accountLevel;

    EAccountLevel(final int accountLevel) {
        this.accountLevel = accountLevel;
    }
}
