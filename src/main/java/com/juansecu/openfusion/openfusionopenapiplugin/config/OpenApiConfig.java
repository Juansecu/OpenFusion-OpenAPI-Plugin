package com.juansecu.openfusion.openfusionopenapiplugin.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Value("${spring.application.description}")
    private String description;
    @Value("${spring.application.name}")
    private String name;
    @Value("${spring.application.version}")
    private String version;

    @Bean
    protected OpenAPI openApi() {
        final ExternalDocumentation externalDocumentation = new ExternalDocumentation()
            .description("README.md")
            .url("https://github.com/Juansecu/OpenFusion-OpenAPI-Plugin#readme");
        final Info info = new Info()
            .description(this.description)
            .license(
                new License()
                    .name("MIT License")
                    .url("https://github.com/Juansecu/OpenFusion-OpenAPI-Plugin/blob/main/LICENSE")
            )
            .title(this.name)
            .version(this.version);

        return new OpenAPI()
            .externalDocs(externalDocumentation)
            .info(info);
    }
}
