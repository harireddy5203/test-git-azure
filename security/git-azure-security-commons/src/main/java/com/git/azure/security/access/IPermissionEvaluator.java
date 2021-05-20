/*
 * Copyright (c) 2021 REPLACE_CUSTOMER_NAME. All rights reserved.
 *
 * This file is part of test-git-azure.
 *
 * test-git-azure project and associated code cannot be copied
 * and/or distributed without a written permission of REPLACE_CUSTOMER_NAME,
 * and/or its subsidiaries.
 */
package com.git.azure.security.access;

import org.springframework.security.core.Authentication;

import com.git.azure.security.userdetails.IExtendedUserDetails;
import com.git.azure.security.utils.AuthenticationUtils;

/**
 * Contract for a permission evaluator and can be used for PBAC (Permission Based Access Control) i.e. this can be used
 * in @{@link org.springframework.security.access.annotation.Secured} annotation to evaluate if the principal has the
 * necessary permissions i.e. the principal in question has access to the concerned permissions.
 *
 * @author Mahalingam Iyer
 */
public interface IPermissionEvaluator {
    /**
     * For the provided authentication, this method attempts to evaluate if the current authenticated user has
     * permissions for the mentioned permission code.
     *
     * @param authentication
     *         Authentication object of type {@link Authentication} and holds the details of the authenticated user. If
     *         there is no authenticated user, this will point to anonymous user.
     * @param permissionCode
     *         Unique identifier of the permission
     *
     * @return True if the current authenticated user has the permissions for the respective permission code else
     * returns false.
     */
    default boolean hasPermission(Authentication authentication, String permissionCode) {
        return hasPermission(AuthenticationUtils.getPrincipal(authentication), permissionCode);
    }

    /**
     * For the provided authentication, this method attempts to evaluate if the current authenticated user has
     * permissions for the mentioned permission code.
     *
     * @param userPrincipal
     *         Authenticated user of type {@link com.git.azure.security.userdetails.IExtendedUserDetails}
     * @param permissionCode
     *         Unique identifier of the permission
     *
     * @return True if the current authenticated user has the permissions for the respective permission code else
     * returns false.
     */
    boolean hasPermission(IExtendedUserDetails userPrincipal, String permissionCode);

    /**
     * For the provided authentication, this method attempts to evaluate if the current authenticated user has the
     * permissions for ALL the mentioned permission codes.
     *
     * @param authentication
     *         Authentication object of type {@link Authentication} and holds the details of the authenticated user. If
     *         there is no authenticated user, this will point to anonymous user.
     * @param permissionCodes
     *         Collection of permission codes
     *
     * @return True if the current authenticated user has the permissions for ALL the permission codes else returns
     * false.
     */
    default boolean hasAllPermissions(Authentication authentication, String... permissionCodes) {
        return hasAllPermissions(AuthenticationUtils.getPrincipal(authentication), permissionCodes);
    }

    /**
     * For the provided authentication, this method attempts to evaluate if the current authenticated user has the
     * permissions for ALL the mentioned permission codes.
     *
     * @param userPrincipal
     *         Authenticated user of type {@link IExtendedUserDetails}
     * @param permissionCodes
     *         Collection of permission codes
     *
     * @return True if the current authenticated user has the permissions for ALL the permission codes else returns
     * false.
     */
    boolean hasAllPermissions(IExtendedUserDetails userPrincipal, String... permissionCodes);

    /**
     * For the provided authentication, this method attempts to evaluate if the current authenticated user has the
     * permissions for any of the mentioned permission codes.
     *
     * @param authentication
     *         Authentication object of type {@link Authentication} and holds the details of the authenticated user. If
     *         there is no authenticated user, this will point to anonymous user.
     * @param permissionCodes
     *         Collection of permission codes
     *
     * @return True if the current authenticated user has the permissions for any of the permission codes else returns
     * false.
     */
    default boolean hasAnyPermissions(Authentication authentication, String... permissionCodes) {
        return hasAnyPermissions(AuthenticationUtils.getPrincipal(authentication), permissionCodes);
    }

    /**
     * For the provided authentication, this method attempts to evaluate if the current authenticated user has the
     * permissions for any of the mentioned permission codes.
     *
     * @param userPrincipal
     *         Authenticated user of type {@link IExtendedUserDetails}
     * @param permissionCodes
     *         Collection of permission codes
     *
     * @return True if the current authenticated user has the permissions for any of the permission codes else returns
     * false.
     */
    boolean hasAnyPermissions(IExtendedUserDetails userPrincipal, String... permissionCodes);
}
