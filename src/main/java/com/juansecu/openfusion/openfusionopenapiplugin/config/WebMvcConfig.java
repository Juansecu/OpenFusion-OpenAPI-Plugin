package com.juansecu.openfusion.openfusionopenapiplugin.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

import com.juansecu.openfusion.openfusionopenapiplugin.verificationtokens.interceptors.VerificationTokenInterceptor;

@Configuration
@EnableWebMvc
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {
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
            .allowedOrigins("*");
    }

    @Override
    public void addInterceptors(final InterceptorRegistry registry) {
        registry
            .addInterceptor(this.verificationTokenInterceptor)
            .addPathPatterns(
                "/api/verification-tokens/verify"
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
