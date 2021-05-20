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

import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.git.azure.commons.web.model.experience.AuthenticationResponse;
import com.git.azure.security.configuration.filter.JwtAuthenticationFilter;
import com.git.azure.security.properties.ApplicationSecurityProperties;
import com.git.azure.security.token.JwtTokenProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;

import com.fasterxml.jackson.databind.ObjectMapper;


/**
 * Abstract implementation for Web Security and sets up the basic security configuration (like authentication entry
 * point, success / failure handlers, etc.).
 * <p>
 * It is the responsibility of the subclasses to annotate their implementations using @{@link Configuration}, @{@link
 * EnableWebSecurity}, @{@link org.springframework.core.annotation.Order}
 *
 * @author Mahalingam Iyer
 */
public abstract class AbstractWebSecurityConfiguration extends WebSecurityConfigurerAdapter {
    /** Configuration properties instance of type {@link ApplicationSecurityProperties}. */
    protected final ApplicationSecurityProperties applicationSecurityProperties;

    /** Singleton instance of ObjectMapper for JSON serialization. */
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    /** Password encoder of type {@link PasswordEncoder}. */
    protected final PasswordEncoder passwordEncoder;

    /** Service implementation of type {@link UserDetailsService}. */
    protected final UserDetailsService userDetailsService;

    /** JWT token provider instance of type {@link JwtTokenProvider}. */
    protected final JwtTokenProvider jwtTokenProvider;

    /**
     * Constructor.
     *
     * @param passwordEncoder
     *         Password encoder of type {@link PasswordEncoder}.
     * @param userDetailsService
     *         Service implementation of type {@link UserDetailsService}.
     * @param jwtTokenProvider
     *         JWT token provider instance of type {@link JwtTokenProvider}.
     */
    public AbstractWebSecurityConfiguration(final PasswordEncoder passwordEncoder,
                                            final UserDetailsService userDetailsService,
                                            final JwtTokenProvider jwtTokenProvider, final ApplicationSecurityProperties applicationSecurityProperties) {
        this.passwordEncoder = passwordEncoder;
        this.userDetailsService = userDetailsService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.applicationSecurityProperties = applicationSecurityProperties;
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    /**
     * This method creates a bean of type {@link JwtAuthenticationFilter} and configured to be called before {@link
     * UsernamePasswordAuthenticationFilter}.
     *
     * @return Bean instance of type {@link JwtAuthenticationFilter}.
     */
    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtTokenProvider, userDetailsService);
    }

    @Override
    protected void configure(final HttpSecurity httpSecurity) throws Exception {
        // @formatter:off
        httpSecurity.csrf().disable()
                    .exceptionHandling()
                        .authenticationEntryPoint(getAuthenticationEntryPoint())
                .and()
                    .authorizeRequests()
                        .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .antMatchers(getUnsecuredEndpoints().toArray(new String[0])).permitAll()
                        .anyRequest().authenticated()
                .and()
                    .formLogin()
                        .failureHandler(getAuthenticationFailureHandler())
                        .successHandler(getAuthenticationSuccessHandler())
                .and()
                    .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                    .logout();
		// @formatter:on

        // Set the JWTAuthenticationFilter to be called before the UsernamePasswordAuthenticationFilter
        httpSecurity.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
    }

    @Override
    protected void configure(final AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
    }

    /**
     * This method returns the authentication entry point instance of type {@link AuthenticationEntryPoint} that is
     * responsible to initiate an authentication scheme.
     *
     * @return Authentication entry point of type {@link AuthenticationEntryPoint} that is responsible to initiate an
     * authentication scheme.
     */
    protected AuthenticationEntryPoint getAuthenticationEntryPoint() {
        return new BasicAuthenticationEntryPoint();
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
            AbstractWebSecurityConfiguration.OBJECT_MAPPER.writeValue(writer, authResponse);
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
            AbstractWebSecurityConfiguration.OBJECT_MAPPER.writeValue(writer, responseData);
            writer.flush();
        };
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
}