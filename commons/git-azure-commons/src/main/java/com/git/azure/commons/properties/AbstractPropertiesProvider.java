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

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.git.azure.commons.utils.Adapter;

/**
 * An abstract implementation that provides the properties support i.e. provides functionality to capture additional
 * properties as key-value pairs.
 *
 * @param <K>
 *         Data type of the key.
 * @param <V>
 *         Data type of the value.
 *
 * @author Mahalingam Iyer
 */
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public abstract class AbstractPropertiesProvider<K, V> implements IPropertiesProvider<K, V> {
    /** Properties associated with this object. */
    @Builder.Default
    private Map<K, V> properties = new LinkedHashMap<>();

    @JsonIgnore
    @Override
    public AbstractPropertiesProvider<K, V> set(final Map<K, V> properties) {
        if (Objects.nonNull(properties) && !properties.isEmpty()) {
            this.properties.clear();
            this.properties.putAll(properties);
        }
        return this;
    }

    @JsonIgnore
    @Override
    public AbstractPropertiesProvider<K, V> add(final K key, final V value) {
        if (Objects.nonNull(key)) {
            properties.put(key, value);
        }
        return this;
    }

    @JsonIgnore
    @Override
    public AbstractPropertiesProvider<K, V> addAll(final Map<K, V> properties) {
        if (Objects.nonNull(properties) && !properties.isEmpty()) {
            this.properties.putAll(properties);
        }
        return this;
    }

    @JsonIgnore
    @Override
    public Map<K, V> get() {
        return properties;
    }

    @JsonIgnore
    @Override
    public V get(final K key) {
        return properties.get(key);
    }

    @JsonIgnore
    @Override
    public <T> T get(final K key, final Class<T> targetType) {
        return Adapter.adaptTo(get(key), targetType);
    }

    @JsonIgnore
    @Override
    public String getAsString(final K key) {
        return get(key, String.class);
    }

    @JsonIgnore
    @Override
    public boolean has(final K key) {
        final V value = get(key);
        if (value instanceof String) {
            return StringUtils.isNotBlank(value.toString());
        }
        return Objects.nonNull(value);
    }
}
