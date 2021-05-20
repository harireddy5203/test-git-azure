/*
 * Copyright (c) 2021 REPLACE_CUSTOMER_NAME. All rights reserved.
 *
 * This file is part of test-git-azure.
 *
 * test-git-azure project and associated code cannot be copied
 * and/or distributed without a written permission of REPLACE_CUSTOMER_NAME,
 * and/or its subsidiaries.
 */
package com.git.azure.commons.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * Utility class that provides helper methods for manipulation of XML files.
 *
 * @author Mahalingam Iyer
 */
@Slf4j
public final class XmlUtils {
    /**
     * Constructor.
     */
    private XmlUtils() {
        throw new IllegalStateException("Cannot create instance of this class.");
    }

    /**
     * This method attempts to replace the provided pattern ({@code inputString} parameter) with the replacement string
     * ({@code replaceString} parameter) in the specified file ({@code file} parameter).
     *
     * @param fileName
     *         Absolute path to the file containing the input string that needs to be replaced.
     * @param findString
     *         Input string that needs to be replaced.
     * @param replaceWith
     *         Replacement string that will be used as the replacement string for the {@code inputString} parameter.
     *
     * @throws IOException
     *         When I/O errors are encountered.
     */
    public static void replaceInFile(final String fileName, final String findString,
                                     final String replaceWith) throws IOException {
        if (StringUtils.isAnyBlank(fileName, findString, replaceWith)) {
            XmlUtils.LOGGER.warn("Skipping pattern replacement as one or more of the provided arguments are null / empty");
            return;
        }

        final Path filePath = Paths.get(fileName);
        try (final Stream<String> stream = Files.lines(filePath)) {
            final List<String> replacedStrings = stream.map(line -> line.replaceAll(findString, replaceWith)).collect(Collectors.toList());
            Files.write(filePath, replacedStrings);
        }
    }
}
