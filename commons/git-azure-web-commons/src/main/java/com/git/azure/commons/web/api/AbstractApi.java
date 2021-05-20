/*
 * Copyright (c) 2021 REPLACE_CUSTOMER_NAME. All rights reserved.
 *
 * This file is part of test-git-azure.
 *
 * test-git-azure project and associated code cannot be copied
 * and/or distributed without a written permission of REPLACE_CUSTOMER_NAME,
 * and/or its subsidiaries.
 */
package com.git.azure.commons.web.api;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;

import lombok.extern.slf4j.Slf4j;

import com.git.azure.commons.exception.ServiceException;
import com.git.azure.commons.web.api.error.ApiErrors;
import com.git.azure.commons.web.utils.HttpUtils;
import com.git.azure.security.utils.AuthenticationUtils;

/**
 * Abstract Api class that exposes the common functionality and reusable methods of all the APIs.
 *
 * @author Mahalingam Iyer
 */
@Slf4j
public abstract class AbstractApi {
    /**
     * This method attempts to return the ip address of the client by looking for specific headers in the provided
     * request.
     *
     * @param request
     *         Http request of type {@link HttpServletRequest}.
     *
     * @return IP address of the caller.
     */
    protected String getClientIpAddress(final HttpServletRequest request) {
        return HttpUtils.getClientIpAddress(request);
    }

    /**
     * This method attempts to transfer the provided multipart file to the specified destination location.
     *
     * @param file
     *         Multipart file that needs to be uploaded to the specified destination.
     * @param destination
     *         Destination path (includes the file name).
     */
    protected void transferFile(final MultipartFile file, final Path destination) {
        try {
            Files.createDirectories(destination.getParent());
            file.transferTo(destination.toFile());
        } catch (final IOException e) {
            AbstractApi.LOGGER.error(e.getMessage(), e);
            throw ServiceException.instance(ApiErrors.FAILED_TO_TRANSFER_FILE, file.getOriginalFilename(), destination.toString());
        }
    }

    /**
     * This method transforms the file instance of type {@link File} to an instance of type {@link InputStreamResource}
     * and enables it to be downloaded on the front-end.
     *
     * @param fileToDownload
     *         Instance of the File object
     * @param fileName
     *         Name of the file pertaining to the picture.
     *
     * @return Instance of type {@link ResponseEntity} that holds an {@link InputStreamResource} representing the file
     * to be downloaded on the client side.
     */
    protected ResponseEntity<InputStreamResource> responseForFileDownload(final File fileToDownload,
                                                                          final String fileName) {
        InputStreamResource resource = null;
        if (Objects.nonNull(fileToDownload)) {
            try {
                if (fileToDownload.isFile() && fileToDownload.exists()) {
                    resource = new InputStreamResource(new FileInputStream(fileToDownload));
                } else {
                    AbstractApi.LOGGER.warn("Unable to download the file [{}]. Either it does not exist or does not have permissions", fileName);
                }
            } catch (final Exception ex) {
                AbstractApi.LOGGER.error(ex.getMessage(), ex);
                throw ServiceException.instance(ApiErrors.FAILED_TO_DOWNLOAD_FILE);
            }
        }
        if (Objects.isNull(resource)) {
            return ResponseEntity.notFound()
                    .headers(initializeHeadersForFileDownload(StringUtils.EMPTY))
                    .build();
        }

        return ResponseEntity.ok()
                .headers(initializeHeadersForFileDownload(fileName))
                .contentLength(fileToDownload.length())
                .contentType(MediaType.parseMediaType(MediaType.APPLICATION_OCTET_STREAM_VALUE))
                .body(resource);
    }

    /**
     * This method creates an instance of type {@link HttpHeaders} with basic initializations and returns it.
     *
     * @return Instance of type {@link HttpHeaders}.
     */
    protected HttpHeaders initializeHeadersForFileDownload(final String fileName) {
        return HttpUtils.generateHeadersForFileDownload(fileName);
    }

    /**
     * This method returns the principal contained within the authentication object and adapts to the specified type if
     * possible. If the adaptation fails, this method returns null.
     *
     * @param authentication
     *         Authentication object of type {@link Authentication} and holds the details of the authenticated user. If
     *         there is no authenticated user, this will point to anonymous user.
     * @param userPrincipalType
     *         Target type of the user principal that needs to be adapted to
     * @param <T>
     *         Target type
     *
     * @return User principal adapted to the target type. If the adaptation fails, this method returns null
     */
    protected <T> T getPrincipal(final Authentication authentication, final Class<T> userPrincipalType) {
        return AuthenticationUtils.getPrincipal(authentication, userPrincipalType);
    }
}
