package com.juansecu.openfusion.openfusionopenapiplugin.shared.utils;

import java.io.IOException;

import jakarta.servlet.http.HttpServletResponse;

public final class RedirectUtil {
    public static void redirect(
        final boolean isAuthenticated,
        final boolean isSuccessful,
        final String message,
        final HttpServletResponse response
    ) throws IOException {
        final String redirectUrl = isAuthenticated
            ? "/accounts/email-preferences"
            : "/auth/login";

        response.sendRedirect(
            redirectUrl +
                "?" +
                (isSuccessful ? "success" : "error") +
                "=" + message
        );
    }
}
