/*
 * Copyright (c) 2020-2021 Innominds inc. All Rights Reserved. This software is
 * confidential and proprietary information of Innominds inc. You shall not disclose
 * Confidential Information and shall use it only in accordance with the terms
 *
 */
package com.git.azure.features.platform.web.service;

import com.git.azure.commons.data.utils.PageUtils;
import com.git.azure.commons.instrumentation.Instrument;
import com.git.azure.features.platform.data.mapper.DeptMapper;
import com.git.azure.features.platform.data.model.experience.dept.CreateDeptRequest;
import com.git.azure.features.platform.data.model.experience.dept.Dept;
import com.git.azure.features.platform.data.model.experience.dept.UpdateDeptRequest;
import com.git.azure.features.platform.data.model.persistence.DeptEntity;
import com.git.azure.features.platform.data.repository.DeptRepository;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

/**
 * Service implementation that provides CRUD (Create, Read, Update, Delete) capabilities for
 * entities of type {@link DeptEntity}.
 *
 * @author Mahalingam Iyer
 */
@Slf4j
@Validated
@Service
public class DeptService {
    /** Repository implementation of type {@link DeptRepository}. */
    private final DeptRepository deptRepository;

    /** Mapper implementation of type {@link DeptMapper} to transform between different types. */
    private final DeptMapper deptMapper;

    /**
     * Constructor.
     *
     * @param deptRepository Repository instance of type {@link DeptRepository}.
     * @param deptMapper Mapper instance of type {@link DeptMapper}.
     */
    public DeptService(final DeptRepository deptRepository, final DeptMapper deptMapper) {
        this.deptRepository = deptRepository;
        this.deptMapper = deptMapper;
    }

    /**
     * This method attempts to create an instance of type {@link DeptEntity} in the system based on
     * the provided payload.
     *
     * @param payload Payload containing the details required to create an instance of type {@link
     *     DeptEntity}.
     * @return An experience model of type {@link Dept} that represents the newly created entity of
     *     type {@link DeptEntity}.
     */
    @Instrument
    @Transactional
    public Dept createDept(@Valid final CreateDeptRequest payload) {
        // 1. Transform the experience model to a persistence model.
        final DeptEntity deptEntity = deptMapper.transform(payload);

        // 2. Save the entity.
        DeptService.LOGGER.debug("Saving a new instance of type - DeptEntity");
        final DeptEntity newInstance = deptRepository.save(deptEntity);

        // 3. Transform the created entity to an experience model and return it.
        return deptMapper.transform(newInstance);
    }

    /**
     * This method attempts to update an existing instance of type {@link DeptEntity} using the
     * details from the provided input, which is an instance of type {@link UpdateDeptRequest}.
     *
     * @param deptId Unique identifier of Dept in the system, which needs to be updated.
     * @param payload Request payload containing the details of an existing Dept, which needs to be
     *     updated in the system.
     * @return A instance of type {@link Dept} containing the updated details.
     */
    @Instrument
    @Transactional
    public Dept updateDept(final Integer deptId, @Valid final UpdateDeptRequest payload) {
        // 1. Verify that the entity being updated truly exists.
        final DeptEntity matchingInstance = deptRepository.findByIdOrThrow(deptId);

        // 2. Transform the experience model to a persistence model and delegate to the save()
        // method.
        deptMapper.transform(payload, matchingInstance);

        // 3. Save the entity
        DeptService.LOGGER.debug("Saving the updated entity - DeptEntity");
        final DeptEntity updatedInstance = deptRepository.save(matchingInstance);

        // 4. Transform updated entity to output object
        return deptMapper.transform(updatedInstance);
    }

    /**
     * This method attempts to find a {@link DeptEntity} whose unique identifier matches the
     * provided identifier.
     *
     * @param deptId Unique identifier of Dept in the system, whose details have to be retrieved.
     * @return Matching entity of type {@link Dept} if found, else returns null.
     */
    @Instrument
    @Transactional(readOnly = true)
    public Dept findDept(final Integer deptId) {
        // 1. Find a matching entity and throw an exception if not found.
        final DeptEntity matchingInstance = deptRepository.findByIdOrThrow(deptId);

        // 2. Transform the matching entity to the desired output.
        return deptMapper.transform(matchingInstance);
    }

    /**
     * This method attempts to find instances of type DeptEntity based on the provided page
     * definition. If the page definition is null or contains invalid values, this method attempts
     * to return the data for the first page (i.e. page index is 0) with a default page size as 20.
     *
     * @return Returns a page of objects based on the provided page definition. Each object in the
     *     returned page is an instance of type {@link Dept}.
     */
    @Instrument
    @Transactional(readOnly = true)
    public Page<Dept> findAllDepts(final Pageable page) {
        // 1. Validate the provided pagination settings.
        final Pageable pageSettings = PageUtils.validateAndUpdatePaginationConfiguration(page);
        DeptService.LOGGER.debug(
                "Page settings: page number {}, page size {}",
                pageSettings.getPageNumber(),
                pageSettings.getPageSize());

        // 2. Delegate to the super class method to find the data (page settings are verified in
        // that method).
        final Page<DeptEntity> pageData = deptRepository.findAll(pageSettings);

        // 3. If the page has data, transform each element into target type.
        if (pageData.hasContent()) {
            final List<Dept> dataToReturn =
                    pageData.getContent().stream()
                            .map(deptMapper::transform)
                            .collect(Collectors.toList());

            return PageUtils.createPage(dataToReturn, pageSettings, pageData.getTotalElements());
        }

        // Return empty page.
        return PageUtils.emptyPage(pageSettings);
    }

    /**
     * This method attempts to delete an existing instance of type {@link DeptEntity} whose unique
     * identifier matches the provided identifier.
     *
     * @param deptId Unique identifier of Dept in the system, which needs to be deleted.
     * @return Unique identifier of the instance of type DeptEntity that was deleted.
     */
    @Instrument
    @Transactional
    public Integer deleteDept(final Integer deptId) {
        // 1. Delegate to our repository method to handle the deletion.
        return deptRepository.deleteOne(deptId);
    }
}
