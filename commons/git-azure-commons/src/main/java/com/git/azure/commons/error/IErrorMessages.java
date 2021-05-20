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

import java.util.Locale;

import org.springframework.context.i18n.LocaleContextHolder;

/**
 * Base contract that defines the methods required to read the localized messages from the resource bundle.
 * <p>
 * See {@link ErrorMessages} for the default implementation of this contract.
 *
 * @author Mahalingam Iyer
 */
public interface IErrorMessages {
    /**
     * This method returns the error message registered against the error-code within the provided error object and for
     * the specified locale. If there is no message registered for the specific error code, this method will return the
     * error-code.
     * <p>
     * The lookup key will be generated based on the provided error code.
     *
     * @param error
     *         Error object against which the error message needs to be retrieved
     * @param locale
     *         Locale
     *
     * @return Error message for the error code wrapped within the specified error object and for the specified locale.
     */
    default String getString(IError error, Locale locale) {
        return getString(error.name(), locale);
    }

    /**
     * This method returns the error message registered against the specified error-code and for the specified locale.
     * If there is no message registered for the specific error code, this method will return the error-code.
     * <p>
     * The lookup key will be generated based on the provided error code.
     *
     * @param errorCode
     *         Error code against which the message needs to be retrieved.
     * @param locale
     *         Locale
     *
     * @return Error message for the error code wrapped within the specified error object and for the specified locale.
     */
    String getString(String errorCode, Locale locale);

    /**
     * This method returns a formatted error message registered against the error code wrapped within the provided error
     * object in the resource bundle for the specified locale. If there is no message registered for the respective
     * error code, this method will return the error-code.
     * <p>
     * The lookup key will be generated based on the provided error code.
     *
     * @param error
     *         Error object against which the error message needs to be retrieved
     * @param locale
     *         Locale
     * @param messageArgs
     *         Arguments for the placeholders in the error message
     *
     * @return Formatted error message for the provided error and locale combination
     */
    default String getFormattedString(IError error, Locale locale, Object... messageArgs) {
        return getFormattedString(error.name(), locale, messageArgs);
    }

    /**
     * This method returns a formatted error message registered against the error code wrapped within the provided error
     * object in the resource bundle for the specified locale. If there is no message registered for the respective
     * error code, this method will return the error-code.
     * <p>
     * The lookup key will be generated based on the provided error code.
     *
     * @param errorCode
     *         Error code for which the message needs to be retrieved (see {@link IError})
     * @param locale
     *         Locale
     * @param messageArgs
     *         Arguments for the placeholders in the error message
     *
     * @return Formatted error message for the provided error and locale combination
     */
    String getFormattedString(String errorCode, Locale locale, Object... messageArgs);

    /**
     * Returns the default locale
     *
     * @return Default locale of type {@link Locale}
     */
    default Locale getLocale() {
        return LocaleContextHolder.getLocale();
    }
}
