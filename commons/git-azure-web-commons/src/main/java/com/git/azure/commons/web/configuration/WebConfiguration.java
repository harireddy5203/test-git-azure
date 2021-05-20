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
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.client.RestTemplate;

import com.git.azure.commons.web.FeatureScanner;
import com.git.azure.commons.web.configuration.properties.CORSProperties;
import com.git.azure.commons.web.filter.JwtAuthenticationFilter;
import com.git.azure.security.access.IPermissionEvaluator;
import com.git.azure.security.access.UserPermissionEvaluator;
import com.git.azure.security.properties.ApplicationSecurityProperties;
import com.git.azure.security.token.JwtTokenProvider;

/**
 * Configuration class that creates the necessary beans and enables the component scan for the packages that this module
 * is responsible for.
 *
 * @author Mahalingam Iyer
 */
@ComponentScan(basePackageClasses = {FeatureScanner.class})
@Import(value = {ApiDocumentationConfiguration.class})
@EnableConfigurationProperties(value = {ApplicationSecurityProperties.class, CORSProperties.class})
@Configuration
public class WebConfiguration {
    /** Number of rounds of hashing to apply and used by {@link BCryptPasswordEncoder}. */
    private static final int NUMBER_OF_ROUNDS_FOR_HASHING = 4;

    /**
     * This method creates a singleton bean instance of type {@link BCryptPasswordEncoder} using the strength as defined
     * by {@code WebSecurityConfiguration.NUMBER_OF_ROUNDS_FOR_HASHING}.
     *
     * @return Singleton bean instance of type {@link BCryptPasswordEncoder}.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(WebConfiguration.NUMBER_OF_ROUNDS_FOR_HASHING);
    }

    /**
     * This method attempts to create a singleton instance of {@link RestTemplate}.
     *
     * @return Instance of type {@link RestTemplate}.
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    /**
     * This method creates a bean instance of type {@link UserPermissionEvaluator}, which can be used in the @{@link
     * org.springframework.security.access.prepost.PreAuthorize} annotation for permission evaluation.
     * <p>
     * Example: @PreAuthorize("@userPermissionEvaluator.hasPermission('CREATE_USER')")
     *
     * @return Permission evaluator instance of type {@link IPermissionEvaluator}.
     */
    @Bean
    public IPermissionEvaluator userPermissionEvaluator() {
        return new UserPermissionEvaluator();
    }

    /**
     * This method creates a bean instance of type {@link JwtTokenProvider}, which provides functionality for JWT token
     * generation, token validation, etc.
     *
     * @param applicationSecurityProperties
     *         Configuration properties instance of type {@link ApplicationSecurityProperties}.
     *
     * @return JWT token provider instance of type {@link JwtTokenProvider}.
     */
    @Bean
    public JwtTokenProvider jwtTokenProvider(final ApplicationSecurityProperties applicationSecurityProperties) {
        return new JwtTokenProvider(applicationSecurityProperties);
    }

    /**
     * This method creates a bean of type {@link JwtAuthenticationFilter} and configured to be called before {@link
     * UsernamePasswordAuthenticationFilter}.
     *
     * @param jwtTokenProvider
     *         JWT Token provider instance of type {@link JwtTokenProvider}.
     * @param userDetailsService
     *         User details service instance of type {@link UserDetailsService}.
     *
     * @return Bean instance of type {@link JwtAuthenticationFilter}.
     */
    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter(final JwtTokenProvider jwtTokenProvider,
                                                           final UserDetailsService userDetailsService) {
        return new JwtAuthenticationFilter(jwtTokenProvider, userDetailsService);
    }
}
