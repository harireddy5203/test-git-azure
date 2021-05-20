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

import java.nio.charset.Charset;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

/**
 * Object that holds the details required to make the REST API call.
 *
 * @author Mahalingam Iyer
 */
@Slf4j
public class RestApi {
    /** Request parameter format. */
    private static final String REQUEST_PARAMETER_FORMAT = "{0}={1}";

    /** Separator to be used between the URL and request parameters. */
    private static final String URL_REQUEST_PARAMETER_SEPARATOR = "?";

    /** Separator to be used to separate multiple request parameters. */
    private static final String REQUEST_PARAMETER_SEPARATOR = "&";

    /** Default charset. */
    private static final String DEFAULT_ACCEPT_CHARSET = "UTF-8";

    /** Bearer token. */
    private static final String BEARER_TOKEN_KEYWORD = "Bearer";

    /** Format for the value of Authorization Header if the token is a Bearer token. */
    private static final String BEARER_TOKEN_FORMAT = "Bearer {0}";

    /** Format for the value of Authorization Header if the token is a Basic authentication token. */
    private static final String BASIC_AUTHORIZATION_FORMAT = "Basic {0}";

    /** Path variables in the Url. */
    private final List<String> pathVariables = new ArrayList<>();

    /** Request parameters to be appended to the Url. */
    private final Map<String, Object> requestParameters = new LinkedHashMap<>();

    /** Http Headers object containing the headers. */
    private final HttpHeaders httpHeaders = new HttpHeaders();

    /** REST API Url. */
    private String url;

    /** Request method for the API. */
    private HttpMethod requestMethod = HttpMethod.GET;

    /** Payload to be sent. Defaulted to null. */
    private Object payload;

    /** Normalized Url with path variables filled and request parameters appended. */
    private String normalizedUrl;

    /** Boolean indicating if the url has been normalized or not. */
    private boolean urlNormalized = false;

    /**
     * Default constructor which initializes few headers to their default values i.e. Content-Type to application/json,
     * Accept to application/json and Accept-Charset to UTF-8.
     */
    public RestApi() {
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        httpHeaders.setAcceptCharset(Arrays.asList(Charset.forName(RestApi.DEFAULT_ACCEPT_CHARSET)));
    }

    /**
     * Sets the url.
     *
     * @param url
     *         REST API Url.
     *
     * @return Updated instance of this object of type {@link RestApi}.
     */
    public RestApi url(final String url) {
        this.url = url;
        return this;
    }

    /**
     * Sets the path variables to be used in the url.
     * <p>
     * If the Url contains path variables, they would take the form as 'http://localhost:8081/prospects/{0}/operation/{1}?key={2}&key2={3}'.
     * <p>
     * Thus, path variable at 0th index (in the input {@code pathVariables}) replaces {0}, path variable at 1st index
     * replaces {1} and so on.
     *
     * @param pathVariables
     *         Array of path variables to be used for replacing the path-variable placeholders.
     *
     * @return Updated instance of this object of type {@link RestApi}.
     */
    public RestApi pathVariables(final String... pathVariables) {
        if (Objects.nonNull(pathVariables) && pathVariables.length > 0) {
            for (final String pathVariable : pathVariables) {
                if (!StringUtils.isBlank(pathVariable.trim())) {
                    this.pathVariables.add(pathVariable.trim());
                }
            }
        }
        return this;
    }

    /**
     * Adds the provided request parameter name / value pair to an internal map which will be appended to the url during
     * url normalization step (which typically happens when url() method is triggered on this instance).
     *
     * @param name
     *         Request parameter name.
     * @param value
     *         Request parameter value.
     *
     * @return Updated instance of this object of type {@link RestApi}.
     */
    public RestApi requestParameter(final String name, final Object value) {
        if (StringUtils.isNotBlank(name) && Objects.nonNull(value)) {
            requestParameters.put(name, value);
        }
        return this;
    }

    /**
     * Sets the request method to the provided value.
     *
     * @param requestMethod
     *         Request method to be used for the REST api invocation.
     *
     * @return Updated instance of this object of type {@link RestApi}.
     */
    public RestApi requestMethod(final HttpMethod requestMethod) {
        if (Objects.nonNull(requestMethod)) {
            this.requestMethod = requestMethod;
        }
        return this;
    }

