/*
 * Copyright (c) 2020-2021 Innominds inc. All Rights Reserved. This software is
 * confidential and proprietary information of Innominds inc. You shall not disclose
 * Confidential Information and shall use it only in accordance with the terms
 *
 */
package com.git.azure.features.platform.web.service;

import com.git.azure.commons.data.utils.PageUtils;
import com.git.azure.commons.instrumentation.Instrument;
import com.git.azure.features.platform.data.mapper.TestMapper;
import com.git.azure.features.platform.data.model.experience.test.CreateTestRequest;
import com.git.azure.features.platform.data.model.experience.test.Test;
import com.git.azure.features.platform.data.model.experience.test.UpdateTestRequest;
import com.git.azure.features.platform.data.model.persistence.TestEntity;
import com.git.azure.features.platform.data.repository.TestRepository;
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
 * entities of type {@link TestEntity}.
 *
 * @author Mahalingam Iyer
 */
@Slf4j
@Validated
@Service
public class TestService {
    /** Repository implementation of type {@link TestRepository}. */
    private final TestRepository testRepository;

    /** Mapper implementation of type {@link TestMapper} to transform between different types. */
    private final TestMapper testMapper;

    /**
     * Constructor.
     *
     * @param testRepository Repository instance of type {@link TestRepository}.
     * @param testMapper Mapper instance of type {@link TestMapper}.
     */
    public TestService(final TestRepository testRepository, final TestMapper testMapper) {
        this.testRepository = testRepository;
        this.testMapper = testMapper;
    }

    /**
     * This method attempts to create an instance of type {@link TestEntity} in the system based on
     * the provided payload.
     *
     * @param payload Payload containing the details required to create an instance of type {@link
     *     TestEntity}.
     * @return An experience model of type {@link Test} that represents the newly created entity of
     *     type {@link TestEntity}.
     */
    @Instrument
    @Transactional
    public Test createTest(@Valid final CreateTestRequest payload) {
        // 1. Transform the experience model to a persistence model.
        final TestEntity testEntity = testMapper.transform(payload);

        // 2. Save the entity.
        TestService.LOGGER.debug("Saving a new instance of type - TestEntity");
        final TestEntity newInstance = testRepository.save(testEntity);

        // 3. Transform the created entity to an experience model and return it.
        return testMapper.transform(newInstance);
    }

    /**
     * This method attempts to update an existing instance of type {@link TestEntity} using the
     * details from the provided input, which is an instance of type {@link UpdateTestRequest}.
     *
     * @param testId Unique identifier of Test in the system, which needs to be updated.
     * @param payload Request payload containing the details of an existing Test, which needs to be
     *     updated in the system.
     * @return A instance of type {@link Test} containing the updated details.
     */
    @Instrument
    @Transactional
    public Test updateTest(final Integer testId, @Valid final UpdateTestRequest payload) {
        // 1. Verify that the entity being updated truly exists.
        final TestEntity matchingInstance = testRepository.findByIdOrThrow(testId);

        // 2. Transform the experience model to a persistence model and delegate to the save()
        // method.
        testMapper.transform(payload, matchingInstance);

        // 3. Save the entity
        TestService.LOGGER.debug("Saving the updated entity - TestEntity");
        final TestEntity updatedInstance = testRepository.save(matchingInstance);

        // 4. Transform updated entity to output object
        return testMapper.transform(updatedInstance);
    }

    /**
     * This method attempts to find a {@link TestEntity} whose unique identifier matches the
     * provided identifier.
     *
     * @param testId Unique identifier of Test in the system, whose details have to be retrieved.
     * @return Matching entity of type {@link Test} if found, else returns null.
     */
    @Instrument
    @Transactional(readOnly = true)
    public Test findTest(final Integer testId) {
        // 1. Find a matching entity and throw an exception if not found.
        final TestEntity matchingInstance = testRepository.findByIdOrThrow(testId);

        // 2. Transform the matching entity to the desired output.
        return testMapper.transform(matchingInstance);
    }

    /**
     * This method attempts to find instances of type TestEntity based on the provided page
     * definition. If the page definition is null or contains invalid values, this method attempts
     * to return the data for the first page (i.e. page index is 0) with a default page size as 20.
     *
     * @return Returns a page of objects based on the provided page definition. Each object in the
     *     returned page is an instance of type {@link Test}.
     */
    @Instrument
    @Transactional(readOnly = true)
    public Page<Test> findAllTests(final Pageable page) {
        // 1. Validate the provided pagination settings.
        final Pageable pageSettings = PageUtils.validateAndUpdatePaginationConfiguration(page);
        TestService.LOGGER.debug(
                "Page settings: page number {}, page size {}",
                pageSettings.getPageNumber(),
                pageSettings.getPageSize());

        // 2. Delegate to the super class method to find the data (page settings are verified in
        // that method).
        final Page<TestEntity> pageData = testRepository.findAll(pageSettings);

        // 3. If the page has data, transform each element into target type.
        if (pageData.hasContent()) {
            final List<Test> dataToReturn =
                    pageData.getContent().stream()
                            .map(testMapper::transform)
                            .collect(Collectors.toList());

            return PageUtils.createPage(dataToReturn, pageSettings, pageData.getTotalElements());
        }

        // Return empty page.
        return PageUtils.emptyPage(pageSettings);
    }

    /**
     * This method attempts to delete an existing instance of type {@link TestEntity} whose unique
     * identifier matches the provided identifier.
     *
     * @param testId Unique identifier of Test in the system, which needs to be deleted.
     * @return Unique identifier of the instance of type TestEntity that was deleted.
     */
    @Instrument
    @Transactional
    public Integer deleteTest(final Integer testId) {
        // 1. Delegate to our repository method to handle the deletion.
        return testRepository.deleteOne(testId);
    }
}
