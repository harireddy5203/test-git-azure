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

import javax.persistence.MappedSuperclass;
import java.io.Serializable;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * An abstract implementation of a lookup entity.
 * <p>
 * The reason why {@code code} and {@code value} are not abstracted here is because of the differing length limits for
 * {@code code} and {@code value} attributes from one lookup type to another.
 * <p>
 * For example: {@code Status} lookup can have the length limit for {@code code} as 6 (OK, NOT_OK) but for {@code
 * TenantStatus} lookup, the length limit for {@code code} field can be different (TENANT_ADMIN_ACCOUNT_ACTIVATION_PENDING,
 * TENANT_DB_PROVISIONING_IN_PROGRESS).
 *
 * @param <ID>
 *         Type of the primary key.
 *
 * @author Mahalingam Iyer
 */
@ToString(callSuper = true, of = {})
@EqualsAndHashCode(of = {})
@Getter
@Setter
@MappedSuperclass
public abstract class AbstractLookupEntity<ID extends Serializable> implements ILookupEntity<ID> {
}
