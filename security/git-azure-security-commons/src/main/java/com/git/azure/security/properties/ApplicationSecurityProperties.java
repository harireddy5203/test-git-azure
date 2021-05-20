/*
 * Copyright (c) 2021 REPLACE_CUSTOMER_NAME. All rights reserved.
 *
 * This file is part of test-git-azure.
 *
 * test-git-azure project and associated code cannot be copied
 * and/or distributed without a written permission of REPLACE_CUSTOMER_NAME,
 * and/or its subsidiaries.
 */
package com.git.azure.security.properties;

import java.util.Collection;
import java.util.LinkedHashSet;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Configuration properties instance that holds the security aspects of the application.
 *
 * @author Mahalingam Iyer
 */
@ConfigurationProperties(prefix = "com.git.azure.configuration.security")
@Getter
@Setter
@NoArgsConstructor
public class ApplicationSecurityProperties {
    /** Boolean indicating if PBAC (Permission Based Access Control) is enabled or not. */
    private PermissionBasedAccessControl pbac = new PermissionBasedAccessControl();

    /** Endpoints security. */
    private Endpoints endpoints = new Endpoints();

    /** Authentication related settings. */
    private Authentication auth = new Authentication();

    @Getter
    @Setter
    @NoArgsConstructor
    public static class PermissionBasedAccessControl {
        /** Enabled flag to indicate if PBAC is enabled or not. */
        private boolean enabled = true;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class Endpoints {
        /** Collection of unsecured endpoints. */
        private Collection<String> unsecured = new LinkedHashSet<>();
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class Authentication {
        /** Token related settings. */
        private AuthToken token = new AuthToken();
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class AuthToken {
        /** Secret that is used for signing the token. */
        private String secret;

        /** JWT Token expiration interval in milliseconds. Defaulted to 7 days. */
        private Integer expirationIntervalInHours = 7 * 24;
    }
}
