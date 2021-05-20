/*
 * Copyright (c) 2021 REPLACE_CUSTOMER_NAME. All rights reserved.
 *
 * This file is part of test-git-azure.
 *
 * test-git-azure project and associated code cannot be copied
 * and/or distributed without a written permission of REPLACE_CUSTOMER_NAME,
 * and/or its subsidiaries.
 */
package com.git.azure.security.mfa.configuration.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

/**
 * Properties implementation to capture the properties that are meant for configuring Multi-Factor authentication.
 *
 * @author Mahalingam Iyer
 */
@Data
@ConfigurationProperties(prefix = "com.git.azure.configuration.security.mfa")
public class TotpProperties {
    /** TOTP settings. */
    private TotpSettings totp = new TotpSettings();

    /**
     * Class that holds the configuration details for OTP (One Time Password).
     */
    @Data
    public static class TotpSettings {
        /** Number of digits in the verification code. */
        private Integer digits = 6;

        /** Time period in seconds before a new verification code is generated. */
        private Integer timePeriodInSeconds = 30;

        /** Allow codes valid for N time-periods before/after to pass as valid. */
        private Integer allowedTimePeriodDiscrepancy = 2;

        /** QRCodeSettings configuration. */
        private QRCodeSettings qrcode = new QRCodeSettings();
    }

    /**
     * Class that holds the QR code Configuration settings.
     */
    @Data
    public static class QRCodeSettings {
        /** Name of the application. */
        private String applicationName;

        /** QR Code issuer. */
        private String issuer = "REPLACE_CUSTOMER_NAME";
    }
}
