/*
 * Copyright (c) 2021 REPLACE_CUSTOMER_NAME. All rights reserved.
 *
 * This file is part of test-git-azure.
 *
 * test-git-azure project and associated code cannot be copied
 * and/or distributed without a written permission of REPLACE_CUSTOMER_NAME,
 * and/or its subsidiaries.
 */
package com.git.azure.commons.web.api.exception.handler;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import lombok.extern.slf4j.Slf4j;

import com.git.azure.commons.error.CommonErrors;
import com.git.azure.commons.exception.ResourceNotFoundException;
import com.git.azure.commons.exception.ServiceException;
import com.git.azure.commons.web.api.error.ApiErrors;
import com.git.azure.commons.web.api.exception.response.ApiExceptionResponse;
import com.git.azure.commons.web.api.exception.response.ConstraintValidationFailuresResponse;

/**
 * Implementation of an exception handler and deals with a centralized exception handling for all methods that are
 * annotated with @RequestMapping annotation.
 *
 * @author Mahalingam Iyer
 */
@Slf4j
@ControllerAdvice
public class DefaultApiExceptionHandler extends ResponseEntityExceptionHandler {
    /** Environment instance. */
    private final Environment env;

    /**
     * Constructor.
     *
     * @param env
     *         Environment instance.
     */
    public DefaultApiExceptionHandler(final Environment env) {
        this.env = env;
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(final MethodArgumentNotValidException ex,
                                                                  @NonNull final HttpHeaders headers,
                                                                  @NonNull final HttpStatus status,
                                                                  @NonNull final WebRequest request) {
        // Get the errors from the binding result.
        final List<ObjectError> errors = ex.getBindingResult().getAllErrors();

        final Collection<ConstraintValidationFailuresResponse.FieldError> fieldErrors = new LinkedHashSet<>();
        // Get the exception messages
        if (errors.isEmpty()) {
            fieldErrors.add(ConstraintValidationFailuresResponse.FieldError.builder()
                                    .fieldName(CommonErrors.ILLEGAL_ARGUMENT.name())
                                    .errorMessage(ExceptionUtils.getMessage(ex))
                                    .build());
        } else {
            for (final ObjectError fieldError : errors) {
                final String propertyName = fieldError.getDefaultMessage();
                final String fieldName = fieldError instanceof FieldError
                        ? ((FieldError) fieldError).getField()
                        : propertyName;
                if (Objects.nonNull(propertyName)) {
                    fieldErrors.add(ConstraintValidationFailuresResponse.FieldError.builder()
                                            .fieldName(fieldName)
                                            .errorMessage(env.getProperty(propertyName))
                                            .build());
                }
            }
        }
        if (DefaultApiExceptionHandler.LOGGER.isErrorEnabled()) {
            DefaultApiExceptionHandler.LOGGER.error(fieldErrors.toString(), ex);
        }

        final ConstraintValidationFailuresResponse apiResponse = ConstraintValidationFailuresResponse.builder()
                .errorCode(CommonErrors.ILLEGAL_ARGUMENT.name())
                .fieldErrors(fieldErrors)
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .headers(headers)
                .body(apiResponse);
    }

    /**
     * Handles exceptions of type {@link ConstraintViolation}.
     *
     * @param ex
     *         Constraint violation exception.
     * @param request
     *         Web request.
     *
     * @return Response entity containing the details of the exception.
     */
    @ExceptionHandler({ConstraintViolationException.class})
    public ResponseEntity<Object> handleConstraintViolation(ConstraintViolationException ex, WebRequest request) {
        // Get the errors from the exception.
        final Collection<ConstraintViolation<?>> errors = ex.getConstraintViolations();
        final Collection<ConstraintValidationFailuresResponse.FieldError> fieldErrors = new LinkedHashSet<>();
        // Get the exception messages
        if (errors.isEmpty()) {
            fieldErrors.add(ConstraintValidationFailuresResponse.FieldError.builder()
                                    .fieldName(CommonErrors.ILLEGAL_ARGUMENT.name())
                                    .errorMessage(ExceptionUtils.getMessage(ex))
                                    .build());
        } else {
            for (final ConstraintViolation<?> fieldError : errors) {
                fieldErrors.add(ConstraintValidationFailuresResponse.FieldError.builder()
                                        .fieldName(fieldError.getPropertyPath().toString())
                                        .errorMessage(fieldError.getMessage())
                                        .build());
            }

        }
        if (DefaultApiExceptionHandler.LOGGER.isErrorEnabled()) {
            DefaultApiExceptionHandler.LOGGER.error(fieldErrors.toString(), ex);
        }

        final ConstraintValidationFailuresResponse apiResponse = ConstraintValidationFailuresResponse.builder()
                .errorCode(CommonErrors.ILLEGAL_ARGUMENT.name())
                .fieldErrors(fieldErrors)
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .headers(new HttpHeaders())
                .body(apiResponse);
    }

    /**
     * Handles exception of types {@link AccessDeniedException}.
     *
     * @param ex
     *         An exception object of type {@link AccessDeniedException} if the exception is to be handled by this
     *         method.
     * @param request
     *         Web request of type {@link WebRequest}.
     *
     * @return Response entity containing the details of the exception.
     */

    @ExceptionHandler({AccessDeniedException.class})
    public ResponseEntity<Object> handleAccessDeniedException(Exception ex, WebRequest request) {
        // Get the exception message
        final String message = ExceptionUtils.getMessage(ex);

        DefaultApiExceptionHandler.LOGGER.error(message, ex);

        final ApiExceptionResponse apiResponse = ApiExceptionResponse.builder()
                .errorCode(ApiErrors.ACCESS_DENIED.name())
                .errorMessage(ApiErrors.ACCESS_DENIED.message())
                .build();

        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .headers(new HttpHeaders())
                .body(apiResponse);
    }


    /**
     * Handles runtime exceptions of type {@link NullPointerException}, {@link IllegalArgumentException} or {@link
     * IllegalStateException}.
     *
     * @param ex
     *         Runtime exception object and should be one of {@link NullPointerException} or {@link
     *         IllegalArgumentException} or {@link IllegalStateException} if they are to be handled by this method.
     * @param request
     *         Web request of type {@link WebRequest}.
     *
     * @return Response entity containing the details of the exception.
     */
    @ExceptionHandler({NullPointerException.class, IllegalArgumentException.class, IllegalStateException.class, IOException.class})
    public ResponseEntity<Object> handleInternal(final RuntimeException ex, final WebRequest request) {
        // Get the exception message
        final String message = ExceptionUtils.getMessage(ex);

        DefaultApiExceptionHandler.LOGGER.error(message, ex);

        final ApiExceptionResponse apiResponse = ApiExceptionResponse.builder()
                .errorCode(ApiErrors.GENERIC_ERROR.name())
                .errorMessage(message)
                .build();

        return handleExceptionInternal(ex, apiResponse, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    /**
     * Handles exceptions of type {@link ServiceException}.
     *
     * @param ex
     *         Exception instance of type {@link ServiceException}.
     * @param request
     *         Web request.
     *
     * @return Response entity containing the details of the exception.
     */
    @ExceptionHandler({ServiceException.class})
    protected ResponseEntity<Object> handleServiceException(final ServiceException ex, final WebRequest request) {
        DefaultApiExceptionHandler.LOGGER.error("Service Exception: error code {}, error message {}", ex.errorCode(), ex.getMessage());
        DefaultApiExceptionHandler.LOGGER.debug(ex.getMessage(), ex);

        final ApiExceptionResponse apiResponse = ApiExceptionResponse.builder()
                .errorCode(ex.errorCode())
                .errorMessage(ex.getMessage())
                .build();

        HttpStatus status = HttpStatus.BAD_REQUEST;
        if (ex instanceof ResourceNotFoundException) {
            status = HttpStatus.NOT_FOUND;
        }
        return ResponseEntity.status(status)
                .headers(new HttpHeaders())
                .body(apiResponse);
    }
}