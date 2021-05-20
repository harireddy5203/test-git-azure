/*
 * Copyright (c) 2021 REPLACE_CUSTOMER_NAME. All rights reserved.
 *
 * This file is part of test-git-azure.
 *
 * test-git-azure project and associated code cannot be copied
 * and/or distributed without a written permission of REPLACE_CUSTOMER_NAME,
 * and/or its subsidiaries.
 */
package com.git.azure.security.mfa.service;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

import dev.samstevens.totp.code.CodeGenerator;
import dev.samstevens.totp.code.DefaultCodeGenerator;
import dev.samstevens.totp.code.DefaultCodeVerifier;
import dev.samstevens.totp.code.HashingAlgorithm;
import dev.samstevens.totp.exceptions.QrGenerationException;
import dev.samstevens.totp.qr.QrData;
import dev.samstevens.totp.qr.QrGenerator;
import dev.samstevens.totp.qr.ZxingPngQrGenerator;
import dev.samstevens.totp.secret.DefaultSecretGenerator;
import dev.samstevens.totp.time.SystemTimeProvider;
import dev.samstevens.totp.util.Utils;

import com.git.azure.commons.exception.ServiceException;
import com.git.azure.security.mfa.configuration.properties.TotpProperties;
import com.git.azure.security.mfa.error.MFAErrors;

/**
 * Base implementation for {@link ITotpService} contract.
 * <p>
 * Credits: Amr Khaled (https://medium.com/javarevisited/spring-boot-two-factor-authentication-78e00aa10176).
 *
 * @author Mahalingam Iyer
 */
@Slf4j
@Service("totpService")
public class TotpService implements ITotpService {
    /** Algorithm to be used for QR code generation & token verification. */
    private static final HashingAlgorithm HASHING_ALGORITHM = HashingAlgorithm.SHA512;

    /** Configuration settings for TOTP. */
    private final TotpProperties totpProperties;

    /**
     * Constructor.
     *
     * @param totpProperties
     *         Configuration properties instance of type {@link TotpProperties}.
     */
    public TotpService(final TotpProperties totpProperties) {
        this.totpProperties = totpProperties;
    }

    @Override
    public String generateSecret() {
        return new DefaultSecretGenerator().generate();
    }

    @Override
    public String getUriForImage(final String secret) {
        final TotpProperties.TotpSettings totpSettings = totpProperties.getTotp();
        final TotpProperties.QRCodeSettings qrcode = totpSettings.getQrcode();

        // Build the QrData object, which will be used in QR Code generation.
        final QrData data = new QrData.Builder()
                .label(qrcode.getApplicationName())
                .secret(secret)
                .issuer(qrcode.getIssuer())
                .algorithm(TotpService.HASHING_ALGORITHM)
                .digits(totpSettings.getDigits())
                .period(totpSettings.getTimePeriodInSeconds())
                .build();

        final QrGenerator qrCodeGenerator = new ZxingPngQrGenerator();
        byte[] imageData;

        try {
            imageData = qrCodeGenerator.generate(data);
        } catch (final QrGenerationException e) {
            TotpService.LOGGER.error(e.getMessage(), e);
            throw ServiceException.instance(MFAErrors.FAILED_TO_GENERATE_QRCODE);
        }

        return Utils.getDataUriForImage(imageData, qrCodeGenerator.getImageMimeType());
    }

    @Override
    public boolean verifyCode(final String code, final String secret) {
        final TotpProperties.TotpSettings totpSettings = totpProperties.getTotp();

        final CodeGenerator codeGenerator = new DefaultCodeGenerator(TotpService.HASHING_ALGORITHM, totpSettings.getDigits());
        final DefaultCodeVerifier verifier = new DefaultCodeVerifier(codeGenerator, new SystemTimeProvider());
        verifier.setTimePeriod(totpSettings.getTimePeriodInSeconds());
        verifier.setAllowedTimePeriodDiscrepancy(totpSettings.getAllowedTimePeriodDiscrepancy());

        return verifier.isValidCode(secret, code);
    }
}
