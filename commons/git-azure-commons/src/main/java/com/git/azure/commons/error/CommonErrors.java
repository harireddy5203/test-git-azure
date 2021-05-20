/*
 * Copyright (c) 2021 REPLACE_CUSTOMER_NAME. All rights reserved.
 *
 * This file is part of test-git-azure.
 *
 * test-git-azure project and associated code cannot be copied
 * and/or distributed without a written permission of REPLACE_CUSTOMER_NAME,
 * and/or its subsidiaries.
 */
package com.git.azure.commons.error;

/**
 * Enum constants that represent the common error codes and messages that can be used across the application.
 * <p>
 * For more details, see the documentation on {@link IError} contract.
 *
 * @author Mahalingam Iyer
 */
public enum CommonErrors implements IError {
    // NOTE:
    // Whenever a new constant is added here, ensure that the error message for the same constant is added in
    // src/main/resources/l10n/common_error_messages.properties

    DECRYPTION_FAILED,
    ENCRYPTION_FAILED,
    ILLEGAL_ARGUMENT,
    ILLEGAL_ARGUMENT_DETAILED,
    JSON_SERIALIZATION_FAILED,
    JSON_DESERIALIZATION_FAILED,
    MISSING_KEY_IN_MAP,
    PATTERN_REPLACEMENT_FAILED,
    RESOURCE_NOT_FOUND,
    RESOURCE_NOT_FOUND_DETAILED,
    RESOURCES_NOT_FOUND,
    RESOURCES_NOT_FOUND_DETAILED,
    ZIP_EXCEPTION;

    /** Reference to {@link IErrorMessages}, which holds the error messages. */
    private static final ErrorMessages ERROR_MESSAGES = ErrorMessages.instance("l10n/common_error_messages", CommonErrors.class.getClassLoader());

    @Override
    public IErrorMessages getErrorMessages() {
        return CommonErrors.ERROR_MESSAGES;
    }
}
