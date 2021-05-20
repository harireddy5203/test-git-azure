/*
 * Copyright (c) 2021 REPLACE_CUSTOMER_NAME. All rights reserved.
 *
 * This file is part of test-git-azure.
 *
 * test-git-azure project and associated code cannot be copied
 * and/or distributed without a written permission of REPLACE_CUSTOMER_NAME,
 * and/or its subsidiaries.
 */
package com.git.azure.commons;

import java.util.Objects;

/**
 * Base contract that can be used on the enumerated data types that can return a value for an enum constant.
 *
 * @author Mahalingam Iyer
 */
public interface IEnumValueProvider<T extends Enum<T>> {
    /**
     * Returns the name (i.e. constant) for the enum constant.
     *
     * @return Name of the enum constant.g
     */
    String name();

    /**
     * Returns the value for the enum constant.
     *
     * @return Value for the enum constant.
     */
    String getValue();

    /**
     * This method checks if this enum instance matches any of the provided enum types.
     *
     * @param enumTypes
     *         Array of enum types to compare.
     *
     * @return True if the type of this enum instance is the same as one of the provided types.
     */
    default boolean isAny(final T[] enumTypes) {
        boolean verdict = false;
        if (Objects.nonNull(enumTypes) && enumTypes.length > 0) {
            for (final T enumType : enumTypes) {
                if (this.equals(enumType)) {
                    verdict = true;
                    break;
                }
            }
        }
        return verdict;
    }

    /**
     * This method checks if this enum instance matches the provided enum type.
     *
     * @param enumType
     *         Enum to compare.
     *
     * @return True if this enum instance is the same as the provided enum type.
     */
    default boolean is(final T enumType) {
        return Objects.nonNull(enumType) && this.equals(enumType);
    }
}
