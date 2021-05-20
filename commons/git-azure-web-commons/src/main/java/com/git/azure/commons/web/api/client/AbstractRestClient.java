/*
 * Copyright (c) 2021 REPLACE_CUSTOMER_NAME. All rights reserved.
 *
 * This file is part of test-git-azure.
 *
 * test-git-azure project and associated code cannot be copied
 * and/or distributed without a written permission of REPLACE_CUSTOMER_NAME,
 * and/or its subsidiaries.
 */
package com.git.azure.commons.web.api.client;

import java.util.Map;
import java.util.Optional;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.git.azure.commons.exception.ServiceException;
import com.git.azure.commons.web.api.error.ApiErrors;

/**
 * Abstract implementation of client contract that provides the functionality to make REST API invocations.
 *
 * @author Mahalingam Iyer
 */
public abstract class AbstractRestClient {
    /** Key in the response map that holds the error code. */
    private static final String ERROR_CODE = "errorCode";

    /** Error message. */
    private static final String ERROR_MESSAGE = "errorMessage";

    /** Rest Template that will be used for use-cases that have a request / session. */
    protected final RestTemplate restTemplate;

    /**
     * Constructor.
     *
     * @param restTemplate
     *         Rest Template that will be used for use-cases that have a request / session.
     */
    public AbstractRestClient(final RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * This method attempts to invoke the REST API using the details in the provided {@code apiDetails} parameter.
     *
     * @param apiDetails
     *         Api details of type {@link RestApi} containing the details about the REST API to be invoked.
     * @param targetType
     *         Target type of the response.
     * @param <T>
     *         Response type.
     *
     * @return Response of the REST API invocation that is adapted to the specified {@code targetType}.
     */
    public <T> ResponseEntity<T> invoke(final RestApi apiDetails, final Class<T> targetType) {
        return apiDetails.exchange(restTemplate, targetType);
    }

    /**
     * This method attempts to invoke the REST API using the details in the provided {@code apiDetails} parameter.
     *
     * @param apiDetails
     *         Api details of type {@link RestApi} containing the details about the REST API to be invoked.
     * @param responseType
     *         Target type of the response.
     * @param <T>
     *         Response type.
     *
     * @return Response of the REST API invocation that is adapted to the specified {@code targetType}.
     */
    public <T> ResponseEntity<T> invoke(final RestApi apiDetails, final ParameterizedTypeReference<T> responseType) {
        return apiDetails.exchange(restTemplate, responseType);
    }

    /**
     * This method processes the response. If the http status of the response represents 4xx or 5xx error, this method
     * throws a service exception of type {@link ServiceException} with the appropriate error code.
     *
     * @param response
     *         Response entity of type {@link ResponseEntity}.
     * @param <T>
     *         Type of the response.
     *
     * @return Response body. If the http status of the response represents 4xx or 5xx error, this method throws a
     * service exception of type {@link ServiceException} with the appropriate error code.
     */
    @SuppressWarnings(value = {"unchecked", "rawtypes"})
    protected <T> T processResponse(final ResponseEntity<T> response) {
        if (response == null) {
            return null;
        }

        // Does the response represent 2xx http status code?
        final HttpStatus status = response.getStatusCode();
        if (status.is4xxClientError() || status.is5xxServerError()) {
            // Is there a body that contains errorCode, errorMessage and errorTrace?
            if (response.getBody() instanceof Map) {
                final Map<String, Object> errorResponse = (Map) response.getBody();
                final String errorCode = Optional.ofNullable(errorResponse.get(AbstractRestClient.ERROR_CODE)).orElse(ApiErrors.GENERIC_ERROR.name()).toString();
                final String errorMessage = Optional.ofNullable(errorResponse.get(AbstractRestClient.ERROR_MESSAGE)).orElse(status.getReasonPhrase()).toString();
                throw ServiceException.instance(errorCode, errorMessage);
            }
            throw ServiceException.instance(ApiErrors.GENERIC_ERROR, status.getReasonPhrase());
        }
        return response.getBody();
    }
}
