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
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import com.git.azure.commons.error.CommonErrors;
import com.git.azure.commons.exception.ServiceException;
import com.git.azure.commons.utils.Strings;
import com.git.azure.test.error.TestErrors;

/**
 * An implementation of an experience model that captures the test data for a single test case and can be applied to
 * "unit" and "integration" tests.
 *
 * @author Mahalingam Iyer
 */
@JsonDeserialize(converter = TestCaseDataPostProcessor.class)
@EqualsAndHashCode(of = {"testName"})
@ToString(of = {"testName"})
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public class TestCaseData {
    /**
     * Enumerated data type that indicates the type of test case data.
     */
    public enum Type {
        INPUT,
        MOCK_INPUT,
        MOCK_OUTPUT
    }

    /** Name of the test. Generally maps to the test method name in the unit / integration test implementation. */
    private String testName;

    /** Map of variable definitions that can be used in this test case data definition. */
    @Builder.Default
    private Map<String, VariableDefinition> variables = new HashMap<>();

    /** Input for this test case. */
    private TestData input;

    /** Mock data capturing input and output data if any downstream layers are being mocked. */
    private MockData mock;

    /**
     * On the {@code input} field of this object, this method attempts to formulate an input name based on the provided
     * {@code targetType}, retrieves the value for it and casts the value to the requested type.
     * <p>
     * The input name is generated by using {@code targetType.getClass().getSimpleName()} and converting to camel-case.
     * For example: if the {@code targetType} is specified as {@code CreatePlatform.class}, this method formulates the
     * input name as {@code createPlatform}.
     * <p>
     * If the casting is not possible, this method returns an empty {@link Optional}.
     *
     * @param targetType
     *         Target type of the input data.
     * @param <T>
     *         Target type.
     *
     * @return An {@link Optional} instance containing the value casted to the requested type if the casting was
     * successful, else an empty {@link Optional} is returned.
     */
    public <T> Optional<T> getInput(final Class<T> targetType) {
        return getInput(Strings.uncapitalizedTypeName(targetType), targetType);
    }

    /**
     * On the {@code input} field of this object, this method attempts to retrieve the value for the specified {@code
     * inputName} and casts to an instance of type {@code targetType}.
     * <p>
     * If the casting is not possible, this method returns an empty {@link Optional}.
     *
     * @param inputName
     *         Name of the input as defined in the JSON file (e.g. createPlatform, createFramework, etc.)
     * @param targetType
     *         Target type of the return value.
     * @param <T>
     *         Target type for the {@code input.value} field.
     *
     * @return An {@link Optional} instance containing the instance of the appropriate type (if the casting was
     * successful), else returns an empty {@link Optional}.
     */
    public <T> Optional<T> getInput(final String inputName, final Class<T> targetType) {
        return get(Type.INPUT, inputName, targetType);
    }

    /**
     * On the {@code input} field of this object, this method attempts to formulate an input name based on the provided
     * {@code targetType}, retrieves the value for it and casts the value to the requested type.
     * <p>
     * The input name is generated by using {@code targetType.getClass().getSimpleName()} and converting to camel-case.
     * For example: if the {@code targetType} is specified as {@code CreatePlatform.class}, this method formulates the
     * input name as {@code createPlatform}.
     * <p>
     * If the casting is not possible, this method throws an exception.
     *
     * @param targetType
     *         Target type of the input data.
     * @param <T>
     *         Target type.
     *
     * @return Return value that is casted to the specified {@code targetType} if the casting is possible else an
     * exception is thrown.
     */
    public <T> T getInputOrThrow(final Class<T> targetType) {
        return getInputOrThrow(Strings.uncapitalizedTypeName(targetType), targetType);
    }

    /**
     * On the {@code input} field of this object, this method attempts to retrieve the value for the specified {@code
     * inputName} and casts to an instance of type {@code targetType}.
     * <p>
     * If the casting is not possible, an exception is thrown.
     *
     * @param inputName
     *         Name of the input as defined in the JSON file (e.g. createPlatform, createFramework, etc.)
     * @param targetType
     *         Target type of the return value.
     * @param <T>
     *         Target type for the {@code input.value} field.
     *
     * @return Return value that is casted to the specified {@code targetType} if the casting is possible else an
     * exception is thrown.
     */
    public <T> T getInputOrThrow(final String inputName, final Class<T> targetType) {
        return getOrThrow(Type.INPUT, inputName, targetType);
    }

    /**
     * On the {@code mock.input} field of this object, this method attempts to formulate an input name based on the
     * provided {@code targetType}, retrieves the value for it and casts the value to the requested type.
     * <p>
     * The input name is generated by using {@code targetType.getClass().getSimpleName()} and converting to camel-case.
     * For example: if the {@code targetType} is specified as {@code CreatePlatform.class}, this method formulates the
     * input name as {@code createPlatform}.
     * <p>
     * If the casting is not possible, this method returns an empty {@link Optional}.
     *
     * @param targetType
     *         Target type of the input data.
     * @param <T>
     *         Target type.
     *
     * @return An {@link Optional} instance containing the value casted to the requested type if the casting was
     * successful, else an empty {@link Optional} is returned.
     */
    public <T> Optional<T> getMockInput(final Class<T> targetType) {
        return getMockInput(Strings.uncapitalizedTypeName(targetType), targetType);
    }

    /**
     * On the {@code mock.input} field of this object, this method attempts to retrieve the value for the specified
     * {@code inputName} and casts to an instance of type {@code targetType}.
     * <p>
     * If the casting is not possible, this method returns an empty {@link Optional}.
     *
     * @param inputName
     *         Name of the input as defined in the JSON file (e.g. createPlatform, createFramework, etc.)
     * @param targetType
     *         Target type of the return value.
     * @param <T>
     *         Target type for the {@code input.value} field.
     *
     * @return An {@link Optional} instance containing the instance of the appropriate type (if the casting was
     * successful), else returns an empty {@link Optional}.
     */
    public <T> Optional<T> getMockInput(final String inputName, final Class<T> targetType) {
        return get(Type.MOCK_INPUT, inputName, targetType);
    }

    /**
     * On the {@code mock.input} field of this object, this method attempts to formulate an input name based on the
     * provided {@code targetType}, retrieves the value for it and casts the value to the requested type.
     * <p>
     * The input name is generated by using {@code targetType.getClass().getSimpleName()} and converting to camel-case.
     * For example: if the {@code targetType} is specified as {@code CreatePlatform.class}, this method formulates the
     * input name as {@code createPlatform}.
     * <p>
     * If the casting is not possible, this method throws an exception.
     *
     * @param targetType
     *         Target type of the input data.
     * @param <T>
     *         Target type.
     *
     * @return Return value that is casted to the specified {@code targetType} if the casting is possible else an
     * exception is thrown.
     */
    public <T> T getMockInputOrThrow(final Class<T> targetType) {
        return getMockInputOrThrow(Strings.uncapitalizedTypeName(targetType), targetType);
    }

    /**
     * On the {@code mock.input} field of this object, this method attempts to retrieve the value for the specified
     * {@code inputName} and casts to an instance of type {@code targetType}.
     * <p>
     * If the casting is not possible, an exception is thrown.
     *
     * @param inputName
     *         Name of the input as defined in the JSON file (e.g. createPlatform, createFramework, etc.)
     * @param targetType
     *         Target type of the return value.
     * @param <T>
     *         Target type for the {@code input.value} field.
     *
     * @return Return value that is casted to the specified {@code targetType} if the casting is possible else an
     * exception is thrown.
     */
    public <T> T getMockInputOrThrow(final String inputName, final Class<T> targetType) {
        return getOrThrow(Type.MOCK_INPUT, inputName, targetType);
    }

    /**
     * On the {@code mock.output} field of this object, this method attempts to formulate an input name based on the
     * provided {@code targetType}, retrieves the value for it and casts the value to the requested type.
     * <p>
     * The input name is generated by using {@code targetType.getClass().getSimpleName()} and converting to camel-case.
     * For example: if the {@code targetType} is specified as {@code CreatePlatform.class}, this method formulates the
     * input name as {@code createPlatform}.
     * <p>
     * If the casting is not possible, this method returns an empty {@link Optional}.
     *
     * @param targetType
     *         Target type of the input data.
     * @param <T>
     *         Target type.
     *
     * @return An {@link Optional} instance containing the value casted to the requested type if the casting was
     * successful, else an empty {@link Optional} is returned.
     */
    public <T> Optional<T> getMockOutput(final Class<T> targetType) {
        return getMockOutput(Strings.uncapitalizedTypeName(targetType), targetType);
    }

    /**
     * On the {@code mock.output} field of this object, this method attempts to retrieve the value for the specified
     * {@code inputName} and casts to an instance of type {@code targetType}.
     * <p>
     * If the casting is not possible, this method returns an empty {@link Optional}.
     *
     * @param inputName
     *         Name of the input as defined in the JSON file (e.g. createPlatform, createFramework, etc.)
     * @param targetType
     *         Target type of the return value.
     * @param <T>
     *         Target type for the {@code input.value} field.
     *
     * @return An {@link Optional} instance containing the instance of the appropriate type (if the casting was
     * successful), else returns an empty {@link Optional}.
     */
    public <T> Optional<T> getMockOutput(final String inputName, final Class<T> targetType) {
        return get(Type.MOCK_OUTPUT, inputName, targetType);
    }

    /**
     * On the {@code mock.output} field of this object, this method attempts to formulate an input name based on the
     * provided {@code targetType}, retrieves the value for it and casts the value to the requested type.
     * <p>
     * The input name is generated by using {@code targetType.getClass().getSimpleName()} and converting to camel-case.
     * For example: if the {@code targetType} is specified as {@code CreatePlatform.class}, this method formulates the
     * input name as {@code createPlatform}.
     * <p>
     * If the casting is not possible, this method throws an exception.
     *
     * @param targetType
     *         Target type of the input data.
     * @param <T>
     *         Target type.
     *
     * @return Return value that is casted to the specified {@code targetType} if the casting is possible else an
     * exception is thrown.
     */
    public <T> T getMockOutputOrThrow(final Class<T> targetType) {
        return getMockOutputOrThrow(Strings.uncapitalizedTypeName(targetType), targetType);
    }

    /**
     * On the {@code mock.output} field of this object, this method attempts to retrieve the value for the specified
     * {@code inputName} and casts to an instance of type {@code targetType}.
     * <p>
     * If the casting is not possible, an exception is thrown.
     *
     * @param inputName
     *         Name of the input as defined in the JSON file (e.g. createPlatform, createFramework, etc.)
     * @param targetType
     *         Target type of the return value.
     * @param <T>
     *         Target type for the {@code input.value} field.
     *
     * @return Return value that is casted to the specified {@code targetType} if the casting is possible else an
     * exception is thrown.
     */
    public <T> T getMockOutputOrThrow(final String inputName, final Class<T> targetType) {
        return getOrThrow(Type.MOCK_OUTPUT, inputName, targetType);
    }

    /**
     * On the {@code input} field of this object, this method attempts to formulate an input name based on the provided
     * {@code targetType}, retrieves the value for it, attempts to convert to a collection where each element in the
     * collection is casted to the requested type.
     * <p>
     * The input name is generated by using {@code targetType.getClass().getSimpleName()} and converting to camel-case.
     * For example: if the {@code targetType} is specified as {@code CreatePlatform.class}, this method formulates the
     * input name as {@code createPlatform}.
     * <p>
     * If any of the elements cannot be casted, they will be excluded in the returned collection.
     *
     * @param targetType
     *         Target type of the elements in the returned collection.
     * @param <T>
     *         Target type for the elements in the returned collection.
     *
     * @return Collection of elements where each element in the returned collection is an instance of type {@code
     * targetType}.
     */
    public <T> Collection<T> getMultipleInputs(final Class<T> targetType) {
        return getMultipleInputs(Strings.uncapitalizedTypeName(targetType), targetType);
    }

    /**
     * On the {@code input} field of this object, this method attempts to retrieve the value for the specified {@code
     * inputName}, attempts to convert to a collection where each element in the collection is casted to the requested
     * type.
     * <p>
     * If any of the elements cannot be casted, they will be excluded in the returned collection.
     *
     * @param inputName
     *         Name of the input as defined in the JSON file (e.g. createPlatform, createFramework, etc.)
     * @param targetType
     *         Target type of the elements in the returned collection.
     * @param <T>
     *         Target type for the elements in the returned collection.
     *
     * @return Collection of elements where each element in the returned collection is an instance of type {@code
     * targetType}.
     */
    public <T> Collection<T> getMultipleInputs(final String inputName, final Class<T> targetType) {
        return getMultiple(Type.INPUT, inputName, targetType);
    }

    /**
     * On the {@code input} field of this object, this method attempts to formulate an input name based on the provided
     * {@code targetType}, retrieves the value for it, attempts to convert to a collection where each element in the
     * collection is casted to the requested type.
     * <p>
     * The input name is generated by using {@code targetType.getClass().getSimpleName()} and converting to camel-case.
     * For example: if the {@code targetType} is specified as {@code CreatePlatform.class}, this method formulates the
     * input name as {@code createPlatform}.
     * <p>
     * If any of the elements cannot be casted, an exception is thrown.
     *
     * @param targetType
     *         Target type of the elements in the returned collection.
     * @param <T>
     *         Target type for the elements in the returned collection.
     *
     * @return Collection of elements where each element in the returned collection is an instance of type {@code
     * targetType}.
     */
    public <T> Collection<T> getMultipleInputsOrThrow(final Class<T> targetType) {
        return getMultipleInputsOrThrow(Strings.uncapitalizedTypeName(targetType), targetType);
    }

    /**
     * On the {@code input} field of this object, this method attempts to retrieve the value for the specified {@code
     * inputName}, attempts to convert to a collection where each element in the collection is casted to the requested
     * type.
     * <p>
     * If any of the elements cannot be casted, an exception is thrown.
     *
     * @param inputName
     *         Name of the input as defined in the JSON file (e.g. createPlatform, createFramework, etc.)
     * @param targetType
     *         Target type of the elements in the returned collection.
     * @param <T>
     *         Target type for the elements in the returned collection.
     *
     * @return Collection of elements where each element in the returned collection is an instance of type {@code
     * targetType}.
     */
    public <T> Collection<T> getMultipleInputsOrThrow(final String inputName, final Class<T> targetType) {
        return getMultipleOrThrow(Type.INPUT, inputName, targetType);
    }

    /**
     * On the {@code mock.input} field of this object, this method attempts to formulate an input name based on the
     * provided {@code targetType}, retrieves the value for it, attempts to convert to a collection where each element
     * in the collection is casted to the requested type.
     * <p>
     * The input name is generated by using {@code targetType.getClass().getSimpleName()} and converting to camel-case.
     * For example: if the {@code targetType} is specified as {@code CreatePlatform.class}, this method formulates the
     * input name as {@code createPlatform}.
     * <p>
     * If any of the elements cannot be casted, they will be excluded in the returned collection.
     *
     * @param targetType
     *         Target type of the elements in the returned collection.
     * @param <T>
     *         Target type for the elements in the returned collection.
     *
     * @return Collection of elements where each element in the returned collection is an instance of type {@code
     * targetType}.
     */
    public <T> Collection<T> getMultipleMockInputs(final Class<T> targetType) {
        return getMultipleMockInputs(Strings.uncapitalizedTypeName(targetType), targetType);
    }

    /**
     * On the {@code mock.input} field of this object, this method attempts to retrieve the value for the specified
     * {@code inputName}, attempts to convert to a collection where each element in the collection is casted to the
     * requested type.
     * <p>
     * If any of the elements cannot be casted, they will be excluded in the returned collection.
     *
     * @param inputName
     *         Name of the input as defined in the JSON file (e.g. createPlatform, createFramework, etc.)
     * @param targetType
     *         Target type of the elements in the returned collection.
     * @param <T>
     *         Target type for the elements in the returned collection.
     *
     * @return Collection of elements where each element in the returned collection is an instance of type {@code
     * targetType}.
     */
    public <T> Collection<T> getMultipleMockInputs(final String inputName, final Class<T> targetType) {
        return getMultiple(Type.MOCK_INPUT, inputName, targetType);
    }

    /**
     * On the {@code mock.input} field of this object, this method attempts to formulate an input name based on the
     * provided {@code targetType}, retrieves the value for it, attempts to convert to a collection where each element
     * in the collection is casted to the requested type.
     * <p>
     * The input name is generated by using {@code targetType.getClass().getSimpleName()} and converting to camel-case.
     * For example: if the {@code targetType} is specified as {@code CreatePlatform.class}, this method formulates the
     * input name as {@code createPlatform}.
     * <p>
     * If any of the elements cannot be casted, an exception is thrown.
     *
     * @param targetType
     *         Target type of the elements in the returned collection.
     * @param <T>
     *         Target type for the elements in the returned collection.
     *
     * @return Collection of elements where each element in the returned collection is an instance of type {@code
     * targetType}.
     */
    public <T> Collection<T> getMultipleMockInputsOrThrow(final Class<T> targetType) {
        return getMultipleMockInputsOrThrow(Strings.uncapitalizedTypeName(targetType), targetType);
    }

    /**
     * On the {@code mock.input} field of this object, this method attempts to retrieve the value for the specified
     * {@code inputName}, attempts to convert to a collection where each element in the collection is casted to the
     * requested type.
     * <p>
     * If any of the elements cannot be casted, an exception is thrown.
     *
     * @param inputName
     *         Name of the input as defined in the JSON file (e.g. createPlatform, createFramework, etc.)
     * @param targetType
     *         Target type of the elements in the returned collection.
     * @param <T>
     *         Target type for the elements in the returned collection.
     *
     * @return Collection of elements where each element in the returned collection is an instance of type {@code
     * targetType}.
     */
    public <T> Collection<T> getMultipleMockInputsOrThrow(final String inputName, final Class<T> targetType) {
        return getMultipleOrThrow(Type.MOCK_INPUT, inputName, targetType);
    }

    /**
     * On the {@code mock.output} field of this object, this method attempts to formulate an input name based on the
     * provided {@code targetType}, retrieves the value for it, attempts to convert to a collection where each element
     * in the collection is casted to the requested type.
     * <p>
     * The input name is generated by using {@code targetType.getClass().getSimpleName()} and converting to camel-case.
     * For example: if the {@code targetType} is specified as {@code CreatePlatform.class}, this method formulates the
     * input name as {@code createPlatform}.
     * <p>
     * If any of the elements cannot be casted, they will be excluded in the returned collection.
     *
     * @param targetType
     *         Target type of the elements in the returned collection.
     * @param <T>
     *         Target type for the elements in the returned collection.
     *
     * @return Collection of elements where each element in the returned collection is an instance of type {@code
     * targetType}.
     */
    public <T> Collection<T> getMultipleMockOutputs(final Class<T> targetType) {
        return getMultipleMockOutputs(Strings.uncapitalizedTypeName(targetType), targetType);
    }

    /**
     * On the {@code mock.output} field of this object, this method attempts to retrieve the value for the specified
     * {@code inputName}, attempts to convert to a collection where each element in the collection is casted to the
     * requested type.
     * <p>
     * If any of the elements cannot be casted, they will be excluded in the returned collection.
     *
     * @param inputName
     *         Name of the input as defined in the JSON file (e.g. createPlatform, createFramework, etc.)
     * @param targetType
     *         Target type of the elements in the returned collection.
     * @param <T>
     *         Target type for the elements in the returned collection.
     *
     * @return Collection of elements where each element in the returned collection is an instance of type {@code
     * targetType}.
     */
    public <T> Collection<T> getMultipleMockOutputs(final String inputName, final Class<T> targetType) {
        return getMultiple(Type.MOCK_OUTPUT, inputName, targetType);
    }

    /**
     * On the {@code mock.output} field of this object, this method attempts to formulate an input name based on the
     * provided {@code targetType}, retrieves the value for it, attempts to convert to a collection where each element
     * in the collection is casted to the requested type.
     * <p>
     * The input name is generated by using {@code targetType.getClass().getSimpleName()} and converting to camel-case.
     * For example: if the {@code targetType} is specified as {@code CreatePlatform.class}, this method formulates the
     * input name as {@code createPlatform}.
     * <p>
     * If any of the elements cannot be casted, an exception is thrown.
     *
     * @param targetType
     *         Target type of the elements in the returned collection.
     * @param <T>
     *         Target type for the elements in the returned collection.
     *
     * @return Collection of elements where each element in the returned collection is an instance of type {@code
     * targetType}.
     */
    public <T> Collection<T> getMultipleMocKOutputsOrThrow(final Class<T> targetType) {
        return getMultipleMocKOutputsOrThrow(Strings.uncapitalizedTypeName(targetType), targetType);
    }

    /**
     * On the {@code mock.output} field of this object, this method attempts to retrieve the value for the specified
     * {@code inputName}, attempts to convert to a collection where each element in the collection is casted to the
     * requested type.
     * <p>
     * If any of the elements cannot be casted, an exception is thrown.
     *
     * @param inputName
     *         Name of the input as defined in the JSON file (e.g. createPlatform, createFramework, etc.)
     * @param targetType
     *         Target type of the elements in the returned collection.
     * @param <T>
     *         Target type for the elements in the returned collection.
     *
     * @return Collection of elements where each element in the returned collection is an instance of type {@code
     * targetType}.
     */
    public <T> Collection<T> getMultipleMocKOutputsOrThrow(final String inputName, final Class<T> targetType) {
        return getMultipleOrThrow(Type.MOCK_OUTPUT, inputName, targetType);
    }

    /**
     * Based on the provided {@code type}, this method uses {@code input} or {@code mock.input} or {@code mock.output}
     * fields, retrieves the value for the specified {@code inputName} and casts to the requested {@code targetType}.
     * <p>
     * If the casting is not possible, this method returns an empty {@link Optional}.
     *
     * @param type
     *         Enumerated data type that will decide how to navigate to the input value field i.e. {@code input.value}
     *         or {@code mockData.input.value} or {@code mockData.output.value} fields.
     * @param inputName
     *         Name of the input as defined in the JSON file (e.g. createPlatform, createFramework, etc.)
     * @param targetType
     *         Target type of the return value.
     * @param <T>
     *         Target type for the {@code input.value} or {@code mockData.input.value} or {@code mockData.output.value}
     *         fields based on the {@code type} parameter.
     *
     * @return An {@link Optional} instance containing the instance of the appropriate type (if the casting was
     * successful), else returns an empty {@link Optional}.
     */
    public <T> Optional<T> get(final Type type, final String inputName, final Class<T> targetType) {
        if (Objects.isNull(type) || Objects.isNull(targetType)) {
            return Optional.empty();
        }

        Optional<T> response = Optional.empty();
        switch (type) {
            case INPUT:
                if (Objects.nonNull(input)) {
                    response = input.get(inputName, targetType);
                }
                break;
            case MOCK_INPUT:
                if (Objects.nonNull(mock) && Objects.nonNull(mock.getInput())) {
                    response = mock.getInput().get(inputName, targetType);
                }
                break;
            case MOCK_OUTPUT:
                if (Objects.nonNull(mock) && Objects.nonNull(mock.getOutput())) {
                    response = mock.getOutput().get(inputName, targetType);
                }
                break;
            default:
                throw ServiceException.instance(TestErrors.UNSUPPORTED_TYPE, type.name());
        }

        return response;
    }

    /**
     * Based on the provided {@code type}, this method uses {@code input} or {@code mock.input} or {@code mock.output}
     * fields, retrieves the value for the specified {@code inputName} and casts to the requested {@code targetType}.
     * <p>
     * If the casting is not possible, an exception is thrown.
     *
     * @param type
     *         Enumerated data type that will decide how to navigate to the input value field i.e. {@code input.value}
     *         or {@code mockData.input.value} or {@code mockData.output.value} fields.
     * @param inputName
     *         Name of the input as defined in the JSON file (e.g. createPlatform, createFramework, etc.)
     * @param targetType
     *         Target type of the return value.
     * @param <T>
     *         Target type for the {@code input.value} or {@code mockData.input.value} or {@code mockData.output.value}
     *         fields based on the {@code type} parameter.
     *
     * @return Return value that is casted to the specified {@code targetType} if the casting is possible else an
     * exception is thrown.
     */
    public <T> T getOrThrow(final Type type, final String inputName, final Class<T> targetType) {
        if (Objects.isNull(type) || Objects.isNull(targetType)) {
            throw ServiceException.instance(CommonErrors.ILLEGAL_ARGUMENT);
        }

        T response = null;
        switch (type) {
            case INPUT:
                if (Objects.nonNull(input)) {
                    response = input.getOrThrow(inputName, targetType);
                }
                break;
            case MOCK_INPUT:
                if (Objects.nonNull(mock) && Objects.nonNull(mock.getInput())) {
                    response = mock.getInput().getOrThrow(inputName, targetType);
                }
                break;
            case MOCK_OUTPUT:
                if (Objects.nonNull(mock) && Objects.nonNull(mock.getOutput())) {
                    response = mock.getOutput().getOrThrow(inputName, targetType);
                }
                break;
            default:
                throw ServiceException.instance(TestErrors.UNSUPPORTED_TYPE, type.name());
        }

        return response;
    }

    /**
     * Based on the provided {@code type}, this method uses {@code input} or {@code mock.input} or {@code mock.output}
     * fields, retrieves the value for the specified {@code inputName}, converts to a collection where each element is
     * casted to the requested {@code targetType}.
     *
     * @param type
     *         Enumerated data type that will decide how to navigate to the input value field i.e. {@code input.values}
     *         or {@code mockData.input.values} or {@code mockData.output.values} fields.
     * @param inputName
     *         Name of the input as defined in the JSON file (e.g. createPlatform, createFramework, etc.)
     * @param targetType
     *         Target type of the elements in the returned collection.
     * @param <T>
     *         Target type for the {@code input.values} or {@code mockData.input.values} or {@code
     *         mockData.output.values} fields based on the {@code type} parameter.
     *
     * @return Collection of elements where each element in the returned collection is an instance of type {@code
     * targetType}.
     */
    public <T> Collection<T> getMultiple(final Type type, final String inputName, final Class<T> targetType) {
        if (Objects.isNull(type) || Objects.isNull(targetType)) {
            return Collections.emptyList();
        }

        Collection<T> response = Collections.emptyList();
        switch (type) {
            case INPUT:
                if (Objects.nonNull(input)) {
                    response = input.getMultiple(inputName, targetType);
                }
                break;
            case MOCK_INPUT:
                if (Objects.nonNull(mock) && Objects.nonNull(mock.getInput())) {
                    response = mock.getInput().getMultiple(inputName, targetType);
                }
                break;
            case MOCK_OUTPUT:
                if (Objects.nonNull(mock) && Objects.nonNull(mock.getOutput())) {
                    response = mock.getOutput().getMultiple(inputName, targetType);
                }
                break;
            default:
                throw ServiceException.instance(TestErrors.UNSUPPORTED_TYPE, type.name());
        }

        return response;
    }

    /**
     * Based on the provided {@code type}, this method uses {@code input} or {@code mock.input} or {@code mock.output}
     * fields, retrieves the value for the specified {@code inputName}, converts to a collection where each element is
     * casted to the requested {@code targetType}.
     * <p>
     * If any of the elements in the {@code values} collection cannot be casted to the {@code targetType}, an exception
     * is thrown.
     *
     * @param type
     *         Enumerated data type that will decide how to navigate to the input value field i.e. {@code input.values}
     *         or {@code mockData.input.values} or {@code mockData.output.values} fields.
     * @param inputName
     *         Name of the input as defined in the JSON file (e.g. createPlatform, createFramework, etc.)
     * @param targetType
     *         Target type of the elements in the returned collection.
     * @param <T>
     *         Target type for the {@code input.values} or {@code mockData.input.values} or {@code
     *         mockData.output.values} fields based on the {@code type} parameter.
     *
     * @return Collection of elements where each element in the returned collection is an instance of type {@code
     * targetType}.
     */
    public <T> Collection<T> getMultipleOrThrow(final Type type, final String inputName, final Class<T> targetType) {
        if (Objects.isNull(type) || Objects.isNull(targetType)) {
            return Collections.emptyList();
        }

        Collection<T> response = Collections.emptyList();
        switch (type) {
            case INPUT:
                if (Objects.nonNull(input)) {
                    response = input.getMultipleOrThrow(inputName, targetType);
                }
                break;
            case MOCK_INPUT:
                if (Objects.nonNull(mock) && Objects.nonNull(mock.getInput())) {
                    response = mock.getInput().getMultipleOrThrow(inputName, targetType);
                }
                break;
            case MOCK_OUTPUT:
                if (Objects.nonNull(mock) && Objects.nonNull(mock.getOutput())) {
                    response = mock.getOutput().getMultipleOrThrow(inputName, targetType);
                }
                break;
            default:
                throw ServiceException.instance(TestErrors.UNSUPPORTED_TYPE, type.name());
        }

        return response;
    }
}
