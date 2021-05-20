/*
 * Copyright (c) 2021 REPLACE_CUSTOMER_NAME. All rights reserved.
 *
 * This file is part of test-git-azure.
 *
 * test-git-azure project and associated code cannot be copied
 * and/or distributed without a written permission of REPLACE_CUSTOMER_NAME,
 * and/or its subsidiaries.
 */
package com.git.azure.security.db.configuration;

import com.git.azure.security.db.EnableDBAuthentication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.git.azure.security.db.configuration.properties.JwtTokenProperties;
import com.git.azure.security.db.model.persistence.UserPrincipalEntity;
import com.git.azure.security.db.repository.UserEntityRepository;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Configuration class that defines and configures the necessary beans.
 *
 * @author Mahalingam Iyer
 */
@EnableConfigurationProperties(JwtTokenProperties.class)
@EntityScan(basePackageClasses = {UserPrincipalEntity.class})
@EnableJpaRepositories(basePackageClasses = {UserEntityRepository.class})
@ComponentScan(basePackageClasses = {EnableDBAuthentication.class})
@Configuration
public class SecurityConfiguration {

    /** Number of rounds of hashing to apply and used by {@link BCryptPasswordEncoder}. */
    private static final int NUMBER_OF_ROUNDS_FOR_HASHING = 4;

    /**
     * This method creates a singleton bean instance of type {@link BCryptPasswordEncoder} using the strength as defined
     * by {@code WebSecurityConfiguration.NUMBER_OF_ROUNDS_FOR_HASHING}.
     *
     * @return Singleton bean instance of type {@link BCryptPasswordEncoder}.
     */
    @Bean
    public PasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder(SecurityConfiguration.NUMBER_OF_ROUNDS_FOR_HASHING);
    }
}
