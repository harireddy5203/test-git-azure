/*
 * Copyright (c) 2021 REPLACE_CUSTOMER_NAME. All rights reserved.
 *
 * This file is part of test-git-azure.
 *
 * test-git-azure project and associated code cannot be copied
 * and/or distributed without a written permission of REPLACE_CUSTOMER_NAME,
 * and/or its subsidiaries.
 */
package com.git.azure.security.db.model.persistence;

import javax.persistence.*;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.git.azure.commons.data.jpa.persistence.AbstractUUIDGeneratedEntity;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import com.git.azure.security.userdetails.UserPrincipal;

/**
 * Implementation that maps the "user" table in the database to an entity in the ORM world.
 *
 * @author Mahalingam Iyer
 */
@ToString(of = {"username"}, callSuper = true)
@EqualsAndHashCode(of = {"username"},callSuper = true)
@Getter
@Setter
@Table(name = "user")
@Entity
public class UserPrincipalEntity extends AbstractUUIDGeneratedEntity {
    /** User display name format, where {0} is first name and {1} is last name. */
    private static final String DISPLAY_NAME_FORMAT = "{0} {1}";

    /** GrantedAuthority format as required by Spring Security. */
    private static final String GRANTED_AUTHORITY_FORMAT = "ROLE_{0}";

    /** User name of the user. */
    @Column(name = "username", unique = true, length = 64, nullable = false)
    private String username;

    /** First name of the user. */
    @Column(name = "first_name", length = 64)
    private String firstName;

    /** Last name of the user. */
    @Column(name = "last_name", length = 64)
    private String lastName;

    /** Password. */
    @Column(name = "password", length = 128, nullable = false)
    private String password;

    /** Is the user account enabled or not. */
    @Column(name = "enabled")
    private Boolean enabled = Boolean.TRUE;

    /** Many to Many mappings between the user and role. */
    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(name = "user_role",
               joinColumns = {@JoinColumn(name = "user_id")},
               inverseJoinColumns = {@JoinColumn(name = "role_id")})
    private Set<UserRoleEntity> roles = new HashSet<>();

    /**
     * Returns the display name which is a concatenation of first name and last name.
     *
     * @return Display name for the user.
     */
    public String getDisplayName() {
        final String fName = Optional.ofNullable(firstName).orElse(StringUtils.EMPTY).trim();
        final String lName = Optional.ofNullable(lastName).orElse(StringUtils.EMPTY).trim();
        return MessageFormat.format(UserPrincipalEntity.DISPLAY_NAME_FORMAT, fName, lName);
    }

    /**
     * This method returns a collection of role names that are assigned to this user.
     *
     * @return Collection of role names.
     */
    public Collection<String> getAssignedRoleNames() {
        // @formatter:off
        return Optional.ofNullable(roles)
                       .orElse(Collections.emptySet())
                       .stream()
                       .map(UserRoleEntity::getName)
                       .collect(Collectors.toSet());
        // @formatter:on
    }

    /**
     * This method returns a collection of authority names (i.e. ROLE_[role name]), which are the roles assigned to this
     * user and prefixed by 'ROLE_'.
     *
     * @return Collection of authority names.
     */
    public Collection<String> getAuthorityNamesForAssignedRoles() {
        // @formatter:off
        return Optional.ofNullable(roles)
                       .orElse(Collections.emptySet())
                       .stream()
                       .map(role -> MessageFormat.format(UserPrincipalEntity.GRANTED_AUTHORITY_FORMAT, role.getName()))
                       .collect(Collectors.toSet());
        // @formatter:on
    }

    /**
     * This method attempts to transform the provided user entity (of type {@link UserPrincipalEntity}) to an instance
     * of type {@link UserDetails}.
     * <p>
     * The default implementation returns an instance of {@link UserPrincipal}. Subclasses can override to provide their
     * own implementations of {@link UserDetails}.
     *
     * @return User details instance for the provided user.
     */
    public UserDetails toUserPrincipal() {
        // Get all the roles associated to the concerned user.
        final Collection<String> roleNames = getAuthorityNamesForAssignedRoles();

        // Build the base user details using the above roles as the authority list.
        // @formatter:off
        final UserDetails baseUserDetails = User.builder()
                                                .username(getUsername())
                                                .password(getPassword())
                                                .disabled(!getEnabled())
                                                .authorities(roleNames.toArray(new String[0]))
                                                .build();
        // @formatter:on

        // Delegate to the below method to create and return an instance of UserPrincipal.
        return toUserPrincipal(baseUserDetails);
    }

    /**
     * This method attempts to transform the provided user entity (of type {@link UserPrincipalEntity}) to an instance
     * of type {@link UserDetails}.
     * <p>
     * The default implementation returns an instance of {@link UserPrincipal}. Subclasses can override to provide their
     * own implementations of {@link UserDetails}.
     *
     * @param userDetails
     *         User details instance of type {@link UserDetails}.
     *
     * @return User details instance for the provided user.
     */
    public UserDetails toUserPrincipal(final UserDetails userDetails) {
        return new UserPrincipal(userDetails, getId(), getFirstName(),getLastName(), getDisplayName());
    }
}
