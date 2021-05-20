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

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Default implementation of a properties provider where the key is of type {@link String} and value is of type {@link
 * Object}.
 *
 * @author Mahalingam Iyer
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@ToString(callSuper = true, of = {})
@EqualsAndHashCode(callSuper = true, of = {})
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
public class DefaultPropertiesProvider extends AbstractPropertiesProvider<String, Object> {
}
