/*
 * Copyright (c) 2021 REPLACE_CUSTOMER_NAME. All rights reserved.
 *
 * This file is part of test-git-azure.
 *
 * test-git-azure project and associated code cannot be copied
 * and/or distributed without a written permission of REPLACE_CUSTOMER_NAME,
 * and/or its subsidiaries.
 */
package com.git.azure.commons.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import lombok.extern.slf4j.Slf4j;

import com.git.azure.commons.error.CommonErrors;
import com.git.azure.commons.exception.ServiceException;

/**
 * Utility class that provides helper methods to validate input data.
 *
 * @author Mahalingam Iyer
 */
@Slf4j
public final class ValidationUtils {
    /**
     * This method asserts that the provided input value is not null.
     *
     * @param inputValue
     *         Input that needs to be validated.
     * @param inputName
     *         Input name that corresponds to the provided input value.
     *
     * @return Instance of {@link ValidationUtils}.
     */
    public ValidationUtils assertNotNull(final Object inputValue, final String inputName) {
        if (Objects.isNull(inputValue)) {
            ValidationUtils.LOGGER.error("Invalid input. {} cannot be null.", inputName);
            throw ServiceException.instance(CommonErrors.ILLEGAL_ARGUMENT, inputName);
        }
        return this;
    }

    /**
     * This method asserts that the provided input value is not empty. The input value is converted to a string before
     * the assertion is done.
     *
     * @param inputValue
     *         Input that needs to be validated.
     * @param inputName
     *         Input name that corresponds to the provided input value.
     *
     * @return Instance of {@link ValidationUtils}.
     */
    public ValidationUtils assertNotEmpty(final Object inputValue, final String inputName) {
        if (inputValue instanceof String && StringUtils.isBlank((String) inputValue)) {
            ValidationUtils.LOGGER.error("Invalid input. {} cannot be null.", inputName);
            throw ServiceException.instance(CommonErrors.ILLEGAL_ARGUMENT, inputName);
        }
        return this;
    }

    /**
     * This method asserts that the provided input value is not null and not empty.
     *
     * @param inputValue
     *         Input that needs to be validated.
     * @param inputName
     *         Input name that corresponds to the provided input value.
     *
     * @return Instance of {@link ValidationUtils}.
     */
    public ValidationUtils assertNotNullAndEmpty(final Object inputValue, final String inputName) {
        return assertNotNull(inputValue, inputName).assertNotEmpty(inputValue, inputName);
    }

    /**
     * This method asserts that the provided input value is not null and not empty.
     *
     * @param inputValue
     *         Input that needs to be validated.
     * @param inputName
     *         Input name that corresponds to the provided input value.
     *
     * @return Instance of {@link ValidationUtils}.
     */
    public ValidationUtils assertCollectionNotEmpty(final Collection<?> inputValue, final String inputName) {
        assertNotNull(inputValue, inputName);

        if (inputValue.isEmpty()) {
            ValidationUtils.LOGGER.error("Invalid input. {} cannot be empty.", inputName);
            throw ServiceException.instance(CommonErrors.ILLEGAL_ARGUMENT, inputName);
        }
        return this;
    }

    /**
     * This method asserts that the provided collection is not null and not empty. In addition, all the elements are
     * checked for the 'not null' and 'not empty' checks.
     *
     * @param inputValue
     *         Input that needs to be validated.
     * @param inputName
     *         Input name that corresponds to the provided input value.
     *
     * @return Instance of {@link ValidationUtils}.
     */
    public ValidationUtils assertCollectionAndElementsNotEmpty(final Collection<?> inputValue, final String inputName) {
        // Ensure that the input value is not null and the collection is not empty.
        assertNotNull(inputValue, inputName).assertCollectionNotEmpty(inputValue, inputName);
        inputValue.forEach(value -> assertNotNullAndEmpty(inputValue, inputName));
        return this;
    }

