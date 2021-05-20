/*
 * Copyright (c) 2021 REPLACE_CUSTOMER_NAME. All rights reserved.
 *
 * This file is part of test-git-azure.
 *
 * test-git-azure project and associated code cannot be copied
 * and/or distributed without a written permission of REPLACE_CUSTOMER_NAME,
 * and/or its subsidiaries.
 */
package com.git.azure.security.utils;

import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.git.azure.commons.utils.Adapter;
import com.git.azure.security.userdetails.UserPrincipal;

/**
 * Utility class that provides helper methods to extract user principal from the Authentication object, etc.
 *
 * @author Mahalingam Iyer
 */
public final class AuthenticationUtils {
    /**
     * Private constructor.
     */
    private AuthenticationUtils() {
        throw new IllegalStateException("Cannot create instances of this class");
    }

    /**
     * This method returns the current logged in user (principal) of type {@link UserPrincipal} from the {@link
     * Authentication} object available in the {@link org.springframework.security.core.context.SecurityContext}.
     *
     * @return User principal of type {@link UserPrincipal} if the authentication object holds one, else returns null
     */
    public static UserPrincipal getPrincipal() {
        return AuthenticationUtils.getPrincipal(SecurityContextHolder.getContext().getAuthentication());
    }

    /**
     * This method returns the principal identifier (i.e. unique identifier of the current logged in user) by extracting
     * the user principal from the {@link Authentication} object available in the {@link
     * org.springframework.security.core.context.SecurityContext}.
     *
     * @return Unique identifier of the current logged in user / principal.
     */
    public static String getPrincipalId() {
        final UserPrincipal userPrincipal = AuthenticationUtils.getPrincipal();
        if (Objects.isNull(userPrincipal)) {
            return StringUtils.EMPTY;
        }

        return String.valueOf(userPrincipal.getId());
    }

    /**
     * This method returns the principal of type {@link UserPrincipal} if the provided authentication object holds the
     * principal of this type
     *
     * @param authentication
     *         Authentication object of type {@link Authentication} and holds the details of the authenticated user. If
     *         there is no authenticated user, this will point to anonymous user.
     *
     * @return User principal of type {@link UserPrincipal} if the authentication object holds one, else returns null
     */
    public static UserPrincipal getPrincipal(final Authentication authentication) {
        return getPrincipal(authentication, UserPrincipal.class);
    }

    /**
     * This method returns the principal contained within the authentication object and adapts to the specified type if
     * possible. If the adaptation fails, this method returns null.
     *
     * @param authentication
     *         Authentication object of type {@link Authentication} and holds the details of the authenticated user. If
     *         there is no authenticated user, this will point to anonymous user.
     * @param userPrincipalType
     *         Target type of the user principal that needs to be adapted to
     * @param <T>
     *         Target type
     *
     * @return User principal adapted to the target type. If the adaptation fails, this method returns null
     */
    public static <T> T getPrincipal(final Authentication authentication, final Class<T> userPrincipalType) {
        // Get the principal object from the authentication object.
        final Object authenticationPrincipal = Objects.nonNull(authentication) && Objects.nonNull(authentication.getPrincipal())
                ? authentication.getPrincipal()
                : null;

        // Cast the principal object to the specified type if possible, else return null.
        T principal = null;
        if (Objects.nonNull(authenticationPrincipal)) {
            principal = Adapter.adaptTo(authenticationPrincipal, userPrincipalType);
        }
        return principal;
    }
}
