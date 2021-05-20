/*
 * Copyright (c) 2021 REPLACE_CUSTOMER_NAME. All rights reserved.
 *
 * This file is part of test-git-azure.
 *
 * test-git-azure project and associated code cannot be copied
 * and/or distributed without a written permission of REPLACE_CUSTOMER_NAME,
 * and/or its subsidiaries.
 */
package com.git.azure.security.userdetails;

import java.util.Collection;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Contract that provides basic user information. Extends what is provided by {@link UserDetails} to include the user's
 * basic details like first name, last name, email address, etc.
 * <p>
 * This can further be extended to add any additional user information that are relevant in the context of the
 * application.
 *
 * @author Mahalingam Iyer
 */
public interface IExtendedUserDetails<T> extends UserDetails {
    /**
     * This method returns the unique identifier of the principal (i.e. current logged-in user).
     *
     * @return Unique identifier of the principal.
     */
    T getId();

    /**
     * This method returns the display name of the principal (i.e. current logged-in user).
     *
     * @return Display name of the principal.
     */
    String getDisplayName();

    /**
     * This method returns a collection of permissions (i.e. permission codes like CREATE_USER, UPDATE_USER, etc.) that
     * are assigned to the principal.
     * <p>
     * These permissions are further utilized to enable the PBAC (Permission Based Access Control).
     * <p>
     * The implementation can return an empty collection if PBAC is not used in the application.
     *
     * @return Collection of permissions (i.e. permission codes) that the principal has access to.
     */
    Collection<String> getAssignedPermissions();

    /**
     * This method returns a collection of roles that are assigned to the principal.
     *
     * @return Collection of roles (i.e. roles) that are assigned to the principal.
     */
    Collection<String> getAssignedRoles();
}
