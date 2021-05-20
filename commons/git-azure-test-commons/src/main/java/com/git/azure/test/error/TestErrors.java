/*
 * Copyright (c) 2021 REPLACE_CUSTOMER_NAME. All rights reserved.
 *
 * This file is part of test-git-azure.
 *
 * test-git-azure project and associated code cannot be copied
 * and/or distributed without a written permission of REPLACE_CUSTOMER_NAME,
 * and/or its subsidiaries.
 */
package com.git.azure.test.error;

import com.git.azure.commons.error.ErrorMessages;
import com.git.azure.commons.error.IError;
import com.git.azure.commons.error.IErrorMessages;

/**
 * Enum constants that represent the test error codes and messages that can be used while developing the unit and
 * integration tests for various features within the platform.
 * <p>
 * For more details, see the documentation on {@link IError} contract.
 *
 * @author Mahalingam Iyer
 */
public enum TestErrors implements IError {
    // NOTE:
    // Whenever a new constant is added here, ensure that the error message for the same constant is added in
    // src/main/resources/l10n/test_error_messages.properties

    FAILED_TO_ADAPT_TO_REQUESTED_TYPE,
    FAILED_TO_DERIVE_FILE_NAME,
    FAILED_TO_FIND_TEST_DATA_FOR_TEST_CASE,
    FAILED_TO_LOAD_TEST_DATA,
    RANDOM_GENERATION_STRATEGY_NOT_FOUND,
    UNSUPPORTED_TYPE;

    /** Reference to {@link IErrorMessages}, which holds the error messages. */
    private static final ErrorMessages ERROR_MESSAGES = ErrorMessages.instance("l10n/test_error_messages", TestErrors.class.getClassLoader());

    @Override
    public IErrorMessages getErrorMessages() {
        return TestErrors.ERROR_MESSAGES;
    }
}
