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

/**
 * Base contract that provides functionality for Time-based One Time Passwords (TOTP) including generation of secret
 * (different for each user), generating QR Code for registering in a virtual authenticator application (like Google
 * Authenticator), verifying the generated code, etc.
 *
 * @author Mahalingam Iyer
 */
public interface ITotpService {
    /**
     * This method attempts to generate a random secret, based on which the QR Code gets generated. This is typically
     * used in the MFA (Multi-Factor Authentication) flows.
     * <p>
     * When users are created in the system, if MFA is enabled, a secret (using this method) shall be generated and
     * persisted with the rest of the user data.
     * <p>
     * Once the user activates their account, a QR code is generated using the previously generated secret for the
     * respective user and displayed to the user (using the {@code getUriForImage} method) so that they can add it as a
     * new account within their virtual authenticator application (like Google Authenticator).
     * <p>
     * Now, when the user attempts to login to the system, if MFA is enabled, a text field is displayed for the user to
     * enter the verification code. This code is then verified using the {@code verifyCode} method.
     *
     * @return Random generated secret.
     */
    String generateSecret();

    /**
     * This method attempts to generate a QR Code using the provided secret.
     *
     * @param secret
     *         Secret for which the QR code needs to be generated.
     *
     * @return URI that displays the generated QR code based on the specified secret.
     */
    String getUriForImage(String secret);

    /**
     * This method attempts to verify the provided {@code} using the specified {@code secret}, which is the user's
     * secret and stored alongside user data.
     *
     * @param code
     *         Verification code that needs to be verified.
     * @param secret
     *         User's secret that was generated during user creation time.
     *
     * @return True if the verification code is correct, false otherwise.
     */
    boolean verifyCode(String code, String secret);
}
