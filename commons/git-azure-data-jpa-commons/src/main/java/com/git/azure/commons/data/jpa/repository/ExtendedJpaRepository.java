/*
 * Copyright (c) 2021 REPLACE_CUSTOMER_NAME. All rights reserved.
 *
 * This file is part of test-git-azure.
 *
 * test-git-azure project and associated code cannot be copied
 * and/or distributed without a written permission of REPLACE_CUSTOMER_NAME,
 * and/or its subsidiaries.
 */
package com.git.azure.commons.data.jpa.repository;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import com.git.azure.commons.Name;
import com.git.azure.commons.data.persistence.IEntity;
import com.git.azure.commons.data.persistence.ISoftDeletable;
import com.git.azure.commons.error.CommonErrors;
import com.git.azure.commons.exception.ServiceException;

/**
 * Base Repository interface that provides common behaviors.
 *
 * @param <T>
 *         Type of the entity.
 * @param <ID>
 *         Type of the primary key.
 *
 * @author Mahalingam Iyer
 */
@NoRepositoryBean
public interface ExtendedJpaRepository<T extends IEntity<ID>, ID extends Serializable> extends JpaRepository<T, ID> {
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
        return findById(id).orElseThrow(() -> ServiceException.instance(CommonErrors.RESOURCE_NOT_FOUND));
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
        return findOne(example).orElseThrow(() -> ServiceException.instance(CommonErrors.RESOURCE_NOT_FOUND));
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
        // 1. Find all the entities with the provided identifiers.
        final Iterable<T> entities = findAllById(ids);

        // 2. Collect all the unique identifiers of the entities.
        final Set<ID> matchingIds = StreamSupport.stream(entities.spliterator(), false)
                .map(IEntity::getId)
                .collect(Collectors.toSet());

        // 3. Are there any Ids in the input that don't exist in the database. If yes, then throw an exception.
        final List<ID> copy = new ArrayList<>(ids);
        // Remove all the elements from matchingIds from this copy.
        copy.removeAll(matchingIds);
        if (!copy.isEmpty()) {
            throw ServiceException.instance(CommonErrors.RESOURCES_NOT_FOUND);
        }

        return entities;
    }

    /**
     * This method attempts to delete the object from the database.
     * <p>
     * The delete mechanism depends on the type of the entity i.e. if the type of the entity is {@link ISoftDeletable},
     * the entity is marked as deleted (by setting the "deleted" flag to true on the entity).
     * <p>
     * Likewise, if the type of the entity is not {@link ISoftDeletable}, then the entity is deleted from the system
     * i.e. hard deleted and there will be no reference to this entity subsequent to the successful deletion.
     *
     * @param id
     *         Unique identifier of the object that needs to be deleted (i.e. soft-deleted or hard-deleted based on the
     *         type of the entity).
     *
     * @return Unique identifier of the object that was deleted.
     */
    @SuppressWarnings("rawtypes")
    default ID deleteOne(final ID id) {
        return deleteOneOrThrow(id, entity -> Boolean.TRUE);
    }

    /**
     * This method attempts to delete the object from the database.
     * <p>
     * The delete mechanism depends on the type of the entity i.e. if the type of the entity is {@link ISoftDeletable},
     * the entity is marked as deleted (by setting the "deleted" flag to true on the entity).
     * <p>
     * Likewise, if the type of the entity is not {@link ISoftDeletable}, then the entity is deleted from the system
     * i.e. hard deleted and there will be no reference to this entity subsequent to the successful deletion.
     *
     * @param id
     *         Unique identifier of the object that needs to be deleted (i.e. soft-deleted or hard-deleted based on the
     *         type of the entity).
     * @param executePreConditions
     *         Function that gets called before the action deletion happens. Any pre-processing work can be done and a
     *         boolean can be returned to indicate if the deletion can proceed or not.
     * @param <R>
     *         Response type of the pre-processing hook, which is a boolean indicating if the deletion can proceed or
     *         not.
     *
     * @return Unique identifier of the object that was deleted.
     */
    @SuppressWarnings("rawtypes")
    default <R extends Boolean> ID deleteOneOrThrow(final ID id, final Function<T, R> executePreConditions) {
        // 1. Validate the provided identifier.
        if (Objects.isNull(id)) {
            throw ServiceException.instance(CommonErrors.ILLEGAL_ARGUMENT_DETAILED, Name.ID.key());
        }

        // 2. Check if the entity exists in the system.
        final T entity = findById(id).orElseThrow(() -> ServiceException.instance(CommonErrors.RESOURCE_NOT_FOUND_DETAILED, Name.ID.key(), id));

        // 3. If pre-processing hook is specified, delegate to it before the deletion is done.
        boolean canProceed = true;
        if (Objects.nonNull(executePreConditions)) {
            // Delegate to the hook for any pre-processing actions.
            canProceed = executePreConditions.apply(entity);
        }

        if (canProceed) {
            // 4. Perform the delete based on whether the entity supports soft-delete or hard-delete.
            if (entity instanceof ISoftDeletable) {
                ((ISoftDeletable) entity).setDeleted(true);
                ((ISoftDeletable) entity).setDeletedTimestamp(System.currentTimeMillis());
                save(entity);
            } else {
                delete(entity);
            }
        }
        return id;
    }
}
