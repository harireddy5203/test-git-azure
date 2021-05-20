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

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.stream.Collectors;

/**
 * Default implementation that wraps the basic user details as modeled in {@link IExtendedUserDetails} contract.
 * <p>
 * This implementation follows the "Composition" pattern over "Inheritance" and subsequently applies additional
 * decorations.
 *
 * @author Mahalingam Iyer
 */
public abstract class AbstractUserPrincipal<T> implements IExtendedUserDetails {
    /**
     * Prefix to be used while building an authority string.
     */
    private static final String AUTHORITY_ROLE_PREFIX = "ROLE_";

    /**
     * Unique identifier of the user.
     */
    public T id;

    /**
     * Reference to the {@link UserDetails} object that holds the user information.
     */
    public UserDetails user;

    /**
     * Display name of the principal and defaulted to empty string.
     */
    public String displayName;

    /** Collection of roles that are assigned to the principal. */
    public final Collection<String> roles = new HashSet<>();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Return an unmodifiable collection.
        return Collections.unmodifiableCollection(user.getAuthorities());
    }

    /**
     * Returns the password used to authenticate the user.
     *
     * @return the password
     */
    @Override
    public String getPassword() {
        return user.getPassword();
    }

    /**
     * Returns the username of the user which is used to authenticate the user.
     *
     * @return the username.
     */
    @Override
    public String getUsername() {
        return user.getUsername();
    }

    /**
     * An expired account cannot be authenticated.
     *
     * @return true if the user's account is non-expired.
     * And false if expired.
     */
    @Override
    public boolean isAccountNonExpired() {
        return user.isAccountNonExpired();
    }

    /**
     * It tells if the user is locked or unlocked. A locked user cannot be
     * authenticated.
     *
     * @return true if the user is not locked and false if locked.
     */
    @Override
    public boolean isAccountNonLocked() {
        return user.isAccountNonLocked();
    }

    /**
     * It tells if account password is Expired or not.
     *
     * @return <code>true</code> if the user's credentials are non-expired,
     * <code>false</code> if expired.
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return user.isCredentialsNonExpired();
    }

    /**
     * It gives whether the user is enabled or disabled. A disabled user cannot be
     * authenticated.
     *
     * @return <code>true</code> if the user is enabled, <code>false</code> otherwise
     */
    @Override
    public boolean isEnabled() {
        return user.isEnabled();
    }

    /**
     * This method returns the display name of the principal (i.e. current logged-in user).
     *
     * @return Display name of the principal.
     */
    @Override
    public Collection<String> getAssignedPermissions() {
        // Return an unmodifiable collection.
        return Collections.unmodifiableCollection(roles);
    }

    public String getDisplayName(){
        return displayName;
    }

    /**
     * This method returns a collection of roles that are assigned to the principal.
     *
     * @return Collection of roles (i.e. roles) that are assigned to the principal.
     */
    @Override
    public Collection<String> getAssignedRoles() {
        return getAuthorities().stream()
                .map(grantedAuthority -> grantedAuthority.getAuthority().replace(AbstractUserPrincipal.AUTHORITY_ROLE_PREFIX, StringUtils.EMPTY))
                .collect(Collectors.toSet());
    }
}
