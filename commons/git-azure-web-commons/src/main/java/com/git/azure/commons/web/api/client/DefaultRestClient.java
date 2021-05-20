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

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

/**
 * Default REST Client implementation that provides functions to communicate over HTTP REST APIs.
 *
 * @author Mahalingam Iyer
 */
@Slf4j
@Component
public class DefaultRestClient extends AbstractRestClient {
    /**
     * Constructor.
     *
     * @param restTemplate
     *         A Rest Template that will be used for use-cases that have a request / session.
     */
    public DefaultRestClient(final RestTemplate restTemplate) {
        super(restTemplate);
    }

    /**
     * This method makes a request to the url as configured in the provided {@code apiDetails} parameter.
     *
     * @param apiDetails
     *         Api details of type {@link RestApi}.
     * @param targetType
     *         Target type of the response.
     * @param <T>
     *         Response target type.
     *
     * @return Response that is adapted to the provided {@code targetType}.
     */
    public <T> ResponseEntity<T> invoke(final RestApi apiDetails, final Class<T> targetType) {
        DefaultRestClient.LOGGER.debug("Invoking url[{}], targetType[{}]", apiDetails.toString(), targetType.getSimpleName());

        return super.invoke(apiDetails, targetType);
    }

    /**
     * This method makes a request to the url as configured in the provided {@code apiDetails} parameter.
     *
     * @param apiDetails
     *         Api details of type {@link RestApi}.
     * @param responseType
     *         Target type of the response.
     * @param <T>
     *         Response target type.
     *
     * @return Response that is adapted to the provided {@code responseType}.
     */
    public <T> ResponseEntity<T> invoke(final RestApi apiDetails, final ParameterizedTypeReference<T> responseType) {
        DefaultRestClient.LOGGER.debug("Invoking url[{}], targetType[{}]", apiDetails.toString(), responseType.getType().getTypeName());

        return super.invoke(apiDetails, responseType);
    }
}
