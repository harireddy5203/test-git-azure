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

import lombok.extern.slf4j.Slf4j;

/**
 * Utility class that provides functionality to adapt objects from one type to another.
 *
 * @author Mahalingam Iyer
 */
@Slf4j
public final class Adapter {
    /**
     * Private constructor.
     */
    private Adapter() {
        // Throw illegal if anyone creates
        throw new IllegalStateException("Cannot create instances of this class");
    }

    /**
     * This method attempts to adapt the provided object (via {@code inputObject} parameter) to the provided target type
     * (via {@code targetType} parameter). If the object can be adapted, it gets casted to the target type and returns
     * it else returns a null.
     *
     * @param inputObject
     *         Input object that needs to be adapted to the target type
     * @param targetType
     *         Target type
     * @param <T>
     *         Target type
     *
     * @return Input object adapted to the target type. Returns null if the input object cannot be adapted to the target
     * type
     */
    public static <T> T adaptTo(final Object inputObject, final Class<T> targetType) {
        if (inputObject != null && targetType.isAssignableFrom(inputObject.getClass())) {
            return targetType.cast(inputObject);
        }

        Adapter.LOGGER.warn("Unable to adapt the provided input to target type {}", targetType.getName());
        return null;
    }

}
