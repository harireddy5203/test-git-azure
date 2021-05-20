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

import java.util.Collection;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import lombok.extern.slf4j.Slf4j;

import com.git.azure.security.userdetails.IExtendedUserDetails;

/**
 * Abstract implementation of a permission evaluator and can be used for PBAC (Permission Based Access Control) i.e.
 * this can be used in @{{@link org.springframework.security.access.prepost.PreAuthorize}} annotation to evaluate if the
 * principal has the necessary permissions i.e. the principal in question has access to the concerned permissions.
 *
 * @author Mahalingam Iyer
 */
@Slf4j
public abstract class AbstractPermissionEvaluator implements IPermissionEvaluator {
    @Override
    public boolean hasPermission(final IExtendedUserDetails userPrincipal, final String permissionCode) {
        boolean hasPermission = false;
        if (Objects.nonNull(userPrincipal) && StringUtils.isNotBlank(permissionCode)) {
            hasPermission = userPrincipal.getAssignedPermissions().contains(permissionCode);
        }

        AbstractPermissionEvaluator.LOGGER.debug("User={}, permission={}, hasPermission={}", userPrincipal, permissionCode, hasPermission);
        return hasPermission;
    }

    @Override
    public boolean hasAllPermissions(final IExtendedUserDetails userPrincipal, final String... permissionCodes) {
        boolean hasPermission = false;
        if (Objects.nonNull(userPrincipal) && Objects.nonNull(permissionCodes) && permissionCodes.length > 0) {
            // Get the permissions enabled for the principal
            final Collection<String> permissionsEnabledForUser = userPrincipal.getAssignedPermissions();
            for (final String permissionCode : permissionCodes) {
                hasPermission = permissionsEnabledForUser.contains(permissionCode);
                if (!hasPermission) {
                    break;
                }
            }
        }

        AbstractPermissionEvaluator.LOGGER.debug("User={}, permissions={}, hasPermission={}", userPrincipal, permissionCodes, hasPermission);
        return hasPermission;
    }

    @Override
    public boolean hasAnyPermissions(final IExtendedUserDetails userPrincipal, final String... permissionCodes) {
        boolean hasPermission = false;
        if (Objects.nonNull(userPrincipal) && Objects.nonNull(permissionCodes) && permissionCodes.length > 0) {
            // Get the permissions enabled for the principal.
            final Collection<String> permissionsEnabledForUser = userPrincipal.getAssignedPermissions();
            for (final String permissionCode : permissionCodes) {
                if (permissionsEnabledForUser.contains(permissionCode)) {
                    hasPermission = true;
                    break;
                }
            }
        }

        AbstractPermissionEvaluator.LOGGER.debug("User={}, permissions={}, hasPermission={}", userPrincipal, permissionCodes, hasPermission);
        return hasPermission;
    }
}
