/*
 * Copyright (c) 2021 REPLACE_CUSTOMER_NAME. All rights reserved.
 *
 * This file is part of test-git-azure.
 *
 * test-git-azure project and associated code cannot be copied
 * and/or distributed without a written permission of REPLACE_CUSTOMER_NAME,
 * and/or its subsidiaries.
 */
package com.git.azure.commons.exception;

import java.text.MessageFormat;
import java.util.Objects;

import com.git.azure.commons.error.IError;

/**
 * Default implementation of a service exception for this application.
 *
 * @author Mahalingam Iyer
 */
public class ServiceException extends RuntimeException {
    /** Serial Version Id. */
    private static final long serialVersionUID = -8552403803011920533L;

    /** Display string for the service exception. {0} - error code; {1} - error message. */
    private static final String DISPLAY_STRING = "{0}: {1}";

    /** Reference to the error code wrapped by the exception. */
    private final String errorCode;

    /**
     * Constructor.
     *
     * @param error
     *         Instance of type {@link IError} that wraps the error code and error message. Uses the message via {@code
     *         message()} on {@link IError}.
     */
    public ServiceException(final IError error) {
        this(error.name(), error.message());
    }

    /**
     * Constructor.
     *
     * @param error
     *         Instance of type {@link IError} that wraps the error code and error message. Uses the message via {@code
     *         formattedMessage()} on {@link IError}.
     * @param errorMessageArguments
     *         Arguments that will be used as placeholder values within the error message.
     */
    public ServiceException(final IError error, final Object... errorMessageArguments) {
        this(error.name(), error.formattedMessage(errorMessageArguments));
    }

    /**
     * Constructor.
     *
     * @param errorCode
     *         Error code.
     * @param formattedErrorMessage
     *         Formatted error message that will be set as the exception error message.
     */
    public ServiceException(final String errorCode, final String formattedErrorMessage) {
        super(formattedErrorMessage);
        this.errorCode = errorCode;
    }

    /**
     * This method returns the error code wrapped by this exception object.
     *
     * @return Error code.
     */
    public String errorCode() {
        return errorCode;
    }

    /**
     * This method returns a boolean indicating if the error code wrapped by the service exception matches the provided
     * error object of type {@link IError}.
     *
     * @param error
     *         Error object of type {@link IError}.
     *
     * @return True if the error code wrapped by this exception object matches the error code of the provided {@link
     * IError} object.
     */
    public boolean is(final IError error) {
        if (Objects.isNull(error)) {
            return false;
        }
        return error.name().equalsIgnoreCase(errorCode);
    }

    @Override
    public String toString() {
        return MessageFormat.format(ServiceException.DISPLAY_STRING, errorCode, getMessage());
    }

    /**
     * Factory method to create an instance of {@link ServiceException} using the provided error of type {@link
     * IError}.
     *
     * @param error
     *         Instance of type {@link IError} that wraps the error code and error message. Uses the message via {@code
     *         message()} on {@link IError}.
     *
     * @return Service exception of type {@link ServiceException}.
     */
    public static ServiceException instance(final IError error) {
        return new ServiceException(error);
    }

    /**
     * Factory method to create an instance of {@link ServiceException} using the provided error of type {@link IError}
     * and error message arguments.
     *
     * @param error
     *         Instance of type {@link IError} that wraps the error code and error message. Uses the message via {@code
     *         message()} on {@link IError}.
     * @param errorMessageArguments
     *         Arguments that will be used as placeholder values within the error message.
     *
     * @return Service exception of type {@link ServiceException}.
     */
    public static ServiceException instance(final IError error, final Object... errorMessageArguments) {
        return new ServiceException(error, errorMessageArguments);
    }

    /**
     * Factory method to create an instance of {@link ServiceException} using the provided error code and formatter
     * error message.
     *
     * @param errorCode
     *         Error code.
     * @param formattedErrorMessage
     *         Formatted error message that will be set as the exception error message.
     *
     * @return Service exception of type {@link ServiceException}.
     */
    public static ServiceException instance(final String errorCode, final String formattedErrorMessage) {
        return new ServiceException(errorCode, formattedErrorMessage);
    }
}
