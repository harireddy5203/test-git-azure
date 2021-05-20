/*
 * Copyright (c) 2020-2021 Innominds inc. All Rights Reserved. This software is
 * confidential and proprietary information of Innominds inc. You shall not disclose
 * Confidential Information and shall use it only in accordance with the terms
 *
 */
package com.git.azure.features.platform.web.api;

import com.git.azure.commons.data.utils.PageUtils;
import com.git.azure.commons.web.api.AbstractApi;
import com.git.azure.commons.web.configuration.properties.ApiDocumentationSettings;
import com.git.azure.features.platform.data.model.experience.test.CreateTestRequest;
import com.git.azure.features.platform.data.model.experience.test.Test;
import com.git.azure.features.platform.data.model.experience.test.UpdateTestRequest;
import com.git.azure.features.platform.web.service.TestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Implementation of APIs that provide CRUD (Create, Read, Update and Delete) functionality for
 * persistence models of type {@link
 * com.git.azure.features.platform.data.model.persistence.TestEntity}.
 *
 * @author Mahalingam Iyer
 */
@Slf4j
@RestController
public class TestApi extends AbstractApi {
    /** Tag for this API. */
    public static final String API_TAG = "Tests";

    /** Service implementation of type {@link TestService}. */
    private final TestService testService;

    /**
     * Constructor.
     *
     * @param testService Service instance of type {@link TestService}.
     */
    public TestApi(final TestService testService) {
        this.testService = testService;
    }

