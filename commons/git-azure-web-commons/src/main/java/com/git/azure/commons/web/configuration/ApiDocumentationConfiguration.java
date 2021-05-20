/*
 * Copyright (c) 2021 REPLACE_CUSTOMER_NAME. All rights reserved.
 *
 * This file is part of test-git-azure.
 *
 * test-git-azure project and associated code cannot be copied
 * and/or distributed without a written permission of REPLACE_CUSTOMER_NAME,
 * and/or its subsidiaries.
 */
package com.git.azure.commons.web.configuration;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

import com.git.azure.commons.web.configuration.properties.ApiDocumentationSettings;

/**
 * Configuration file for Swagger's documentation.
 *
 * @author Mahalingam Iyer
 */
@EnableConfigurationProperties(value = {ApiDocumentationSettings.class})
@Configuration
public class ApiDocumentationConfiguration {
    /** API documentation properties. */
    private final ApiDocumentationSettings apiDocumentationSettings;

    /**
     * Constructor.
     *
     * @param apiDocumentationSettings
     *         API Documentation configuration.
     */
    public ApiDocumentationConfiguration(final ApiDocumentationSettings apiDocumentationSettings) {
        this.apiDocumentationSettings = apiDocumentationSettings;
    }

    @Bean
    public OpenAPI customOpenAPI() {
        final ApiDocumentationSettings.Contact contact = apiDocumentationSettings.getContact();

        // API Information
        final Info info = new Info()
                .title(apiDocumentationSettings.getTitle())
                .version(apiDocumentationSettings.getVersion())
                .contact(new Contact().name(contact.getName()).email(contact.getEmail()).url(contact.getUrl()))
                .license(new License().name(apiDocumentationSettings.getLicense()).url(apiDocumentationSettings.getLicenseUrl()));

        // Security scheme.
        final ApiDocumentationSettings.ApiSecurityScheme apiSecurityScheme = apiDocumentationSettings.getSecurityScheme();
        final String securitySchemeName = apiSecurityScheme.getName();

        final SecurityScheme oasSecurityScheme = new SecurityScheme()
                .name(securitySchemeName)
                .type(SecurityScheme.Type.valueOf(apiSecurityScheme.getType()))
                .scheme(apiSecurityScheme.getScheme())
                .bearerFormat(apiSecurityScheme.getBearerFormat());

        // Return the configuration object.
        return new OpenAPI()
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new Components().addSecuritySchemes(securitySchemeName, oasSecurityScheme))
                .info(info);
    }
}