    /**
     * This method asserts that the elements in {@code collection1} and {@code collection2} are the same (ignoring the
     * sequence / ordering).
     *
     * @param collection1
     *         Elements in collection 1.
     * @param collection2
     *         Elements in collection 2.
     * @param inputName
     *         Input name that corresponds to the provided input value.
     * @param <T>
     *         Type of the elements in the collection.
     *
     * @return Instance of {@link ValidationUtils}.
     */
    public <T> ValidationUtils assertSameCollections(final Collection<T> collection1, final Collection<T> collection2,
                                                     final String inputName) {
        // Create a copy of the collection 1.
        final List<T> copy = new ArrayList<>(collection1);
        // Remove all the collection 2 elements from this copy.
        copy.removeAll(collection2);
        if (!copy.isEmpty()) {
            throw ServiceException.instance(CommonErrors.RESOURCES_NOT_FOUND_DETAILED, inputName, copy.toString());
        }
        return this;
    }

    /**
     * This method asserts that the provided input value is a positive value (i.e. 0 or more).
     *
     * @param inputValue
     *         Input that needs to be validated.
     * @param inputName
     *         Input name that corresponds to the provided input value.
     *
     * @return Instance of {@link ValidationUtils}.
     */
    public ValidationUtils assertPositiveValue(final int inputValue, final String inputName) {
        if (inputValue < 0) {
            ValidationUtils.LOGGER.error("Invalid input. {} must be a positive value (greater than or equal to 0).", inputName);
            throw ServiceException.instance(CommonErrors.ILLEGAL_ARGUMENT_DETAILED, inputName);
        }
        return this;
    }

    /**
     * This method asserts that the provided input value is a positive value (i.e. 0 or more).
     *
     * @param inputValue
     *         Input that needs to be validated.
     * @param inputName
     *         Input name that corresponds to the provided input value.
     *
     * @return Instance of {@link ValidationUtils}.
     */
    public ValidationUtils assertPositiveValueGreaterThanZero(final int inputValue, final String inputName) {
        if (inputValue <= 0) {
            ValidationUtils.LOGGER.error("Invalid input. {} must be a positive value (greater than 0).", inputName);
            throw ServiceException.instance(CommonErrors.ILLEGAL_ARGUMENT_DETAILED, inputName);
        }
        return this;
    }

    /**
     * This method asserts that the provided input map is not null and not empty.
     *
     * @param inputMap
     *         Input that needs to be validated.
     * @param inputName
     *         Input name that corresponds to the provided input value.
     *
     * @return Instance of {@link ValidationUtils}.
     */
    public ValidationUtils assertMapNotEmpty(final Map<?, ?> inputMap, final String inputName) {
        assertNotNull(inputMap, inputName);

        if (inputMap.isEmpty()) {
            ValidationUtils.LOGGER.error("Invalid input. {} cannot be empty.", inputName);
            throw ServiceException.instance(CommonErrors.ILLEGAL_ARGUMENT_DETAILED, inputName);
        }
        return this;
    }

    /**
     * This method asserts that the provided input map is not null and not empty. In addition, this method checks for
     * the existence of the provided keys.
     *
     * @param inputMap
     *         Input that needs to be validated.
     * @param inputName
     *         Input name that corresponds to the provided input value.
     * @param keyNames
     *         Key names that needs to be checked for their existence in the provided input map. If any one of the keys
     *         is missing, this method throws an exception.
     *
     * @return Instance of {@link ValidationUtils}.
     */
    public ValidationUtils assertMapNotEmptyAndContainsKeys(final Map<?, ?> inputMap, final String inputName,
                                                            final String... keyNames) {
        // Ensure that the input value is not null and the collection is not empty.
        assertMapNotEmpty(inputMap, inputName);

        if (Objects.nonNull(keyNames) && keyNames.length > 0) {
            for (final String keyName : keyNames) {
                if (!inputMap.containsKey(keyName)) {
                    ValidationUtils.LOGGER.error("Invalid input. Unable to find {} in input map.", keyName);
                    throw ServiceException.instance(CommonErrors.MISSING_KEY_IN_MAP, keyName);
                }
            }
        }
        return this;
    }

    /**
     * Factory method to return an instance of {@link ValidationUtils}.
     *
     * @return Instance of type {@link ValidationUtils}.
     */
    public static ValidationUtils instance() {
        return new ValidationUtils();
    }
}
