package com.juansecu.openfusion.openfusionopenapiplugin.config;

import com.juansecu.openfusion.openfusionopenapiplugin.accounts.enums.EAccountLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.juansecu.openfusion.openfusionopenapiplugin.auth.filters.JwtAuthenticationFilter;
import com.juansecu.openfusion.openfusionopenapiplugin.auth.filters.ProtectedViewAgainstAuthenticatedUserFilter;
import com.juansecu.openfusion.openfusionopenapiplugin.auth.filters.ProtectedViewAgainstAuthenticationFilter;
import com.juansecu.openfusion.openfusionopenapiplugin.verificationtokens.filters.VerificationTokenFinderFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    @Autowired
    @Qualifier("delegatedAuthenticationEntryPoint")
    private AuthenticationEntryPoint authenticationEntryPoint;

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final PasswordEncoder passwordEncoder;
    private final ProtectedViewAgainstAuthenticatedUserFilter protectedViewAgainstAuthenticatedUserFilter;
    private final ProtectedViewAgainstAuthenticationFilter protectedViewAgainstAuthenticationFilter;
    private final UserDetailsService userDetailsService;
    private final VerificationTokenFinderFilter verificationTokenFinderFilter;

    @Bean
    protected AuthenticationManager authenticationManager(
        final AuthenticationConfiguration authenticationConfiguration
    ) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    protected AuthenticationProvider authenticationProvider() {
        final DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();

        daoAuthenticationProvider.setUserDetailsService(this.userDetailsService);
        daoAuthenticationProvider.setPasswordEncoder(this.passwordEncoder);

        return daoAuthenticationProvider;
    }

    @Bean
    protected SecurityFilterChain securityFilterChain(
        final HttpSecurity httpSecurity
    ) throws Exception {
        httpSecurity
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(authorizationManagerRequestMatcherRegistry ->
                authorizationManagerRequestMatcherRegistry
                    .requestMatchers(
                        "/api/auth/**",
                        "/auth/forgot-password",
                        "/auth/login",
                        "/auth/register",
                        "/auth/reset-password",
                        "/favicon.ico",
                        "/static/**",
                        "/api/verification-tokens/**"
                    ).permitAll()
                    .requestMatchers(
                        "/api/docs/**",
                        "/docs",
                        "/swagger-ui/**"
                    ).hasAnyAuthority(
                        EAccountLevel.DEVELOPER.toString(),
                        EAccountLevel.GAME_MASTER.toString(),
                        EAccountLevel.MASTER.toString()
                    )
                    .anyRequest().authenticated()
            )
            .sessionManagement(sessions ->
                sessions.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .authenticationProvider(this.authenticationProvider())
            .addFilterBefore(
                this.jwtAuthenticationFilter,
                UsernamePasswordAuthenticationFilter.class
            )
            .addFilterAfter(
                this.protectedViewAgainstAuthenticationFilter,
                JwtAuthenticationFilter.class
            )
            .addFilterAfter(
                this.protectedViewAgainstAuthenticatedUserFilter,
                ProtectedViewAgainstAuthenticationFilter.class
            )
            .addFilterAfter(
                this.verificationTokenFinderFilter,
                ProtectedViewAgainstAuthenticatedUserFilter.class
            )
            .exceptionHandling(exception ->
                exception.authenticationEntryPoint(this.authenticationEntryPoint)
            );

        return httpSecurity.build();
    }
}
