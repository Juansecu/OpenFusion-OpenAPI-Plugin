package com.juansecu.openfusion.openfusionopenapiplugin.shared.adapters;

import java.util.Date;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

@Component
public class JwtAdapter {
    public static final int MINUTES_OF_VALID_TOKEN = 30;

    private static final Logger CONSOLE_LOGGER = LogManager.getLogger(JwtAdapter.class);
    private static final Algorithm JWT_ALGORITHM = Algorithm.HMAC256("${JWT_SECRET}");

    public String generateJsonWebToken(final String subject, final String issuer) {
        JwtAdapter.CONSOLE_LOGGER.info("Generating JSON Web Token...");

        return JWT.create()
            .withSubject(subject)
            .withIssuer(issuer)
            .withExpiresAt(
                new Date(
                    new Date().getTime() + JwtAdapter.MINUTES_OF_VALID_TOKEN * 60 * 1000
                )
            )
            .sign(JwtAdapter.JWT_ALGORITHM);
    }

    public String getSubject(final String token) {
        return JWT.decode(token).getSubject();
    }

    public boolean isValidJsonWebToken(final String token) {
        try {
            JwtAdapter.CONSOLE_LOGGER.info("Validating JSON Web Token...");

            final DecodedJWT decodedJwt = JWT.decode(token);
            final Date today = new Date();

            if (decodedJwt.getExpiresAt().before(today)) {
                JwtAdapter.CONSOLE_LOGGER.error("JSON Web Token is already expired");
                return false;
            }

            JWT.require(JwtAdapter.JWT_ALGORITHM)
                .build()
                .verify(decodedJwt);

            JwtAdapter.CONSOLE_LOGGER.info("JSON Web Token validated successfully");

            return true;
        } catch (JWTVerificationException jwtVerificationException) {
            JwtAdapter.CONSOLE_LOGGER.error(
                String.format(
                    "Error while trying to validate JSON Web Token:%n%s",
                    jwtVerificationException
                )
            );

            return false;
        }
    }
}
