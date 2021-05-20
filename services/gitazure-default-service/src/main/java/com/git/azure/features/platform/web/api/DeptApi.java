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
import com.git.azure.features.platform.data.model.experience.dept.CreateDeptRequest;
import com.git.azure.features.platform.data.model.experience.dept.Dept;
import com.git.azure.features.platform.data.model.experience.dept.UpdateDeptRequest;
import com.git.azure.features.platform.web.service.DeptService;
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
 * com.git.azure.features.platform.data.model.persistence.DeptEntity}.
 *
 * @author Mahalingam Iyer
 */
@Slf4j
@RestController
public class DeptApi extends AbstractApi {
    /** Tag for this API. */
    public static final String API_TAG = "Depts";

    /** Service implementation of type {@link DeptService}. */
    private final DeptService deptService;

    /**
     * Constructor.
     *
     * @param deptService Service instance of type {@link DeptService}.
     */
    public DeptApi(final DeptService deptService) {
        this.deptService = deptService;
    }

    /**
     * This API provides the capability to add a new instance of type {@link
     * com.git.azure.features.platform.data.model.persistence.DeptEntity} into the system.
     *
     * @param payload Payload containing the details required to create an instance of type {@link
     *     com.git.azure.features.platform.data.model.persistence.DeptEntity}.
     * @return Response of type {@link ResponseEntity} that wraps an instance of type {@link Dept}.
     */
    @Operation(
            method = "createDept",
            summary = "Create a new Dept.",
            description = "This API is used to create a new Dept in the system.",
            tags = {DeptApi.API_TAG},
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
                        description = "Successfully created a new Dept in the system.",
                        content = @Content),
                @ApiResponse(
                        responseCode = "403",
                        description = "You do not have permissions to perform this operation.",
                        content = @Content)
            })
    @PreAuthorize(value = "isAuthenticated()")
    @PostMapping(value = "/depts")
    public ResponseEntity<Dept> createDept(@Valid @RequestBody final CreateDeptRequest payload) {
        // Delegate to the service layer.
        final Dept newInstance = deptService.createDept(payload);

        // Build a response entity object and return it.
        return ResponseEntity.status(HttpStatus.CREATED).body(newInstance);
    }

    /**
     * This API provides the capability to update an existing instance of type {@link
     * com.git.azure.features.platform.data.model.persistence.DeptEntity} in the system.
     *
     * @param deptId Unique identifier of Dept in the system, which needs to be updated.
     * @param payload Request payload containing the details of an existing Dept, which needs to be
     *     updated in the system.
     * @return Response of type {@link ResponseEntity} that wraps an instance of type {@link Dept}.
     */
    @Operation(
            method = "updateDept",
            summary = "Update an existing Dept.",
            description = "This API is used to update an existing Dept in the system.",
            tags = {DeptApi.API_TAG},
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
                        description = "Successfully updated an existing Dept in the system.",
                        content = @Content),
                @ApiResponse(
                        responseCode = "403",
                        description = "You do not have permissions to perform this operation.",
                        content = @Content)
            })
    @PreAuthorize(value = "isAuthenticated()")
    @PutMapping(value = "/depts/{deptId}")
    public ResponseEntity<Dept> updateDept(
            @PathVariable(name = "deptId") final Integer deptId,
            @Valid @RequestBody final UpdateDeptRequest payload) {
        // Delegate to the service layer.
        final Dept updatedInstance = deptService.updateDept(deptId, payload);

        // Build a response entity object and return it.
        return ResponseEntity.ok(updatedInstance);
    }

    /**
     * This API provides the capability to retrieve the details of an existing {@link
     * com.git.azure.features.platform.data.model.persistence.DeptEntity} in the system.
     *
     * @param deptId Unique identifier of Dept in the system, whose details have to be retrieved.
     * @return Response of type {@link ResponseEntity} that wraps an instance of type {@link Dept}.
     */
    @Operation(
            method = "findDept",
            summary = "Find an existing Dept.",
            description = "This API is used to find an existing Dept in the system.",
            tags = {DeptApi.API_TAG},
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
                                "Successfully retrieved the details of an existing Dept in the system.",
                        content = @Content),
                @ApiResponse(
                        responseCode = "403",
                        description = "You do not have permissions to perform this operation.",
                        content = @Content)
            })
    @PreAuthorize(value = "isAuthenticated()")
    @GetMapping(value = "/depts/{deptId}")
    public ResponseEntity<Dept> findDept(@PathVariable(name = "deptId") final Integer deptId) {
        // Delegate to the service layer.
        final Dept matchingInstance = deptService.findDept(deptId);

        // Build a response entity object and return it.
        return ResponseEntity.ok(matchingInstance);
    }

    /**
     * This API provides the capability to retrieve all instances of type {@link
     * com.git.azure.features.platform.data.model.persistence.DeptEntity} in the system in a
     * paginated manner.
     *
     * @param page Page number.
     * @param size Page size.
     * @return Response of type {@link ResponseEntity} that holds a page of instances of type Dept
     *     based on the provided pagination settings.
     */
    @Operation(
            method = "findAllDepts",
            summary = "Find all Depts.",
            description = "This API is used to find all Depts in the system.",
            tags = {DeptApi.API_TAG},
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
                                "Successfully retrieved the Depts in the system based on the provided pagination settings.",
                        content = @Content),
                @ApiResponse(
                        responseCode = "403",
                        description = "You do not have permissions to perform this operation.",
                        content = @Content)
            })
    @PreAuthorize(value = "isAuthenticated()")
    @GetMapping(value = "/depts")
    public ResponseEntity<Page<Dept>> findAllDepts(
            @RequestParam(name = "page", required = false, defaultValue = "0") final Integer page,
            @RequestParam(name = "size", required = false, defaultValue = "20")
                    final Integer size) {
        // Delegate to the service layer.
        final Pageable pageSettings = PageUtils.createPaginationConfiguration(page, size);
        final Page<Dept> matchingInstances = deptService.findAllDepts(pageSettings);

        // Build a response entity object and return it.
        return ResponseEntity.ok(matchingInstances);
    }

    /**
     * This API provides the capability to delete an existing instance of type {@link
     * com.git.azure.features.platform.data.model.persistence.DeptEntity} in the system.
     *
     * @param deptId Unique identifier of Dept in the system, which needs to be deleted.
     * @return Response of type {@link ResponseEntity} that holds the unique identifier of the
     *     {@link com.git.azure.features.platform.data.model.persistence.DeptEntity} that was
     *     deleted from the system.
     */
    @Operation(
            method = "deleteDept",
            summary = "Delete an existing Dept.",
            description = "This API is used to delete an existing Dept in the system.",
            tags = {DeptApi.API_TAG},
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
                        description = "Successfully deleted an existing Dept in the system.",
                        content = @Content),
                @ApiResponse(
                        responseCode = "403",
                        description = "You do not have permissions to perform this operation.",
                        content = @Content)
            })
    @PreAuthorize(value = "isAuthenticated()")
    @DeleteMapping(value = "/depts/{deptId}")
    public ResponseEntity<Integer> deleteDept(@PathVariable(name = "deptId") final Integer deptId) {
        // Delegate to the service layer.
        final Integer deletedInstance = deptService.deleteDept(deptId);

        // Build a response entity object and return it.
        return ResponseEntity.ok(deletedInstance);
    }
}
