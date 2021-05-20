/*
 * Copyright (c) 2021 REPLACE_CUSTOMER_NAME. All rights reserved.
 *
 * This file is part of test-git-azure.
 *
 * test-git-azure project and associated code cannot be copied
 * and/or distributed without a written permission of REPLACE_CUSTOMER_NAME,
 * and/or its subsidiaries.
 */
package com.git.azure.security.db.service;

import java.text.MessageFormat;
import java.util.Objects;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;

import com.git.azure.security.db.model.persistence.UserPrincipalEntity;
import com.git.azure.security.db.repository.UserEntityRepository;

/**
 * Abstract implementation that provides the necessary abstractions to load user-specific data.
 * <p>
 * It is the responsibility of the subclass implementations to perform the specifics of connecting to their data-sources
 * and finding the appropriate users.
 *
 * @author Mahalingam Iyer
 */
public abstract class AbstractUserDetailsService implements UserDetailsService {
    /** User not found error message. */
    private static final String USER_NOT_FOUND_MESSAGE = "Unable to find user with username - {0}";

    /** User not found error message. */
    private static final String USER_NOT_FOUND_MESSAGE_2 = "Unable to find user with id - {0}";

    /** Repository implementation of type {@link UserEntityRepository}. */
    protected final UserEntityRepository userEntityRepository;

    /**
     * Constructor.
     *
     * @param userEntityRepository
     *         Repository implementation of type {@link UserEntityRepository}.
     */
    public AbstractUserDetailsService(final UserEntityRepository userEntityRepository) {
        this.userEntityRepository = userEntityRepository;
    }

    @Transactional(readOnly = true)
    @Override
    public UserDetails loadUserByUsername(final String username) {
        if (StringUtils.isBlank(username)) {
            // Should not generally occur.
            throw new IllegalArgumentException("Username cannot be null / empty");
        }

        // Find the user either by username or by email.
        final Optional<UserPrincipalEntity> matchingUser = userEntityRepository.findByUsername(username);
        if (Objects.isNull(matchingUser)) {
            final String error = MessageFormat.format(AbstractUserDetailsService.USER_NOT_FOUND_MESSAGE, username);
            throw new UsernameNotFoundException(error);
        }

        if(matchingUser.isPresent()){
            // Now delegate to the method that transforms this user object to the principal object.
            return matchingUser.get().toUserPrincipal();
        }
        return null;
    }
}