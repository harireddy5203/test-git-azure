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

import lombok.Setter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * Default implementation that wraps the basic user details as modeled in {@link IExtendedUserDetails} contract.
 * <p>
 * This implementation follows the "Composition" pattern over "Inheritance" and subsequently applies additional
 * decorations.
 *
 * @author Mahalingam Iyer
 */
@EqualsAndHashCode(of = {"id"})
@ToString(of = {"id"})
@Getter
@Setter
public class UserPrincipal extends AbstractUserPrincipal<String>  {
    /** The unique identifier of the user. */
    private String id;

    /** first name of the user. */
    private final String firstName;

    /** last name of the user. */
    private final String lastName;

    /**
     * Constructor.
     *
     * @param user
     *         User instance of type {@link User}.
     * @param id
     *         Unique identifier of the user principal.
     * @param displayName
     *         Display name of the user principal.
     */
    public UserPrincipal(final UserDetails user, final String id, final String firstName, final String lastName,
                         final String displayName) {
        // Build the base user details.
        this.user = user;
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.displayName = displayName;
    }
}
