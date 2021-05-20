/*
 * Copyright (c) 2021 REPLACE_CUSTOMER_NAME. All rights reserved.
 *
 * This file is part of test-git-azure.
 *
 * test-git-azure project and associated code cannot be copied
 * and/or distributed without a written permission of REPLACE_CUSTOMER_NAME,
 * and/or its subsidiaries.
 */
package com.git.azure.security.configuration;

import com.git.azure.security.properties.ApplicationSecurityProperties;
import com.git.azure.security.token.JwtTokenProvider;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Implementation for configuring Web Security for the application..
 *
 * @author Mahalingam Iyer
 */
@Order(SecurityProperties.BASIC_AUTH_ORDER - 2)
@Import(value ={JwtTokenProvider.class})
@EnableWebSecurity
@Configuration
public class WebSecurityConfiguration extends AbstractWebSecurityConfiguration {
    /**
     * Constructor.
     *
     * @param userDetailsService
     *         Service implementation of type {@link UserDetailsService}.
     * @param jwtTokenProvider
     *         JWT token provider instance of type {@link JwtTokenProvider}.
     */
    public WebSecurityConfiguration(final PasswordEncoder passwordEncoder, final UserDetailsService userDetailsService,
                                    final JwtTokenProvider jwtTokenProvider, final ApplicationSecurityProperties applicationSecurityProperties) {
        super(passwordEncoder,userDetailsService, jwtTokenProvider, applicationSecurityProperties);
    }

}