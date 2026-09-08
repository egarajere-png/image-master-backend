package com.abcbank.images.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Swagger/OpenAPI setup — registers the bearer-token auth scheme so "Try it out" in Swagger UI can send a real JWT. */

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI workflowOpenAPI() {

        return new OpenAPI()
                .info(
                        new Info()
                                .title("ABC Bank Workflow API")
                                .description(
                                        "Workflow and Image Management API"
                                )
                                .version("v1.0.0")
                )
                .addSecurityItem(
                        new SecurityRequirement()
                                .addList("bearerAuth")
                )
                .components(
                        new Components()
                                .addSecuritySchemes(
                                        "bearerAuth",
                                        new SecurityScheme()
                                                .type(SecurityScheme.Type.HTTP)
                                                .scheme("bearer")
                                                .bearerFormat("JWT")
                                )
                );
    }
}