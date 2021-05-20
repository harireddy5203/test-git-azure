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
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.Param;

import com.git.azure.commons.data.persistence.IEntity;
import com.git.azure.commons.data.persistence.ISoftDeletable;

/**
 * Base Repository interface that overrides finder methods ({@code findById(ID)}, {@code findAll()}, {@code
 * findAll(Pageable)}) to exclude soft deleted entities.
 *
 * @param <T>
 *         Type of the entity.
 * @param <ID>
 *         Type of the primary key.
 *
 * @author Mahalingam Iyer
 */
@NoRepositoryBean
public interface ExtendedSoftDeletableJpaRepository<T extends IEntity<ID> & ISoftDeletable, ID extends Serializable> extends ExtendedJpaRepository<T, ID> {
    @Override
    @Query(value = "SELECT entity FROM #{#entityName} entity WHERE entity.id = :id AND entity.deleted = FALSE")
    Optional<T> findById(@Param("id") ID id);

    @Override
    @Query(value = "SELECT CASE WHEN COUNT(entity) > 0 THEN true ELSE false END FROM #{#entityName} entity WHERE entity.id = :id AND entity.deleted = FALSE")
    boolean existsById(@Param("id") ID id);

    @Override
    @Query(value = "SELECT entity FROM #{#entityName} entity WHERE entity.deleted = FALSE")
    List<T> findAll();

    @Override
    @Query(value = "SELECT entity FROM #{#entityName} entity WHERE entity.deleted = FALSE")
    List<T> findAll(Sort sort);

    @Override
    @Query(value = "SELECT entity FROM #{#entityName} entity WHERE entity.id IN :ids AND entity.deleted = FALSE")
    List<T> findAllById(@Param("ids") Iterable<ID> ids);

    @Override
    @Query(value = "SELECT entity FROM #{#entityName} entity WHERE entity.id = :id AND entity.deleted = FALSE")
    T getOne(@Param("id") ID id);

    @Override
    @Query(value = "SELECT entity FROM #{#entityName} entity WHERE entity.deleted = FALSE")
    Page<T> findAll(Pageable pageable);

    /**
     * This method returns all the deleted records in a pagination manner based on the provided pagination settings.
     *
     * @param pageable
     *         Page settings.
     *
     * @return Collection of records that were soft deleted.
     */
    @Query(value = "SELECT entity FROM #{#entityName} entity WHERE entity.deleted = TRUE")
    Page<T> findDeleted(Pageable pageable);
}
