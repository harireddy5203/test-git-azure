/*
 * Copyright (c) 2021 REPLACE_CUSTOMER_NAME. All rights reserved.
 *
 * This file is part of test-git-azure.
 *
 * test-git-azure project and associated code cannot be copied
 * and/or distributed without a written permission of REPLACE_CUSTOMER_NAME,
 * and/or its subsidiaries.
 */
package com.git.azure.commons.data.mongo.repository;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.data.domain.Example;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.NoRepositoryBean;

import com.git.azure.commons.Name;
import com.git.azure.commons.data.persistence.IEntity;
import com.git.azure.commons.data.persistence.ISoftDeletable;
import com.git.azure.commons.error.CommonErrors;
import com.git.azure.commons.exception.ServiceException;
import com.git.azure.commons.utils.ValidationUtils;

/**
 * Base Repository implementation that extends {@link MongoRepository} and provides additional behaviors like {@code
 * findByIdOrThrow}, {@code findOneOrThrow}, {@code deleteOne} supporting soft-delete or hard-delete including execution
 * of pre-conditions, etc.
 *
 * @author Mahalingam Iyer
 */
@NoRepositoryBean
public interface ExtendedMongoRepository<T extends IEntity<ID>, ID extends Serializable> extends MongoRepository<T, ID> {
    /**
     * This method attempts to find an entity based on the provided {@code id} parameter. If the resource is not found,
     * an exception is thrown.
     *
     * @param id
     *         Unique identifier of the entity that needs to be retrieved.
     *
     * @return Matching entity instance if found else an exception is thrown.
     */
    default T findByIdOrThrow(final ID id) {
        return findById(id).orElseThrow(() -> ServiceException.instance(CommonErrors.RESOURCE_NOT_FOUND_DETAILED, Name.ID.key(), id));
    }

    /**
     * This method attempts to find an entity based on the provided {@code example} parameter. If the resource is not
     * found, an exception is thrown.
     *
     * @param example
     *         Example instance based on which the resource needs to be found (leverages QBE - Query By Example).
     *
     * @return Matching entity instance if found else an exception is thrown.
     */
    default T findOneOrThrow(final Example<T> example) {
        return findOne(example).orElseThrow(() -> ServiceException.instance(CommonErrors.RESOURCE_NOT_FOUND, Name.EXAMPLE.key(), example));
    }

    /**
     * This method attempts to find the entities for the provided identifiers. An exception is thrown in situations
     * where any of the provided identifiers do not exist in the system.
     *
     * @param ids
     *         Collection of unique identifiers of the entities that needs to be retrieved from the system.
     *
     * @return Map containing the matching entity instances for the provided identifiers. The key in the map is the
     * unique identifier of the entity and the value is an instance of type T.
     */
    default Iterable<T> findAllOrThrow(final Collection<ID> ids) {
        // 1. Validate the input.
        final ValidationUtils validationUtils = ValidationUtils.instance();
        validationUtils.assertCollectionAndElementsNotEmpty(ids, Name.IDS.key());

        // 2. Find all the entities with the provided identifiers.
        final Iterable<T> entities = findAllById(ids);

        // 3. Collect all the unique identifiers of the entities.
        final Set<ID> matchingIds = StreamSupport.stream(entities.spliterator(), false)
                .map(IEntity::getId)
                .collect(Collectors.toSet());

        // 4. Are there any Ids in the input that don't exist in the database. If yes, then throw an exception.
        validationUtils.assertSameCollections(ids, matchingIds, Name.IDS.key());

        return entities;
    }

    /**
     * This method attempts to find an entity based on the provided example ({@code example} parameter). The deletion is
     * performed based on whether the entity represents {@link ISoftDeletable} or not.
     *
     * @param example
     *         Example matcher.
     * @param executePreConditions
     *         Consumer that gets called before the deletion is triggered. This is called irrespective of whether the
     *         entity is a {@link ISoftDeletable} or not.
     *
     * @return Unique identifier of the entity that got deleted.
     */
    @SuppressWarnings("rawtypes")
    default <R extends Boolean> ID deleteOne(final Example<T> example, final Function<T, R> executePreConditions) {
        // 1. Validate the provided example.
        ValidationUtils.instance().assertNotNullAndEmpty(example, Name.EXAMPLE.key());

        // 2. Check if the entity exists in the system.
        final T entity = findOneOrThrow(example);

        // 3. Perform the delete based on whether the entity supports soft-delete or hard-delete.
        final ID id = entity.getId();

        // 4. Delegate to the consumer to perform any pre-condition activities
        boolean canProceed = true;
        if (Objects.nonNull(executePreConditions)) {
            // Delegate to the hook for any pre-processing actions.
            canProceed = executePreConditions.apply(entity);
        }

        if (canProceed) {
            // 5. Perform the delete based on whether the entity supports soft-delete or hard-delete.
            if (entity instanceof ISoftDeletable) {
                // Set the "deleted" flag on the entity to true and delegate to save(..)
                ((ISoftDeletable) entity).setDeleted(true);
                save(entity);
            } else {
                // Perform the hard delete.
                delete(entity);
            }
        }
        return id;
    }

    /**
     * This method attempts to find multiple entities based on the provided example ({@code example} parameter). The
     * deletion is performed based on whether the entity represents {@link ISoftDeletable} or not.
     *
     * @param example
     *         Example matcher.
     * @param executePreConditions
     *         Consumer that gets called before the deletion is triggered. This is called irrespective of whether the
     *         entity is a {@link ISoftDeletable} or not.
     *
     * @return Collection of Unique identifiers of the entities that were deleted.
     */
    @SuppressWarnings("rawtypes")
    default <R extends Boolean> Collection<ID> deleteAll(final Example<T> example,
                                                         final Function<T, R> executePreConditions) {
        // 1. Validate the provided example.
        ValidationUtils.instance().assertNotNullAndEmpty(example, Name.EXAMPLE.key());

        // 2. Find the entities based on the provided example.
        final List<T> entities = findAll(example);
        if (entities.isEmpty()) {
            return Collections.emptySet();
        }

        // Capture the ids of the entities that we can return after the deletion indicating the ids that got deleted.
        final Set<ID> ids = entities.stream()
                .map(IEntity::getId)
                .collect(Collectors.toSet());

        // 3. Loop through the entities and delete them one by one (we can optimize later for bulk case).
        for (final T entity : entities) {
            // 4. If pre-processing hook is specified, delegate to it before the deletion is done.
            boolean canProceed = true;
            if (Objects.nonNull(executePreConditions)) {
                canProceed = executePreConditions.apply(entity);
            }

            if (canProceed) {
                // 5. Perform the delete based on whether the entity supports soft-delete or hard-delete.
                if (entity instanceof ISoftDeletable) {
                    // Set the "deleted" flag on the entity to true and delegate to save(..)
                    ((ISoftDeletable) entity).setDeleted(true);
                    ((ISoftDeletable) entity).setDeletedTimestamp(System.currentTimeMillis());
                    save(entity);
                } else {
                    // Perform the hard delete.
                    delete(entity);
                }
            }
        }

        // 6. Return the deleted ids.
        return ids;
    }
}
