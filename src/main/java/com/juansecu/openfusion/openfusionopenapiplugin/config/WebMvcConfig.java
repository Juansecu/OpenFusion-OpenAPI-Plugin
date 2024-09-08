package com.juansecu.openfusion.openfusionopenapiplugin.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

import com.juansecu.openfusion.openfusionopenapiplugin.verificationtokens.interceptors.VerificationTokenInterceptor;

@Configuration
@EnableWebMvc
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {
    @Value("${server.cors.allowed-origins}")
    private String allowedOrigins;

    private final VerificationTokenInterceptor verificationTokenInterceptor;

    @Override
    public void addCorsMappings(final CorsRegistry registry) {

        registry
            .addMapping("/**")
            .allowedHeaders("*")
            .allowedMethods(
                "DELETE",
                "GET",
                "OPTIONS",
                "POST",
                "PUT"
            )
            .allowedOrigins(this.allowedOrigins.split(","));
    }

    @Override
    public void addInterceptors(final InterceptorRegistry registry) {
        registry
            .addInterceptor(this.verificationTokenInterceptor)
            .addPathPatterns(
                "/api/verification-tokens/verify",
                "/auth/reset-password"
            );
    }

    @Override
    public void addResourceHandlers(final ResourceHandlerRegistry registry) {
        registry
            .addResourceHandler("/static/**")
            .addResourceLocations("classpath:/static/")
            .resourceChain(true);
    }
}