    /**
     * This API provides the capability to add a new instance of type {@link
     * com.git.azure.features.platform.data.model.persistence.TestEntity} into the system.
     *
     * @param payload Payload containing the details required to create an instance of type {@link
     *     com.git.azure.features.platform.data.model.persistence.TestEntity}.
     * @return Response of type {@link ResponseEntity} that wraps an instance of type {@link Test}.
     */
    @Operation(
            method = "createTest",
            summary = "Create a new Test.",
            description = "This API is used to create a new Test in the system.",
            tags = {TestApi.API_TAG},
            security = {
                @SecurityRequirement(
                        name =
                                ApiDocumentationSettings.ApiSecurityScheme
                                        .DEFAULT_SECURITY_SCHEME_NAME)
            })
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "201",
                        description = "Successfully created a new Test in the system.",
                        content = @Content),
                @ApiResponse(
                        responseCode = "403",
                        description = "You do not have permissions to perform this operation.",
                        content = @Content)
            })
    @PreAuthorize(value = "isAuthenticated()")
    @PostMapping(value = "/tests")
    public ResponseEntity<Test> createTest(@Valid @RequestBody final CreateTestRequest payload) {
        // Delegate to the service layer.
        final Test newInstance = testService.createTest(payload);

        // Build a response entity object and return it.
        return ResponseEntity.status(HttpStatus.CREATED).body(newInstance);
    }

    /**
     * This API provides the capability to update an existing instance of type {@link
     * com.git.azure.features.platform.data.model.persistence.TestEntity} in the system.
     *
     * @param testId Unique identifier of Test in the system, which needs to be updated.
     * @param payload Request payload containing the details of an existing Test, which needs to be
     *     updated in the system.
     * @return Response of type {@link ResponseEntity} that wraps an instance of type {@link Test}.
     */
    @Operation(
            method = "updateTest",
            summary = "Update an existing Test.",
            description = "This API is used to update an existing Test in the system.",
            tags = {TestApi.API_TAG},
            security = {
                @SecurityRequirement(
                        name =
                                ApiDocumentationSettings.ApiSecurityScheme
                                        .DEFAULT_SECURITY_SCHEME_NAME)
            })
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Successfully updated an existing Test in the system.",
                        content = @Content),
                @ApiResponse(
                        responseCode = "403",
                        description = "You do not have permissions to perform this operation.",
                        content = @Content)
            })
    @PreAuthorize(value = "isAuthenticated()")
    @PutMapping(value = "/tests/{testId}")
    public ResponseEntity<Test> updateTest(
            @PathVariable(name = "testId") final Integer testId,
            @Valid @RequestBody final UpdateTestRequest payload) {
        // Delegate to the service layer.
        final Test updatedInstance = testService.updateTest(testId, payload);

        // Build a response entity object and return it.
        return ResponseEntity.ok(updatedInstance);
    }

    /**
     * This API provides the capability to retrieve the details of an existing {@link
     * com.git.azure.features.platform.data.model.persistence.TestEntity} in the system.
     *
     * @param testId Unique identifier of Test in the system, whose details have to be retrieved.
     * @return Response of type {@link ResponseEntity} that wraps an instance of type {@link Test}.
     */
    @Operation(
            method = "findTest",
            summary = "Find an existing Test.",
            description = "This API is used to find an existing Test in the system.",
            tags = {TestApi.API_TAG},
            security = {
                @SecurityRequirement(
                        name =
                                ApiDocumentationSettings.ApiSecurityScheme
                                        .DEFAULT_SECURITY_SCHEME_NAME)
            })
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description =
                                "Successfully retrieved the details of an existing Test in the system.",
                        content = @Content),
                @ApiResponse(
                        responseCode = "403",
                        description = "You do not have permissions to perform this operation.",
                        content = @Content)
            })
    @PreAuthorize(value = "isAuthenticated()")
    @GetMapping(value = "/tests/{testId}")
    public ResponseEntity<Test> findTest(@PathVariable(name = "testId") final Integer testId) {
        // Delegate to the service layer.
        final Test matchingInstance = testService.findTest(testId);

        // Build a response entity object and return it.
        return ResponseEntity.ok(matchingInstance);
    }

    /**
     * This API provides the capability to retrieve all instances of type {@link
     * com.git.azure.features.platform.data.model.persistence.TestEntity} in the system in a
     * paginated manner.
     *
     * @param page Page number.
     * @param size Page size.
     * @return Response of type {@link ResponseEntity} that holds a page of instances of type Test
     *     based on the provided pagination settings.
     */
    @Operation(
            method = "findAllTests",
            summary = "Find all Tests.",
            description = "This API is used to find all Tests in the system.",
            tags = {TestApi.API_TAG},
            security = {
                @SecurityRequirement(
                        name =
                                ApiDocumentationSettings.ApiSecurityScheme
                                        .DEFAULT_SECURITY_SCHEME_NAME)
            })
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description =
                                "Successfully retrieved the Tests in the system based on the provided pagination settings.",
                        content = @Content),
                @ApiResponse(
                        responseCode = "403",
                        description = "You do not have permissions to perform this operation.",
                        content = @Content)
            })
    @PreAuthorize(value = "isAuthenticated()")
    @GetMapping(value = "/tests")
    public ResponseEntity<Page<Test>> findAllTests(
            @RequestParam(name = "page", required = false, defaultValue = "0") final Integer page,
            @RequestParam(name = "size", required = false, defaultValue = "20")
                    final Integer size) {
        // Delegate to the service layer.
        final Pageable pageSettings = PageUtils.createPaginationConfiguration(page, size);
        final Page<Test> matchingInstances = testService.findAllTests(pageSettings);

        // Build a response entity object and return it.
        return ResponseEntity.ok(matchingInstances);
    }

    /**
     * This API provides the capability to delete an existing instance of type {@link
     * com.git.azure.features.platform.data.model.persistence.TestEntity} in the system.
     *
     * @param testId Unique identifier of Test in the system, which needs to be deleted.
     * @return Response of type {@link ResponseEntity} that holds the unique identifier of the
     *     {@link com.git.azure.features.platform.data.model.persistence.TestEntity} that was
     *     deleted from the system.
     */
    @Operation(
            method = "deleteTest",
            summary = "Delete an existing Test.",
            description = "This API is used to delete an existing Test in the system.",
            tags = {TestApi.API_TAG},
            security = {
                @SecurityRequirement(
                        name =
                                ApiDocumentationSettings.ApiSecurityScheme
                                        .DEFAULT_SECURITY_SCHEME_NAME)
            })
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Successfully deleted an existing Test in the system.",
                        content = @Content),
                @ApiResponse(
                        responseCode = "403",
                        description = "You do not have permissions to perform this operation.",
                        content = @Content)
            })
    @PreAuthorize(value = "isAuthenticated()")
    @DeleteMapping(value = "/tests/{testId}")
    public ResponseEntity<Integer> deleteTest(@PathVariable(name = "testId") final Integer testId) {
        // Delegate to the service layer.
        final Integer deletedInstance = testService.deleteTest(testId);

        // Build a response entity object and return it.
        return ResponseEntity.ok(deletedInstance);
    }
}
