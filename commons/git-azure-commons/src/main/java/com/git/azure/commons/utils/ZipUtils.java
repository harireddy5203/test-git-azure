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

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import lombok.extern.slf4j.Slf4j;

import com.git.azure.commons.error.CommonErrors;
import com.git.azure.commons.exception.ServiceException;

/**
 * Utility class that provides methods for creating zip files.
 *
 * @author Mahalingam Iyer
 */
@Slf4j
public class ZipUtils {
    /**
     * This method attempts to zip the contents in the specified folder using the provided file name.
     *
     * @param folder
     *         Folder whose contents have to be zipped.
     * @param zipFile
     *         Output file name of the zip file.
     *
     * @return Path that points to the output zip file.
     *
     * @throws IOException
     *         If any I/O errors are encountered.
     */
    public static Path zipFolder(final Path folder, final Path zipFile) throws IOException {
        ZipUtils.LOGGER.info("Zipping folder {}, zip file name {}", folder, zipFile);

        // Delete the zip archive if already exists.
        Files.deleteIfExists(zipFile);
        final Path zipFilePath = Files.createFile(zipFile);
        // Iterate the contents of the directory and add to the zip archive file.
        try (final ZipOutputStream serverCodeOutput = new ZipOutputStream(Files.newOutputStream(zipFilePath));
             final Stream<Path> paths = Files.walk(folder)) {
            paths.filter(path -> !Files.isDirectory(path)).forEach(path -> {
                final ZipEntry zipEntry = new ZipEntry(folder.relativize(path).toString());
                try {
                    serverCodeOutput.putNextEntry(zipEntry);
                    serverCodeOutput.write(Files.readAllBytes(path));
                    serverCodeOutput.closeEntry();
                } catch (IOException e) {
                    throw new ServiceException(CommonErrors.ZIP_EXCEPTION, e.getMessage());
                }
            });
        }

        ZipUtils.LOGGER.info("Zipped file - {}", zipFilePath);
        return zipFilePath;
    }

    /**
     * This method attempts to unzip the provided zip file to the specified destination directory.
     *
     * @param zipFile
     *         Zip file that needs to be unzipped.
     * @param destinationDirectory
     *         Destination directory where the input file needs to be unzipped.
     *
     * @throws IOException
     *         Whenever an exception is encountered during the unzipping process.
     */
    public static void unzipFile(final Path zipFile, final Path destinationDirectory) throws IOException {
        final byte[] buffer = new byte[1024];
        try (final ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(zipFile.toFile()))) {
            ZipEntry zipEntry;
            while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                final Path zipFileEntry = destinationDirectory.resolve(zipEntry.getName());
                // Get the parent of this entry, check if the folder exists. If it does not, create it.
                final Path parentPath = zipFileEntry.getParent();
                // Does the parent exist?
                if (!parentPath.toFile().exists()) {
                    Files.createDirectories(parentPath);
                }
                try (final FileOutputStream fos = new FileOutputStream(zipFileEntry.toFile())) {
                    int len;
                    while ((len = zipInputStream.read(buffer)) > 0) {
                        fos.write(buffer, 0, len);
                    }
                }
                // Close this entry
                zipInputStream.closeEntry();
            }
            // Close the last entry
            zipInputStream.closeEntry();
        }
    }
}
