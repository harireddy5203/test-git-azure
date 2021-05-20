/*
 * Copyright (c) 2021 REPLACE_CUSTOMER_NAME. All rights reserved.
 *
 * This file is part of test-git-azure.
 *
 * test-git-azure project and associated code cannot be copied
 * and/or distributed without a written permission of REPLACE_CUSTOMER_NAME,
 * and/or its subsidiaries.
 */
package com.git.azure.commons.properties;

import java.io.Serializable;
import java.util.Map;

/**
 * A contract that provides the functionality for maintaining additional properties (as key / value pairs) across any
 * object.
 *
 * @param <K>
 *         Data type of the key.
 * @param <V>
 *         Data type of the value.
 *
 * @author Mahalingam Iyer
 */
public interface IPropertiesProvider<K, V> extends Serializable {
    /**
     * This method sets the provided properties as the base. Any previous properties are cleared before using the
     * provided properties as the base.
     *
     * @param properties
     *         Map containing the properties as key-value pairs.
     *
     * @return Updated instance.
     */
    IPropertiesProvider<K, V> set(Map<K, V> properties);

    /**
     * This method attempts to add the provided key / value pair to the internal map.
     *
     * @param key
     *         Key that needs to be added.
     * @param value
     *         Value for the specified key.
     *
     * @return Updated instance.
     */
    IPropertiesProvider<K, V> add(K key, V value);

    /**
     * This method attempts to add all the key / value pairs in the provided map to the internal map. Note that the
     * existing properties are not cleared unlike the {@code set} method.
     *
     * @param properties
     *         Map containing the key / value pairs that needs to be added to the internal map.
     *
     * @return Updated instance.
     */
    IPropertiesProvider<K, V> addAll(Map<K, V> properties);

    /**
     * This method returns all the properties as a {@link Map} where the key represents the property name and the value
     * is the property value.
     *
     * @return Properties as a {@link Map} containing the key-value pairs.
     */
    Map<K, V> get();

    /**
     * For the provided key, this method returns the value.
     *
     * @param key
     *         Key for which the value needs to be retrieved.
     *
     * @return Value for the provided key.
     */
    V get(K key);

    /**
     * For the provided key, this method attempts to retrieve the value and casts the value to the specified target
     * type.
     *
     * @param key
     *         Key for which the value needs to be retrieved.
     * @param targetType
     *         Target type of the value.
     * @param <T>
     *         Target type of the value.
     *
     * @return Value for the specified key and casted to the specified type. Returns null if the value cannot be casted
     * to the specified type.
     */
    <T> T get(K key, Class<T> targetType);

    /**
     * For the provided key, this method attempts to retrieve the value and casts the value to a string before returning
     * it.
     *
     * @param key
     *         Key for which the value needs to be retrieved.
     *
     * @return Value for the specified key and casted to a string.
     */
    String getAsString(K key);

    /**
     * This method checks if the provided key is present in the properties captured by this instance and returns true /
     * false based on the presence / absence of the key.
     *
     * @param key
     *         Key whose presence needs to be checked.
     *
     * @return True if the key is present in the key / value pairs maintained by this instance, false otherwise.
     */
    boolean has(K key);
}
