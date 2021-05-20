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

import java.io.IOException;
import java.util.Collection;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import lombok.extern.slf4j.Slf4j;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.git.azure.commons.error.CommonErrors;
import com.git.azure.commons.exception.ServiceException;

/**
 * Utility class that contains methods for serialization and deserialization of Objects from / to JSON.
 *
 * @author Mahalingam Iyer
 */
@Slf4j
public final class JsonUtils {
    /** Object Mapper instance that will be used for serialization / deserialization. */
    public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    /**
     * Private constructor.
     */
    private JsonUtils() {
        throw new IllegalStateException("Cannot create instance of this object");
    }

    /**
     * Serializes the provided object to JSON format and returns a JSON string. If an exception is encountered during
     * the serialization, the exception message is logged to the console and an empty string is returned.
     *
     * @param object
     *         Object that needs to be serialized to JSON.
     *
     * @return JSON string representation of the provided object.
     */
    public static String serialize(final Object object) {
        if (Objects.isNull(object)) {
            return StringUtils.EMPTY;
        }

        String jsonString = StringUtils.EMPTY;
        try {
            jsonString = JsonUtils.OBJECT_MAPPER.writeValueAsString(object);
        } catch (final JsonProcessingException e) {
            JsonUtils.LOGGER.error(e.getMessage(), e);
        }
        return jsonString;
    }

    /**
     * This method serializes the provided payload to a JSON string. If an exception occurs, it is thrown back to the
     * caller.
     *
     * @param payload
     *         Payload that needs to be serialized.
     *
     * @return Serialized JSON string.
     */
    public static String serializeOrThrow(final Object payload) {
        try {
            return new ObjectMapper().writeValueAsString(payload);
        } catch (final JsonProcessingException e) {
            throw ServiceException.instance(CommonErrors.JSON_SERIALIZATION_FAILED);
        }
    }

    /**
     * This method de-serializes the provided JSON string to an object of type identified by {@code targetType}
     * parameter.
     *
     * @param jsonString
     *         JSON string that needs to be de-serialized.
     * @param targetType
     *         Target type that the de-serialized object needs to be casted to.
     * @param <T>
     *         Target type.
     *
     * @return De-serialized object casted to the appropriate type.
     */
    public static <T> T deserialize(final String jsonString, final Class<T> targetType) {
        try {
            return JsonUtils.OBJECT_MAPPER.readValue(jsonString, targetType);
        } catch (final IOException e) {
            JsonUtils.LOGGER.error(e.getMessage(), e);
            throw ServiceException.instance(CommonErrors.JSON_DESERIALIZATION_FAILED);
        }
    }

    /**
     * This method attempts to create an instance of type {@link ObjectNode}.
     *
     * @return Instance of type {@link ObjectNode}.
     */
    public static ObjectNode createObjectNode() {
        return JsonUtils.OBJECT_MAPPER.createObjectNode();
    }

    /**
     * This method attempts to create an instance of type {@link ArrayNode}.
     *
     * @param node
     *         Node to add to the {@link ArrayNode}.
     *
     * @return Instance of type {@link ArrayNode}.
     */
    public static ArrayNode createArrayNode(final JsonNode node) {
        return JsonUtils.OBJECT_MAPPER.createArrayNode().add(node);
    }

    /**
     * This method attempts to create an instance of type {@link ArrayNode}.
     *
     * @param nodes
     *         Collection of nodes where each element is an instance of type {@link JsonNode}.
     *
     * @return Instance of type {@link ArrayNode}.
     */
    public static ArrayNode createArrayNode(final Collection<? extends JsonNode> nodes) {
        return JsonUtils.OBJECT_MAPPER.createArrayNode().addAll(nodes);
    }
}
