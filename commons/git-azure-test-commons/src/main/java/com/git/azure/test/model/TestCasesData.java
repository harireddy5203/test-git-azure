/*
 * Copyright (c) 2021 REPLACE_CUSTOMER_NAME. All rights reserved.
 *
 * This file is part of test-git-azure.
 *
 * test-git-azure project and associated code cannot be copied
 * and/or distributed without a written permission of REPLACE_CUSTOMER_NAME,
 * and/or its subsidiaries.
 */
package com.git.azure.test.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import com.git.azure.commons.exception.ServiceException;
import com.git.azure.test.error.TestErrors;

/**
 * An implementation of an experience model that captures the test data for multiple test-cases and can be applied to
 * "unit" and "integration" tests.
 *
 * @author Mahalingam Iyer
 */
@EqualsAndHashCode(of = {"testCases"})
@ToString(of = {"testCases"})
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public class TestCasesData {
    /** Collection of test data for various test-cases belonging to this feature. */
    @Builder.Default
    private Collection<TestCaseData> testCases = new ArrayList<>();

    /**
     * This method attempts to find the test data for the specified test case name.
     *
     * @param testCaseName
     *         Test case name.
     *
     * @return An {@link Optional} instance containing the {@link TestCaseData} for the specified {@code testCaseName}
     * if found, else returns an empty {@link Optional}.
     */
    public Optional<TestCaseData> findTestData(final String testCaseName) {
        if (StringUtils.isBlank(testCaseName) || testCases.isEmpty()) {
            return Optional.empty();
        }

        return testCases.stream()
                .filter(testCaseData -> testCaseData.getTestName().equals(testCaseName))
                .findFirst();
    }

    /**
     * This method attempts to return the test data for the specified test case name. If there is no test data found,
     * this method throws an exception.
     *
     * @param testCaseName
     *         Test case name.
     *
     * @return Test data of type {@link TestCaseData} if found, else an exception is thrown.
     */
    public TestCaseData findTestDataOrThrow(final String testCaseName) {
        return findTestData(testCaseName).orElseThrow(() -> ServiceException.instance(TestErrors.FAILED_TO_FIND_TEST_DATA_FOR_TEST_CASE, testCaseName));
    }
}