    /**
     * This method sets the header 'Authorization' to the provided value. If the value starts with 'Bearer', the
     * provided value is used as-is else the provided value is concatenated to 'Bearer '.
     *
     * @param accessToken
     *         Access token.
     *
     * @return Updated instance of this object of type {@link RestApi}.
     */
    public RestApi authorization(final String accessToken) {
        if (StringUtils.isNotBlank(accessToken)) {
            if (accessToken.startsWith(RestApi.BEARER_TOKEN_KEYWORD)) {
                httpHeaders.set(HttpHeaders.AUTHORIZATION, accessToken);
            } else {
                httpHeaders.set(HttpHeaders.AUTHORIZATION, MessageFormat.format(RestApi.BEARER_TOKEN_FORMAT, accessToken));
            }
        }
        return this;
    }

    /**
     * This method sets the header 'Authorization' to the Basic authorization value (i.e. Authorization: Basic
     * [base64encoded string in the format username:password]).
     *
     * @param base64EncodedBytes
     *         Base64 encoded bytes in the format username:password.
     *
     * @return Updated instance of this object of type {@link RestApi}.
     */
    public RestApi basicAuthorization(final byte[] base64EncodedBytes) {
        if (Objects.nonNull(base64EncodedBytes) && base64EncodedBytes.length > 0) {
            httpHeaders.set(HttpHeaders.AUTHORIZATION, MessageFormat.format(RestApi.BASIC_AUTHORIZATION_FORMAT, new String(base64EncodedBytes)));
        }
        return this;
    }

    /**
     * This method sets the header 'Content-Type' to the provided value.
     *
     * @param contentType
     *         Content type of the payload that the client is sending to the server (for ex: application/json).
     *
     * @return Updated instance of this object of type {@link RestApi}.
     */
    public RestApi contentType(final MediaType contentType) {
        if (Objects.nonNull(contentType)) {
            httpHeaders.setContentType(contentType);
        }
        return this;
    }

    /**
     * This method sets the header 'Accept' to the provided value.
     *
     * @param accept
     *         Collection of formats that are acceptable (for ex: application/json, application/xml, etc.).
     *
     * @return Updated instance of this object of type {@link RestApi}.
     */
    public RestApi accept(final MediaType... accept) {
        if (Objects.nonNull(accept) && accept.length > 0) {
            httpHeaders.setAccept(Arrays.asList(accept));
        }
        return this;
    }

    /**
     * This method sets the header 'Accept-Charset' to the provided value.
     *
     * @param charset
     *         Charset name (for ex: UTF-8).
     *
     * @return Updated instance of this object of type {@link RestApi}.
     */
    public RestApi acceptCharset(final String charset) {
        if (StringUtils.isNotBlank(charset)) {
            httpHeaders.setAcceptCharset(Arrays.asList(Charset.forName(charset)));
        }
        return this;
    }

    /**
     * This method sets the header name and value into the {@link HttpHeaders} object maintained by this instance.
     *
     * @param name
     *         Header name.
     * @param value
     *         Header value.
     *
     * @return Updated instance of this object of type {@link RestApi}.
     */
    public RestApi header(final String name, final String value) {
        if (StringUtils.isNoneBlank(name, value)) {
            httpHeaders.set(name, value);
        }
        return this;
    }

    /**
     * Returns a boolean indicating if the specified header ({@code headerName}) is present within the collection of
     * headers maintained by this object.
     *
     * @param headerName
     *         Header to check.
     *
     * @return True if the specified header is present, false otherwise.
     */
    public boolean hasHeader(final String headerName) {
        return httpHeaders.containsKey(headerName);
    }

    /**
     * Sets the payload to be sent while making the REST api request.
     *
     * @param payload
     *         Payload to be sent with the REST api request.
     *
     * @return Updated instance of this object of type {@link RestApi}.
     */
    public RestApi payload(final Object payload) {
        this.payload = payload;
        return this;
    }

    /**
     * Returns the normalized url to be used for making REST api request. The normalization process replaces the path
     * variables with the provided path-variable values and appends any request parameters to the url.
     *
     * @return Normalized url to be used for making the REST api request.
     */
    public String url() {
        if (!urlNormalized) {
            normalizedUrl = normalizeUrlWithPathVariablesAndRequestParameters();
            urlNormalized = true;
        }
        return normalizedUrl;
    }

    /**
     * Returns the http request entity of type {@link HttpEntity} which holds the payload and http headers of type
     * {@link HttpHeaders}
     *
     * @return Http request entity of type {@link HttpEntity} which holds the payload and http headers of type {@link
     * HttpHeaders}
     */
    public HttpEntity requestEntity() {
        return new HttpEntity(payload, httpHeaders);
    }

    /**
     * Returns the Http request method
     *
     * @return Http request method of type {@link HttpMethod}
     */
    public HttpMethod requestMethod() {
        return requestMethod;
    }

