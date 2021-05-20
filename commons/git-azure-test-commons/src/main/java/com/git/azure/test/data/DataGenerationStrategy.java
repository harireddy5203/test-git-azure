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

import java.text.MessageFormat;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Stream;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;

import lombok.AllArgsConstructor;
import lombok.Getter;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.git.azure.commons.exception.ServiceException;
import com.git.azure.test.error.TestErrors;

/**
 * Enumerated data type that lists different strategies for generating random data (integer, float, double, string,
 * etc.).
 *
 * @author Mahalingam Iyer
 */
@Getter
@AllArgsConstructor
public enum DataGenerationStrategy {
    // Constant for random double generation.
    @JsonProperty("$none")
    NONE("$none") {
        @Override
        public Object generate() {
            return null;
        }

        @Override
        public Object generate(final int startInclusive, final int endExclusive) {
            return generate();
        }
    },

    // Constant for random double generation.
    @JsonProperty("$randomDouble")
    RANDOM_DOUBLE("$randomDouble") {
        @Override
        public Object generate() {
            return RandomUtils.nextDouble();
        }

        @Override
        public Object generate(final int startInclusive, final int endExclusive) {
            return RandomUtils.nextDouble(startInclusive, endExclusive);
        }
    },

    // Constant for random float generation.
    @JsonProperty("$randomFloat")
    RANDOM_FLOAT("$randomFloat") {
        @Override
        public Object generate() {
            return RandomUtils.nextFloat();
        }

        @Override
        public Object generate(final int startInclusive, final int endExclusive) {
            return RandomUtils.nextFloat(startInclusive, endExclusive);
        }
    },

    // Constant for random long  generation.
    @JsonProperty("$randomLong")
    RANDOM_LONG("$randomLong") {
        @Override
        public Object generate() {
            return RandomUtils.nextLong();
        }

        @Override
        public Object generate(final int startInclusive, final int endExclusive) {
            return RandomUtils.nextLong(startInclusive, endExclusive);
        }
    },

    // Constant for random integer generation.
    @JsonProperty("$randomInteger")
    RANDOM_INTEGER("$randomInteger") {
        @Override
        public Object generate() {
            return RandomUtils.nextInt();
        }

        @Override
        public Object generate(final int startInclusive, final int endExclusive) {
            return RandomUtils.nextInt(startInclusive, endExclusive);
        }
    },

    // Constant for random string generation.
    @JsonProperty("$randomString")
    RANDOM_STRING("$randomString") {
        @Override
        public Object generate() {
            return RandomStringUtils.random(16);
        }

        @Override
        public Object generate(final int startInclusive, final int endExclusive) {
            return RandomStringUtils.random(endExclusive);
        }
    },

    // Constant for random string (alphanumeric) generation.
    @JsonProperty("$randomAlphanumeric")
    RANDOM_STRING_ALPHANUMERIC("$randomAlphanumeric") {
        @Override
        public Object generate() {
            return RandomStringUtils.randomAlphanumeric(16);
        }

        @Override
        public Object generate(final int startInclusive, final int endExclusive) {
            return RandomStringUtils.randomAlphanumeric(startInclusive, endExclusive);
        }
    },

    // Constant for random string (alphabetic) generation.
    @JsonProperty("$randomAlphabetic")
    RANDOM_STRING_ALPHABETIC("$randomAlphabetic") {
        @Override
        public Object generate() {
            return RandomStringUtils.randomAlphabetic(16);
        }

        @Override
        public Object generate(final int startInclusive, final int endExclusive) {
            return RandomStringUtils.randomAlphabetic(startInclusive, endExclusive);
        }
    },

    // Constant for random email string (alphabetic) generation.
    @JsonProperty("$randomEmail")
    RANDOM_EMAIL("$randomEmail") {
        @Override
        public Object generate() {
            return generate(12, 16);
        }

        @Override
        public Object generate(final int startInclusive, final int endExclusive) {
            return MessageFormat.format("{0}@yopmail.com", RandomStringUtils.randomAlphabetic(startInclusive, endExclusive));
        }
    },

