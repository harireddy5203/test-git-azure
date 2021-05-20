/*
 * Copyright (c) 2021 REPLACE_CUSTOMER_NAME. All rights reserved.
 *
 * This file is part of test-git-azure.
 *
 * test-git-azure project and associated code cannot be copied
 * and/or distributed without a written permission of REPLACE_CUSTOMER_NAME,
 * and/or its subsidiaries.
 */
package com.git.azure.security.configuration.filter;

import com.git.azure.commons.web.utils.HttpUtils;
import com.git.azure.security.token.JwtTokenProvider;
import com.git.azure.security.token.TokenType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * A filter that gets called before {@link org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter}
 * and is responsible to validate the JWT token.
 *
 * @author Mahalingam Iyer
 */
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    /** JWT token provider that is responsible for token generation, validation, etc. */
    private final JwtTokenProvider jwtTokenProvider;

    /** Service implementation of type {@link UserDetailsService}. */
    private final UserDetailsService userDetailsService;

    /**
     * Constructor.
     *
     * @param jwtTokenProvider
     *         JWT token provider that is responsible for token generation, validation, etc.
     * @param userDetailsService
     *         Service implementation of type {@link UserDetailsService}.
     */
    public JwtAuthenticationFilter(final JwtTokenProvider jwtTokenProvider,
                                   final UserDetailsService userDetailsService) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(@NonNull final HttpServletRequest request,
                                    @NonNull final HttpServletResponse response,
                                    @NonNull final FilterChain filterChain) throws ServletException, IOException {
        try {
            final String bearerToken = HttpUtils.extractTokenFromAuthorizationHeader(request, TokenType.BEARER);
            if (StringUtils.isNotBlank(bearerToken) && jwtTokenProvider.isTokenValid(bearerToken)) {
                // TODO: Convert the token to UserDetails (thereby avoiding database trips).
                final String username = jwtTokenProvider.getSubjectForToken(bearerToken);
                final UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                final UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (final Exception ex) {
            JwtAuthenticationFilter.LOGGER.error(ex.getMessage());
            // If debug is enabled, print the trace.
            if (JwtAuthenticationFilter.LOGGER.isDebugEnabled()) {
                JwtAuthenticationFilter.LOGGER.error(ex.getMessage(), ex);
            }
        }
        filterChain.doFilter(request, response);
    }
}