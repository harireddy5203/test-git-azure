/*
 * Copyright (c) 2021 REPLACE_CUSTOMER_NAME. All rights reserved.
 *
 * This file is part of test-git-azure.
 *
 * test-git-azure project and associated code cannot be copied
 * and/or distributed without a written permission of REPLACE_CUSTOMER_NAME,
 * and/or its subsidiaries.
 */
package com.git.azure.test.data;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.text.MessageFormat;

import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;

import com.git.azure.commons.exception.ServiceException;
import com.git.azure.commons.utils.JsonUtils;
import com.git.azure.test.error.TestErrors;

/**
 * Implementation class that is responsible to load the test data.
 * <p>
 * The test data is configured in JSON file, which is then passed to this implementation for loading it as {@link
 * com.git.azure.test.model.TestCaseData} or {@link com.git.azure.test.model.TestCasesData} instance
 * if the JSON file contains data for multiple test-cases.
 * <p>
 * Examples:
 * <p>
 * If the test data file contains data for a single test-case, loading it can be done as below:
 * <pre>
 * TestDataLoader.builder()
 *      .classLoader(..)
 *      .path(..)
 *      .build()
 *      .load("file name", TestCaseData.class);
 * </pre>
 * <p>
 * Likewise, if the test data file contains data for multiple test-cases, loading it can be done as below (notice the
 * subtle difference in the {@code load()} method):
 * <pre>
 * TestDataLoader.builder()
 *      .classLoader(..)
 *      .path(..)
 *      .build()
 *      .load("file name", TestCasesData.class);
 * </pre>
 *
 * @author Mahalingam Iyer
 */
@Slf4j
@SuperBuilder
@NoArgsConstructor
public class TestDataLoader {
    /** JSON Extension. */
    private static final String EXTENSION_JSON = ".json";

    /** Template that has a file name and the JSON extension. */
    private static final String TEMPLATE_FILENAME_WITH_JSON_EXTENSION = "{0}.json";

    /** Base path to the folder where the test data files reside. */
    private String path;

    /** Classloader that knows how to resolve the specified test data file name. */
    private ClassLoader classLoader;

    /**
     * This method loads the test data using the invoking method name as the file name containing the test data.
     * <p>
     * For example: if this method is invoked from a method named "test_CreateUser" inside the test class, the file name
     * that will be considered is "test_CreateUser.json".
     *
     * @param targetType
     *         Target type of the response object that holds the test data.
     * @param <T>
     *         Target type.
     *
     * @return Instance of type {@code targetType} that holds the test data.
     */
    public <T> T load(final Class<T> targetType) {
        final StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        if (stackTraceElements.length < 2) {
            throw ServiceException.instance(TestErrors.FAILED_TO_DERIVE_FILE_NAME);
        }

        // [0] is the Thread.currentThread().getStackTrace() method.
        // [1] is the TestDataLoader.loadSingle() method.
        // [2] is the test method that is invoking this method.
        return load(stackTraceElements[2].getMethodName(), targetType);
    }

    /**
     * This method loads the test data using the provided {@code fileName} as the name of the file that holds the test
     * data.
     * <p>
     * The response containing this test data shall be casted to the requested {@code targetType}.
     *
     * @param fileName
     *         Name of the file that contains the test data. If the file name does not contain ".json" extension, this
     *         method adds that before trying to load it.
     * @param targetType
     *         Target type of the response object that holds the test data.
     * @param <T>
     *         Target type.
     *
     * @return Instance of type {@code targetType} that holds the test data.
     */
    public <T> T load(final String fileName, final Class<T> targetType) {
        return load(classLoader.getResourceAsStream(formulateFileName(path, fileName)), targetType);
    }

    /**
     * This method loads the test data using the provided {@code inputStream} as the stream containing the test data.
     * <p>
     * The response containing this test data shall be casted to the requested {@code targetType}.
     *
     * @param inputStream
     *         Input stream that contains the test data.
     * @param targetType
     *         Target type of the response object that holds the test data.
     * @param <T>
     *         Target type.
     *
     * @return Instance of type {@code targetType} that holds the test data.
     */
    public <T> T load(final InputStream inputStream, final Class<T> targetType) {
        try {
            return JsonUtils.OBJECT_MAPPER.readValue(inputStream, targetType);
        } catch (final IOException e) {
            TestDataLoader.LOGGER.error(e.getMessage(), e);
            throw ServiceException.instance(TestErrors.FAILED_TO_LOAD_TEST_DATA, e.getMessage());
        }
    }

    /**
     * For the provided parameters, this method attempts to formulate a file name inclusive of the provided parameters.
     *
     * @param path
     *         Path to the folder containing the file.
     * @param fileName
     *         File name containing the test data. If the file name does not contain "json" extension, it will be
     *         added.
     *
     * @return Relative path (in the context of the project) that points to the file containing the test data.
     */
    private String formulateFileName(final String path, final String fileName) {
        final String modifiedFileName = fileName.endsWith(TestDataLoader.EXTENSION_JSON)
                ? fileName
                : MessageFormat.format(TestDataLoader.TEMPLATE_FILENAME_WITH_JSON_EXTENSION, fileName);

        return Paths.get(path, modifiedFileName).toString();
    }
}
