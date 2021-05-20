/*
 * Copyright (c) 2021 REPLACE_CUSTOMER_NAME. All rights reserved.
 *
 * This file is part of test-git-azure.
 *
 * test-git-azure project and associated code cannot be copied
 * and/or distributed without a written permission of REPLACE_CUSTOMER_NAME,
 * and/or its subsidiaries.
 */
package com.git.azure.commons.data.mongo.persistence;

import java.io.Serializable;

import org.springframework.data.annotation.Id;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import com.git.azure.commons.data.persistence.IEntity;
import com.git.azure.commons.properties.DefaultPropertiesProvider;

/**
 * An abstract implementation of a Mongo Document that can be subclassed by concrete implementations.
 * <p>
 * This abstract implementation provides the notion of "properties" i.e. documents subclassing from this implementation
 * inherently get "properties" attribute that can contain unlimited number of properties, which are expressed as
 * key-value pairs.
 *
 * @author Mahalingam Iyer
 */
@EqualsAndHashCode(of = {"id"}, callSuper = false)
@ToString(of = {"id"})
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public abstract class AbstractDocument<ID extends Serializable> extends DefaultPropertiesProvider implements IEntity<ID> {
    /** Unique identifier of the document. */
    @Id
    private ID id;
}
