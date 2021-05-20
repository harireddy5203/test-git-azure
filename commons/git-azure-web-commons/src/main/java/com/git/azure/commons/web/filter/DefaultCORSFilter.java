/*
 * Copyright (c) 2021 REPLACE_CUSTOMER_NAME. All rights reserved.
 *
 * This file is part of test-git-azure.
 *
 * test-git-azure project and associated code cannot be copied
 * and/or distributed without a written permission of REPLACE_CUSTOMER_NAME,
 * and/or its subsidiaries.
 */
package com.git.azure.commons.web.filter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.git.azure.commons.SpecialCharacter;
import com.git.azure.commons.web.configuration.properties.CORSProperties;

/**
 * Filter for setting the required CORS headers.
 *
 * @author Mahalingam Iyer
 */
@Order(Ordered.HIGHEST_PRECEDENCE)
@Component
public class DefaultCORSFilter implements Filter {
    /** Gateway properties instance of type {@link CORSProperties}. */
    private final CORSProperties corsProperties;

    /**
     * Constructor.
     *
     * @param corsProperties
     *         Gateway properties instance of type {@link CORSProperties}.
     */
    public DefaultCORSFilter(final CORSProperties corsProperties) {
        this.corsProperties = corsProperties;
    }

    @Override
    public void init(final FilterConfig filterConfig) {
        // Any initializations can be handled here.
    }

    @Override
    public void doFilter(final ServletRequest request, final ServletResponse response,
                         final FilterChain chain) throws IOException, ServletException {
        final HttpServletResponse httpResponse = (HttpServletResponse) response;
        final HttpServletRequest httpRequest = (HttpServletRequest) request;
        final CORSProperties.CorsHeaders corsHeaders = corsProperties.getCors().getHeaders();

        // Does the origin header contain the allowed origins?
        final String originHeader = httpRequest.getHeader(HttpHeaders.ORIGIN);
        if (corsHeaders.getAllowedOrigins().contains(originHeader)) {
            httpResponse.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, originHeader);
            httpResponse.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, String.join(SpecialCharacter.COMMA.getValue(), corsHeaders.getAllowedMethods()));
            httpResponse.setHeader(HttpHeaders.ACCESS_CONTROL_MAX_AGE, String.valueOf(corsHeaders.getMaxAge()));
            httpResponse.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, String.valueOf(corsHeaders.isAllowCredentials()));
            httpResponse.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS, String.join(SpecialCharacter.COMMA.getValue(), corsHeaders.getAllowedHeaders()));
        }

        if (httpRequest.getMethod().equals(HttpMethod.OPTIONS.name())) {
            httpResponse.setStatus(HttpStatus.NO_CONTENT.value());
        } else {
            chain.doFilter(request, response);
        }
    }

    @Override
    public void destroy() {
        // Any cleanup activities can be done here.
    }
}
