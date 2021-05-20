/*
 * Copyright (c) 2021 REPLACE_CUSTOMER_NAME. All rights reserved.
 *
 * This file is part of test-git-azure.
 *
 * test-git-azure project and associated code cannot be copied
 * and/or distributed without a written permission of REPLACE_CUSTOMER_NAME,
 * and/or its subsidiaries.
 */
package com.git.azure.commons.web.configuration.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Configuration properties that holds the API Documentation settings.
 *
 * @author Mahalingam Iyer
 */
@Getter
@Setter
@NoArgsConstructor
@ConfigurationProperties(prefix = "com.git.azure.configuration")
public class ApiDocumentationSettings {
    /** Reference to the title. */
    private String title;

    /** Brief description about the API. */
    private String description;

    /** Security scheme for our APIs. */
    private ApiSecurityScheme securityScheme = new ApiSecurityScheme();

    /** Type of license for this software. */
    private String license;

    /** License url. */
    private String licenseUrl;

    /** Terms of service url. */
    private String termsOfServiceUrl;

    /** Version number. */
    private String version;

    /** Contact information. */
    private Contact contact = new Contact();

    /** Base package. */
    private String basePackage;

    /**
     * Contact details for the API.
     */
    @Data
    public static class Contact {
        /** Contact name. */
        private String name;

        /** Contact url. */
        private String url;

        /** Email address of the contact. */
        private String email;
    }

    /**
     * Security scheme for the exposed APIs.
     */
    @Data
    public static class ApiSecurityScheme {
        /** Default security scheme name. */
        public static final String DEFAULT_SECURITY_SCHEME_NAME = "bearerAuth";

        /** Security scheme name. */
        private String name = ApiSecurityScheme.DEFAULT_SECURITY_SCHEME_NAME;

        /** Security scheme. */
        private String scheme = "bearer";

        /** Security scheme type - HTTP, OAuth2, etc. */
        private String type = "HTTP";

        /** Bearer format. */
        private String bearerFormat = "JWT";
    }
}