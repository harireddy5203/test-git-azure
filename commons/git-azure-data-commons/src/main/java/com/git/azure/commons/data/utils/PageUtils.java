/*
 * Copyright (c) 2021 REPLACE_CUSTOMER_NAME. All rights reserved.
 *
 * This file is part of test-git-azure.
 *
 * test-git-azure project and associated code cannot be copied
 * and/or distributed without a written permission of REPLACE_CUSTOMER_NAME,
 * and/or its subsidiaries.
 */
package com.git.azure.commons.data.utils;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import lombok.extern.slf4j.Slf4j;

/**
 * Utility class that provides helper methods to deal with Page objects.
 *
 * @author Mahalingam Iyer
 */
@Slf4j
public final class PageUtils {
    /** Default page number. */
    public static final int DEFAULT_PAGE_NUMBER = 0;

    /** Default page number. */
    public static final String REQUEST_PARAM_DEFAULT_PAGE_NUMBER = "0";

    /** Default page size i.e. number of records to return per page. */
    public static final int DEFAULT_PAGE_SIZE = 20;

    /** Default page size i.e. number of records to return per page. */
    public static final String REQUEST_PARAM_DEFAULT_PAGE_SIZE = "20";

    /** Maximum page size i.e. maximum number of records to return per page. */
    public static final int MAX_PAGE_SIZE = 100;

    /**
     * Private constructor.
     */
    private PageUtils() {
        // Prevent creation of an instance of this class.
        throw new IllegalStateException("Cannot create an instance of this object.");
    }

    /**
     * Creates and returns an empty page preserving the pagination settings that was provided.
     *
     * @param pageRequest
     *         Page request object containing the pagination settings
     *
     * @return Page object of type {@link Page}
     */
    public static <X> Page<X> emptyPage(final Pageable pageRequest) {
        return PageUtils.createPage(Collections.emptyList(), pageRequest, 0);
    }

    /**
     * Creates and returns a page of records using the provided data.
     *
     * @param records
     *         Collection of records that are wrapped in the {@link Page} object.
     * @param pageRequest
     *         Page request object containing the pagination settings
     * @param totalRecords
     *         Total number of records
     *
     * @return Page object of type {@link Page}
     */
    public static <X> Page<X> createPage(final List<X> records, final Pageable pageRequest, final long totalRecords) {
        return new PageImpl<>(records, pageRequest, totalRecords);
    }

    /**
     * This method attempts to create a pagination configuration object of type {@link Pageable}.
     *
     * @param pageNumber
     *         Page number and defaulted to {@code PageUtils.DEFAULT_PAGE_NUMBER} if the value is invalid.
     * @param pageSize
     *         Page size and defaulted to {@code PageUtils.DEFAULT_PAGE_SIZE} if the value is invalid.
     *
     * @return Pagination configuration instance of type {@link Pageable}.
     */
    public static Pageable createPaginationConfiguration(final int pageNumber, final int pageSize) {
        final int pageNumberToUse = pageNumber < 0
                ? PageUtils.defaultPageNumber()
                : pageNumber;

        final int pageSizeToUse = pageSize <= 0 || pageSize > PageUtils.maximumPageSize()
                ? PageUtils.defaultPageSize()
                : pageSize;

        return PageRequest.of(pageNumberToUse, pageSizeToUse);
    }

    /**
     * This method attempts to validate the provided page definition. If the validation passes, the same page definition
     * is returned else a new page definition with the default settings is returned i.e. page-number is defaulted to
     * {@code PageUtils.DEFAULT_PAGE_NUMBER} and page-size to {@code PageUtils.DEFAULT_PAGE_SIZE}.
     *
     * @param page
     *         Page definition of type {@link Pageable} that needs to be validated.
     *
     * @return Validated page definition instance of type {@link Pageable}.
     */
    public static Pageable validateAndUpdatePaginationConfiguration(final Pageable page) {
        if (Objects.isNull(page) || page.getPageNumber() < 0 || page.getPageSize() <= 0 || page.getPageSize() > PageUtils.maximumPageSize()) {
            PageUtils.LOGGER.warn("Invalid page settings provided. Using the defaults.");
            return PageRequest.of(PageUtils.defaultPageNumber(), PageUtils.defaultPageSize());
        }
        return page;
    }

    /**
     * This method returns the default page number ({@code PageUtils.DEFAULT_PAGE_NUMBER}) and is used in situations
     * where the pagination settings are invalid.
     *
     * @return Default page number ({@code PageUtils.DEFAULT_PAGE_NUMBER}).
     */
    public static int defaultPageNumber() {
        return PageUtils.DEFAULT_PAGE_NUMBER;
    }

    /**
     * This method returns the default page size ({@code PageUtils.DEFAULT_PAGE_SIZE}) and is used in situations where
     * the pagination settings are invalid.
     *
     * @return Default page size ({@code PageUtils.DEFAULT_PAGE_SIZE}).
     */
    public static int defaultPageSize() {
        return PageUtils.DEFAULT_PAGE_SIZE;
    }

    /**
     * This method returns the permitted maximum page size({@code PageUtils.MAX_PAGE_SIZE}) and is used in situations
     * where the pagination settings are invalid.
     *
     * @return Maximum page size ({@code PageUtils.MAX_PAGE_SIZE}).
     */
    public static int maximumPageSize() {
        return PageUtils.MAX_PAGE_SIZE;
    }
}