    /**
     * This method attempts to make a REST endpoint call to the specified url using the provided details. The response
     * is casted back to the target type as indicated in the {@code targetType} parameter.
     *
     * @param restTemplate
     *         Rest template of type {@link RestTemplate}
     * @param targetType
     *         Target type of the response
     * @param <T>
     *         Type of the response
     *
     * @return Response entity that holds the data based on the target type as indicated in the {@code targetType}
     * parameter
     *
     * @throws RestClientException
     *         Whenever an exception is encountered during REST invocation.
     */
    public <T> ResponseEntity<T> exchange(final RestTemplate restTemplate, final Class<T> targetType) {
        final String urlToInvoke = url();

        RestApi.LOGGER.debug("Invoking Url[{}], Response type[{}]", urlToInvoke, targetType.getSimpleName());
        try {
            // Make the REST call now
            final ResponseEntity<T> response = restTemplate.exchange(urlToInvoke, requestMethod(), requestEntity(), targetType);
            final HttpStatus responseStatus = response.getStatusCode();

            RestApi.LOGGER.debug("Url [{}], Status[{}], Reason [{}]", urlToInvoke, responseStatus.value(), responseStatus.getReasonPhrase());

            return response;
        } catch (final RestClientException rce) {
            RestApi.LOGGER.error(rce.getMessage(), rce);
            throw rce;
        }
    }

    /**
     * This method attempts to make a REST endpoint call to the specified url using the provided details. The response
     * is casted back to the target type as indicated in the {@code responseType} parameter.
     *
     * @param restTemplate
     *         Rest template of type {@link RestTemplate}.
     * @param responseType
     *         Response type and is an instance of type {@link ParameterizedTypeReference}.
     * @param <T>
     *         Type of the response.
     *
     * @return Response entity that holds the data based on the target type as indicated in the {@code responseType}
     * parameter.
     *
     * @throws RestClientException
     *         Whenever an exception is encountered during REST invocation.
     */
    public <T> ResponseEntity<T> exchange(final RestTemplate restTemplate,
                                          final ParameterizedTypeReference<T> responseType) {
        final String urlToInvoke = url();

        RestApi.LOGGER.debug("Invoking Url[{}], Response type[{}]", urlToInvoke, responseType.getType().getTypeName());
        try {
            // Make the REST call now
            final ResponseEntity<T> response = restTemplate.exchange(urlToInvoke, requestMethod(), requestEntity(), responseType);
            final HttpStatus responseStatus = response.getStatusCode();

            RestApi.LOGGER.debug("Url [{}], Status[{}], Reason[{}]", urlToInvoke, responseStatus.value(), responseStatus.getReasonPhrase());

            return response;
        } catch (final RestClientException rce) {
            RestApi.LOGGER.error(rce.getMessage(), rce);
            throw rce;
        }
    }

    @Override
    public String toString() {
        return urlNormalized
                ? normalizedUrl
                : url;
    }

    /**
     * This method generates a new url using the path variables and request parameters set on the object. The path
     * variables in the url are represented as {0}, {1}, etc. If there are any request parameters, they are appended to
     * the url in the format: ?key1=value1&key2=value2&key3=value3... where key1, key2, key3 are the keys in the request
     * parameters map as identified by the {@code requestParameters} field.
     *
     * @return Generated url containing path variables and request parameters string appended to the original url.
     */
    private String normalizeUrlWithPathVariablesAndRequestParameters() {
        String generatedUrl = url;

        // Are there any path variables?
        if (!pathVariables.isEmpty()) {
            generatedUrl = MessageFormat.format(generatedUrl, pathVariables.toArray(new Object[0]));
        }

        final StringBuilder urlBuilder = new StringBuilder(generatedUrl);
        if (requestParameters.isEmpty()) {
            return urlBuilder.toString();
        }
        // If the url already contains request parameters (means the Url already has ?), then we need to append an '&' before we append the request parameters else we append a '?'
        // before we append the request parameters
        urlBuilder.append((!generatedUrl.contains(RestApi.URL_REQUEST_PARAMETER_SEPARATOR))
                                  ? RestApi.URL_REQUEST_PARAMETER_SEPARATOR
                                  : RestApi.REQUEST_PARAMETER_SEPARATOR);

        for (final Map.Entry<String, Object> entry : requestParameters.entrySet()) {
            urlBuilder.append(MessageFormat.format(RestApi.REQUEST_PARAMETER_FORMAT, entry.getKey(), entry.getValue())).append(RestApi.REQUEST_PARAMETER_SEPARATOR);
        }

        final String urlString = urlBuilder.toString();
        return urlString.substring(0, urlString.length() - 1);
    }

    /**
     * Factory method to return an instance of {@link RestApi}.
     *
     * @return Instance of type {@link RestApi}.
     */
    public static RestApi instance() {
        return new RestApi();
    }
}
