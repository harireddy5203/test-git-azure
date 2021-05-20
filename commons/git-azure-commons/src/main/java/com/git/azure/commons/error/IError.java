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

import java.text.MessageFormat;
import java.util.Locale;

import org.springframework.context.i18n.LocaleContextHolder;

/**
 * Base interface to define the contracts for retrieving the messages based on their error codes.
 * <p>
 * This interface is typically implemented by the enumerated data types that provide messages based on unique codes. The
 * process is as below:
 *
 * <p>A message code needs to be added in the enumerated data type that implements {@link IError} interface.</p>
 * <p>Against the message code, a new entry is added to the resource bundle (e.g. error_messages.properties)</p>
 * <p>Every message in the resource bundle takes the form: [Message Code].[Message Code Suffix]=[Message]</p>
 * <p>Example:</p>
 * <p>USER_NOT_FOUND=Unable to find an user with username {0}.</p>
 * <pre>
 * public interface ApplicationErrors implements IError {
 *     GENERIC_ERROR,
 *     USER_NOT_FOUND;
 *     ....
 * }
 * </pre>
 * <p>Usage:</p>
 * <pre>
 * public class UsageExample {
 *     public void performSomething() {
 *          if(conditionFailed) {
 *              throw ServiceException.instance(ApplicationErrors.GENERIC_ERROR);
 *          }
 *     }
 *     public void performSomeOtherThing(userName) {
 *          if(conditionFailed) {
 *              throw ServiceException.instance(ApplicationErrors.USER_NOT_FOUND, userName);
 *          }
 *     }
 * }
 * </pre>
 *
 * @author Mahalingam Iyer
 */
public interface IError {
    /**
     * This method returns an instance of type {@link IErrorMessages}, which holds all the error messages.
     *
     * @return Instance of type {@link IErrorMessages} and cannot be null.
     */
    IErrorMessages getErrorMessages();

    /**
     * Returns the error code that are typically enum constants (for example: USER_NOT_FOUND).
     *
     * @return Error code.
     */
    String name();

    /**
     * Returns a localized (default locale) message for the error code associated with this instance.
     *
     * @return Localized message for the error code associated with this instance. Returns null if there is no message
     * associated.
     */
    default String message() {
        return message(getLocale());
    }

    /**
     * Returns a localized message (for the provided locale) for the error code associated with this instance.
     *
     * @param locale
     *         Locale
     *
     * @return Localized message (for the specified locale) for the error code associated with this instance. Returns
     * null if there is no message associated.
     */
    default String message(Locale locale) {
        return getErrorMessages().getString(this, locale);
    }

    /**
     * Returns a formatted message for the error code associated with this instance and for the default locale.
     *
     * @param messageArguments
     *         Arguments that will be used to replace the placeholders in the message string.
     *
     * @return Formatted message for the error code associated with this instance and for the default locale.
     */
    default String formattedMessage(Object... messageArguments) {
        return formattedMessage(getLocale(), messageArguments);
    }

    /**
     * Returns a formatted message for the error code associated with this instance and for the provided locale.
     *
     * @param locale
     *         Locale
     * @param messageArguments
     *         Arguments that will be used to replace the placeholders in the error message string
     *
     * @return Formatted message for the error code associated with this instance and for the provided locale.
     */
    default String formattedMessage(Locale locale, Object... messageArguments) {
        return getErrorMessages().getFormattedString(this, locale, messageArguments);
    }

    /**
     * Returns the display message based on the code and the error message. The format of the display message is
     * determined by DISPLAY_STRING_FORMAT property on {@link IError}
     *
     * @return Display message
     */
    default String toDisplayString() {
        return MessageFormat.format("{0} [{1}]", name(), message());
    }

    /**
     * Returns the default locale
     *
     * @return Default locale of type {@link Locale}
     */
    default Locale getLocale() {
        return LocaleContextHolder.getLocale();
    }
}
