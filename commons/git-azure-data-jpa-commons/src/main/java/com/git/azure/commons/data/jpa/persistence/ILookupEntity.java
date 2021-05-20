/*
 * Copyright (c) 2021 REPLACE_CUSTOMER_NAME. All rights reserved.
 *
 * This file is part of test-git-azure.
 *
 * test-git-azure project and associated code cannot be copied
 * and/or distributed without a written permission of REPLACE_CUSTOMER_NAME,
 * and/or its subsidiaries.
 */
package com.git.azure.commons.data.jpa.persistence;

import java.io.Serializable;

import com.git.azure.commons.data.persistence.IEntity;

/**
 * Based contract for a lookup entity i.e. an entity that has "code" and "value" fields and is generally meant for
 * representing values in a drop-down on the UI side.
 * <p>
 * The "code" of a lookup entity is represented in uppercase (e.g. ACTIVE, INACTIVE) while the "value" is the display
 * value for the corresponding code (i.e. "Active", "Inactive").
 * <p>
 * The lookup entities do not support soft-deletion functionality i.e. they can be hard-deleted as long as they are not
 * referenced by any other entities (because of referential-integrity checks).
 *
 * @param <ID>
 *         Type of the primary keep of the lookup entity.
 *
 * @author Mahalingam Iyer
 */
public interface ILookupEntity<ID extends Serializable> extends IEntity<ID> {
    /**
     * This method returns the unique code for the lookup record (e.g. ACTIVE, INACTIVE).
     *
     * @return Unique code of the lookup entity.
     */
    ID getCode();

    /**
     * This method returns the display value for the corresponding "code" of this lookup record (e.g. Active,
     * Inactive).
     *
     * @return Display value of the corresponding "code" that this lookup entity encapsulates.
     */
    String getValue();

    @Override
    default ID getId() {
        return getCode();
    }
}
