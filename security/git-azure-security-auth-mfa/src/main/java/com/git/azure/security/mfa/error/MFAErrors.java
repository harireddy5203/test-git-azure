/*
 * Copyright (c) 2021 REPLACE_CUSTOMER_NAME. All rights reserved.
 *
 * This file is part of test-git-azure.
 *
 * test-git-azure project and associated code cannot be copied
 * and/or distributed without a written permission of REPLACE_CUSTOMER_NAME,
 * and/or its subsidiaries.
 */
package com.git.azure.security.mfa.error;

import com.git.azure.commons.error.ErrorMessages;
import com.git.azure.commons.error.IError;
import com.git.azure.commons.error.IErrorMessages;

/**
 * Enum constants that represent the error codes and messages in the context of MFA (Multi-Factor Authentication)
 * functionality.
 * <p>
 * For more details, see the documentation on {@link IError} contract.
 *
 * @author Mahalingam Iyer
 */
public enum MFAErrors implements IError {
    // NOTE:
    // Whenever a new constant is added here, ensure that the error message for the same constant is added in
    // src/main/resources/l10n/mfa_error_messages.properties

    FAILED_TO_GENERATE_OTP,
    FAILED_TO_GENERATE_QRCODE,
    INVALID_OTP,
    MISSING_OTP,
    UNSUPPORTED_CHANNEL,
    UNSUPPORTED_ENCODING;

    /** Reference to {@link IErrorMessages}, which holds the error messages. */
    private static final ErrorMessages ERROR_MESSAGES = ErrorMessages.instance("l10n/mfa_error_messages", MFAErrors.class.getClassLoader());

    @Override
    public IErrorMessages getErrorMessages() {
        return MFAErrors.ERROR_MESSAGES;
    }
}