    // Constant for random string (numeric) generation.
    @JsonProperty("$randomNumeric")
    RANDOM_STRING_NUMERIC("$randomNumeric") {
        @Override
        public Object generate() {
            return RandomStringUtils.randomNumeric(16);
        }

        @Override
        public Object generate(final int startInclusive, final int endExclusive) {
            return RandomStringUtils.randomNumeric(startInclusive, endExclusive);
        }
    },

    // Constant for random UUID generation.
    @JsonProperty("$randomUUID")
    RANDOM_UUID("$randomUUID") {
        @Override
        public Object generate() {
            return UUID.randomUUID().toString();
        }

        @Override
        public Object generate(final int startInclusive, final int endExclusive) {
            return generate();
        }
    };

    /** Random data generation strategy for this constant. */
    private final String strategy;

    /**
     * This method is responsible to generate a random value (based on the type).
     *
     * @return Random generated value based on the enum type.
     */
    public abstract Object generate();

    /**
     * This method is responsible to generate a random value (based on the type) and the value will be between {@code
     * startInclusive} and {@code endExclusive}.
     * <p>
     * For strings, these parameters represent the length of the string.
     *
     * @param startInclusive
     *         Start value (inclusive). For strings, this indicates the minimum length.
     * @param endExclusive
     *         End value (exclusive). For strings, this indicates the maximum length.
     *
     * @return Random generated value based on the provided parameters.
     */
    public abstract Object generate(int startInclusive, int endExclusive);

    /**
     * This method checks if the current instance matches any of the provided {@code strategies}.
     *
     * @param strategies
     *         Array of strategies to compare against.
     *
     * @return True if the current instances matches any of the provided {@code strategies}.
     */
    public boolean is(final DataGenerationStrategy... strategies) {
        if (Objects.isNull(strategies) || strategies.length == 0) {
            return false;
        }

        boolean verdict = false;
        for (final DataGenerationStrategy dataGenerationStrategy : strategies) {
            if (this.equals(dataGenerationStrategy)) {
                verdict = true;
                break;
            }
        }
        return verdict;
    }

    /**
     * This method checks if the current instance's generation strategy applicable for {@link String} types.
     *
     * @return True if the current instance's generation strategy application for {@link String} types.
     */
    public boolean applicableForStringType() {
        return is(DataGenerationStrategy.RANDOM_STRING,
                  DataGenerationStrategy.RANDOM_STRING_ALPHABETIC,
                  DataGenerationStrategy.RANDOM_STRING_ALPHANUMERIC,
                  DataGenerationStrategy.RANDOM_EMAIL,
                  DataGenerationStrategy.RANDOM_STRING_NUMERIC);
    }

    /**
     * This method checks if the current instance's generation strategy applicable for {@link Number} types.
     *
     * @return True if the current instance's generation strategy application for {@link Number} types.
     */
    public boolean applicableForNumericType() {
        return is(DataGenerationStrategy.RANDOM_FLOAT,
                  DataGenerationStrategy.RANDOM_DOUBLE,
                  DataGenerationStrategy.RANDOM_INTEGER);
    }

    /**
     * This method attempts to transform the provided strategy into an enum constant of type {@link
     * DataGenerationStrategy}.
     *
     * @param strategy
     *         Random data generation strategy (e.x: $randomString, $randomFloat, etc.).
     *
     * @return Instance of type {@link DataGenerationStrategy} for the specified strategy.
     */
    public static DataGenerationStrategy forStrategy(final String strategy) {
        return Stream.of(values())
                .filter(dataGenerationStrategy -> dataGenerationStrategy.getStrategy().equals(strategy))
                .findFirst()
                .orElseThrow(() -> ServiceException.instance(TestErrors.RANDOM_GENERATION_STRATEGY_NOT_FOUND, strategy));
    }
}
