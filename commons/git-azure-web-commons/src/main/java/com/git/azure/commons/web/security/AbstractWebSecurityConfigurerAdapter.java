/*
 * Copyright (c) 2021 REPLACE_CUSTOMER_NAME. All rights reserved.
 *
 * This file is part of test-git-azure.
 *
 * test-git-azure project and associated code cannot be copied
 * and/or distributed without a written permission of REPLACE_CUSTOMER_NAME,
 * and/or its subsidiaries.
 */
package com.git.azure.commons.web.security;

import java.io.PrintWriter;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.git.azure.commons.web.model.experience.AuthenticationResponse;
import com.git.azure.security.token.JwtTokenProvider;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.git.azure.commons.web.filter.JwtAuthenticationFilter;
import com.git.azure.commons.web.security.authentication.JwtAuthenticationEntryPoint;
import com.git.azure.security.properties.ApplicationSecurityProperties;

import javax.servlet.http.HttpServletResponse;

/**
 * An abstract implementation of a {@link WebSecurityConfigurerAdapter}.
 * <p>
 * Subclasses can override and tighten the security aspects.
 *
 * @author Mahalingam Iyer
 */
public abstract class AbstractWebSecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {
    /** Singleton instance of ObjectMapper for JSON serialization. */
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    /** Configuration properties instance of type {@link ApplicationSecurityProperties}. */
    protected final ApplicationSecurityProperties applicationSecurityProperties;

    /** Configuration properties instance of type {@link JwtAuthenticationFilter}. */
    protected final JwtAuthenticationFilter jwtAuthenticationFilter;

    /** JWT token provider that is responsible for token generation, validation, etc. */
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * Constructor.
     *
     * @param applicationSecurityProperties
     *         Configuration properties instance of type {@link ApplicationSecurityProperties}.
     * @param jwtAuthenticationFilter
     *         Configuration properties instance of type {@link JwtAuthenticationFilter}.
     * @param jwtTokenProvider
     *        Configuration properties instance of type {@link JwtTokenProvider}.
     */
    public AbstractWebSecurityConfigurerAdapter(final ApplicationSecurityProperties applicationSecurityProperties,
                                                final JwtAuthenticationFilter jwtAuthenticationFilter, final JwtTokenProvider jwtTokenProvider) {
        this.applicationSecurityProperties = applicationSecurityProperties;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    protected void configure(final HttpSecurity http) throws Exception {
        // @formatter:off
        http.csrf().disable()
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling()
                .authenticationEntryPoint(getAuthenticationEntryPoint())
            .and()
                .authorizeRequests()
                .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .antMatchers(getUnsecuredEndpoints().toArray(new String[0])).permitAll()
                .anyRequest().authenticated()
//                .anyRequest().permitAll()
            .and()
                .formLogin()
                .failureHandler(getAuthenticationFailureHandler())
                .successHandler(getAuthenticationSuccessHandler())
            .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
                .logout();

        // Set the JWTAuthenticationFilter to be called before the UsernamePasswordAuthenticationFilter
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        // @formatter:on
    }
    /**
     * This method returns an instance of type {@link AuthenticationEntryPoint}. The default behavior returns an
     * instance of type {@link JwtAuthenticationEntryPoint}.
     * <p>
     * Subclasses can override to return a different type of {@link AuthenticationEntryPoint}.
     *
     * @return Instance of type {@link AuthenticationEntryPoint}.
     */
    protected AuthenticationEntryPoint getAuthenticationEntryPoint() {
        return new JwtAuthenticationEntryPoint();
    }

    /**
     * This method returns a collection of unsecured endpoints. Subclasses can override to return their own unsecured
     * endpoints.
     *
     * @return Collection of endpoints that are unsecured i.e. these endpoints can be accessed without any
     * authentication.
     */
    protected Collection<String> getUnsecuredEndpoints() {
        return applicationSecurityProperties.getEndpoints().getUnsecured();
    }

    /**
     * This method returns the handler that is responsible to handle the case when a successful authentication happens.
     * We will return a JSON response (instead of the standard url redirection).
     *
     * @return Handler of type {@link AuthenticationSuccessHandler}, which will be called / triggered whenever a
     * successful authentication happens.
     */
    protected AuthenticationSuccessHandler getAuthenticationSuccessHandler() {
        return (request, response, authentication) -> {
            response.setStatus(HttpServletResponse.SC_OK);
            final String accessToken = jwtTokenProvider.generateToken(authentication);
            // @formatter:off
            final AuthenticationResponse authResponse = AuthenticationResponse.builder()
                    .accessToken(accessToken)
                    .build();
            // @formatter:on

            final PrintWriter writer = response.getWriter();
            AbstractWebSecurityConfigurerAdapter.OBJECT_MAPPER.writeValue(writer, authResponse);
            writer.flush();
        };
    }

    /**
     * This method returns the handler that is responsible to handle the case when an authentication failure happens. We
     * will return a JSON response (instead of the standard url redirection).
     *
     * @return Handler of type {@link AuthenticationFailureHandler}, which will be called / triggered whenever an
     * authentication failure occurs.
     */
    protected AuthenticationFailureHandler getAuthenticationFailureHandler() {
        return (request, response, exception) -> {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

            final Map<String, Object> responseData = new HashMap<>();
            responseData.put("message", exception.getMessage());

            final PrintWriter writer = response.getWriter();
            AbstractWebSecurityConfigurerAdapter.OBJECT_MAPPER.writeValue(writer, responseData);
            writer.flush();
        };
    }
}
