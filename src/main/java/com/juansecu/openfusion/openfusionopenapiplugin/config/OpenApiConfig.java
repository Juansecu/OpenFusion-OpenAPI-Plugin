package com.juansecu.openfusion.openfusionopenapiplugin.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    protected OpenAPI openApi() {
        final ExternalDocumentation externalDocumentation = new ExternalDocumentation()
            .description("README.md")
            .url("https://github.com/Juansecu/OpenFusion-OpenAPI-Plugin#readme");
        final Info info = new Info()
            .description("Plugin for server applications based on OpenFusion.")
            .license(
                new License()
                    .name("MIT License")
                    .url("https://github.com/Juansecu/OpenFusion-OpenAPI-Plugin/blob/main/LICENSE")
            )
            .title("OpenFusion OpenAPI Plugin")
            .version("1.0");

        return new OpenAPI()
            .externalDocs(externalDocumentation)
            .info(info);
    }
}
