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

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.fasterxml.jackson.databind.util.StdConverter;

import com.git.azure.commons.SpecialCharacter;
import com.git.azure.test.data.DataGenerationStrategy;

/**
 * Implementation class that gets called after the deserialization of the {@link TestCaseData} instances.
 *
 * @author Mahalingam Iyer
 */
@SuppressWarnings(value = {"unchecked", "rawtypes"})
@Slf4j
@NoArgsConstructor
public class TestCaseDataPostProcessor extends StdConverter<TestCaseData, TestCaseData> {
    @Override
    public TestCaseData convert(final TestCaseData testCaseData) {
        // Initialize the variables data.
        initializeVariablesData(testCaseData.getVariables());

        // Get the input data and recursively update the variables.
        recursivelyUpdateVariables(testCaseData.getVariables(), testCaseData.getInput());

        // Get the mock data and recursively update the variables.
        final MockData mockData = testCaseData.getMock();
        if (Objects.nonNull(mockData)) {
            // Get the input on the mock and recursively update the variables.
            recursivelyUpdateVariables(testCaseData.getVariables(), mockData.getInput());
            // Get the output on the mock and recursively update the variables.
            recursivelyUpdateVariables(testCaseData.getVariables(), mockData.getOutput());
        }

        return testCaseData;
    }

    /**
     * This method attempts to walk-through all the variables defined in the test-case. If any variables refer to data
     * generation strategies, the data will be generated the variable updated accordingly.
     *
     * @param variables
     *         Map of variables where the key is the variable name and the value can be actual value of the variable or
     *         a data generation strategy for the variable.
     */
    private void initializeVariablesData(final Map<String, VariableDefinition> variables) {
        if (Objects.isNull(variables) || variables.isEmpty()) {
            return;
        }

        for (final Map.Entry<String, VariableDefinition> entry : variables.entrySet()) {
            final VariableDefinition variable = entry.getValue();

            // Does the value represent a token (i.e. generation strategy like $randomString, etc.).
            if (Objects.nonNull(variable) && variable.usesDataGenerationStrategy()) {
                // Capture the configurations on the variable definition.
                final int length = variable.getLength();
                final int startInclusive = variable.getStartInclusive();
                final int endExclusive = variable.getEndExclusive();

                final Object currentVariableValue = variable.getValue();
                if (Objects.isNull(currentVariableValue)) {
                    Object variableValue;
                    final DataGenerationStrategy strategy = variable.getStrategy();
                    if (length > 0 && strategy.applicableForStringType()) {
                        variableValue = strategy.generate(length, length);
                    } else if (startInclusive >= 0 && endExclusive > 0) {
                        variableValue = strategy.generate(startInclusive, endExclusive);
                    } else {
                        variableValue = strategy.generate();
                    }

                    variable.setValue(variableValue);
                } else {
                    // Does the value point to a string? If so, it is possible that the value can contain one or
                    // more strategies.
                    if (currentVariableValue instanceof String) {
                        variable.setValue(updateAllStrategiesInString(currentVariableValue.toString()));
                    }
                }
            }
        }
    }

    /**
     * This method recursively walks through the {@code dataProvider} and updates any variables with the actual values.
     *
     * @param variables
     *         Map containing the list of variables.
     * @param testData
     *         Instance that provides the test data. This test data will be walked through in a recursive fashion for
     *         updating any variable names usage.
     */
    private void recursivelyUpdateVariables(final Map<String, VariableDefinition> variables, final TestData testData) {
        if (Objects.nonNull(testData)) {
            // Walk-through the data and update.
            recursivelyUpdateVariablesInMap(variables, testData.getData());
            // Walk-through the metadata as well.
            recursivelyUpdateVariablesInMap(variables, testData.getMetadata());
        }
    }

    /**
     * This method recursively walks through the {@code data} map and updates any variables with the actual values.
     *
     * @param variables
     *         Map containing the list of variables.
     * @param data
     *         Test data in a {@link Map} format. This test data will be walked through in a recursive fashion for
     *         updating any variable names usage.
     */
    private void recursivelyUpdateVariablesInMap(final Map<String, VariableDefinition> variables,
                                                 final Map<String, Object> data) {
        if (Objects.isNull(data) || data.isEmpty()) {
            return;
        }

        for (final Map.Entry<String, Object> entry : data.entrySet()) {
            final Object value = entry.getValue();
            if (value instanceof Map) {
                recursivelyUpdateVariablesInMap(variables, (Map) value);
            } else if (value instanceof Collection) {
                data.put(entry.getKey(), recursivelyUpdateVariablesInCollection(variables, (Collection) value));
            } else if (isToken(value)) {
                // Get the variable definition for this variable.
                final VariableDefinition variable = variables.get(value.toString());
                data.put(entry.getKey(), variable.getValue());
            }
        }
    }

    /**
     * This method walks through the provided {@code sourceCollection} and replaces any variables with the actual
     * values.
     *
     * @param variables
     *         Map containing the variable definitions.
     * @param sourceCollection
     *         Source collection and elements in this source collection can represent either tokens or regular values.
     *
     * @return New collection containing the data with variables being replaced with the actual values.
     */
    private Object recursivelyUpdateVariablesInCollection(final Map<String, VariableDefinition> variables,
                                                          final Collection<Object> sourceCollection) {
        if (Objects.isNull(sourceCollection) || sourceCollection.isEmpty()) {
            return Collections.emptySet();
        }

        final Collection<Object> newCollection = new LinkedHashSet<>(sourceCollection.size());
        for (final Object element : sourceCollection) {
            if (isToken(element)) {
                // Get the variable definition for this variable.
                final VariableDefinition variable = variables.get(element.toString());
                newCollection.add(variable.getValue());
            } else {
                newCollection.add(element);
                // Does this element represent a map?
                if (element instanceof Map) {
                    final Map<String, Object> inputDataMap = (Map) element;
                    recursivelyUpdateVariablesInMap(variables, inputDataMap);
                }
            }
        }

        return newCollection;
    }

    /**
     * This method checks if the provided value is a token i.e. it starts with a "$" symbol.
     *
     * @param value
     *         Value that needs to be checked.
     *
     * @return True if the value is a {@link String} and starts with "$" symbol, false otherwise.
     */
    private boolean isToken(final Object value) {
        return value instanceof String && value.toString().startsWith(SpecialCharacter.DOLLAR.getValue());
    }

    /**
     * This method attempts to find all the strategies defined in the variable value (a {@link String}) and replaces
     * them with actual generated values.
     * <p>
     * For example: If the value is "This is a dynamic string with id = $randomUUID and name = $randomAlphanumeric"
     *
     * @param value
     *         String value containing the different strategies that have to be replaced with the generated values
     *         (based on the corresponding strategy).
     *
     * @return Updated string where all the strategies are replaced with the generated values.
     */
    private String updateAllStrategiesInString(final String value) {
        if (StringUtils.isBlank(value)) {
            return value;
        }

        // Begin with the provided value
        String updatedValue = value;
        for (final DataGenerationStrategy strategy : DataGenerationStrategy.values()) {
            if (updatedValue.contains(strategy.getStrategy())) {
                updatedValue = updatedValue.replace(strategy.getStrategy(), strategy.generate().toString());
            }
        }
        return updatedValue;
    }
}
