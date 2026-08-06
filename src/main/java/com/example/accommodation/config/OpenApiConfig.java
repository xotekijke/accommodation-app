package com.example.accommodation.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    private static final String SCHEME_NAME = "bearerAuth";
    private static final String API_TITLE = "Accommodation API";
    private static final String API_VERSION = "v1";
    private static final String BEARER_FORMAT = "JWT";
    private static final String BEARER_SCHEME = "bearer";

    @Bean
    public OpenAPI accommodationOpenApi() {
        return new OpenAPI()
                .info(new Info().title(API_TITLE).version(API_VERSION))
                .addSecurityItem(new SecurityRequirement().addList(SCHEME_NAME))
                .components(new Components().addSecuritySchemes(SCHEME_NAME,
                        new SecurityScheme()
                                .name(SCHEME_NAME)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme(BEARER_SCHEME)
                                .bearerFormat(BEARER_FORMAT)));
    }
}
