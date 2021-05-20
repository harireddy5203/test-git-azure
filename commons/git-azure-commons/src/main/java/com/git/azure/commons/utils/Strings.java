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

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Base64;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import lombok.extern.slf4j.Slf4j;

import com.git.azure.commons.error.CommonErrors;
import com.git.azure.commons.exception.ServiceException;

/**
 * Utility class that contains commonly used functions.
 *
 * @author Mahalingam Iyer
 */
@Slf4j
public final class Strings {
    /** Sample masked string. */
    private static final String SAMPLE_MASKED_STRING = "xxxxxxxx";

    /** Email masking template. */
    private static final String MASK_EMAIL_ADDRESS = "{0}@xxxx.xxx";

    /** Basic authentication format. */
    private static final String BASIC_AUTHENTICATION_FORMAT = "{0}:{1}";

    /** Secret key. */
    private static final byte[] SECRET = "azABCDEbcdsNJKLViopGjkeflmntuvwxyFHI67MWXYqghrZ01234OPQSTU589".getBytes(StandardCharsets.UTF_8);

    /** Encryption algorithm to  be used. */
    private static final byte[] ENCRYPTION_ALGORITHM = "AES/CBC/PKCS5Padding".getBytes(StandardCharsets.UTF_8);

    /**
     * Private constructor.
     */
    private Strings() {
        // Do not allow creation of objects of this utility class
        throw new IllegalStateException("Cannot create instances of this class");
    }

    /**
     * This method returns a sample masked string which contains a sequence of "x" characters.
     *
     * @return Sample masked string.
     */
    public static String sampleMaskedString() {
        return Strings.SAMPLE_MASKED_STRING;
    }

    /**
     * Masks the provided email address by retaining the text before the '@' symbol and masking rest of it.
     *
     * @param email
     *         Email address to be masked.
     *
     * @return Masked email address. This method returns a null if the provided input is null.
     */
    public static String maskEmail(final String email) {
        final int indexOfAtSymbol = StringUtils.isBlank(email)
                ? -1
                : email.indexOf('@');
        if (indexOfAtSymbol <= 0) {
            return email;
        }
        final String textBeforeAt = email.substring(0, indexOfAtSymbol);
        return MessageFormat.format(Strings.MASK_EMAIL_ADDRESS, textBeforeAt);
    }

    /**
     * Masks the provided string. Default is to mask the characters between 2 and length - 2 of the input.
     *
     * @param inputToBeMasked
     *         Input that needs to be masked.
     *
     * @return Masked string.
     */
    public static String maskString(final String inputToBeMasked) {
        if (StringUtils.isBlank(inputToBeMasked)) {
            return inputToBeMasked;
        }
        return Strings.maskString(inputToBeMasked, 2, inputToBeMasked.length() - 2);
    }

    /**
     * Replaces the characters between {@code fromIndex} and {@code toIndex} with '*".
     *
     * @param inputToBeMasked
     *         Input containing the range of characters to be masked.
     * @param fromIndex
     *         From index and should be >= 0 and < input.length() - 1. This is inclusive.
     * @param toIndex
     *         To index and should be > 0 and < input.length. This is also inclusive.
     *
     * @return Masked string.
     */
    public static String maskString(final String inputToBeMasked, final int fromIndex, final int toIndex) {
        if (StringUtils.isBlank(inputToBeMasked)) {
            return inputToBeMasked;
        }
        final int inputLength = inputToBeMasked.length();
        if (inputLength > 6) {
            return Strings.replaceChars(inputToBeMasked, fromIndex, toIndex, '*');
        }
        return Strings.replaceChars(inputToBeMasked, 1, inputToBeMasked.length(), '*');
    }

    /**
     * This method performs a Base64 encoding of the provided bytes.
     *
     * @param bytesToEncode
     *         Array of bytes to be Base64 encoded.
     *
     * @return Base64 encoded bytes.
     */
    public static byte[] base64Encode(final byte[] bytesToEncode) {
        if (Objects.isNull(bytesToEncode) || bytesToEncode.length == 0) {
            return new byte[0];
        }
        return Base64.getEncoder().encode(bytesToEncode);
    }

    /**
     * This method performs a Base64 decoding of the provided bytes.
     *
     * @param bytesToDecode
     *         Array of bytes to be Base64 decoded.
     *
     * @return Base64 decoded bytes.
     */
    public static byte[] base64Decode(final byte[] bytesToDecode) {
        if (Objects.isNull(bytesToDecode) || bytesToDecode.length == 0) {
            return new byte[0];
        }
        return Base64.getDecoder().decode(bytesToDecode);
    }

    /**
     * Performs a Base64 encode of the provided data in the basic authentication format i.e. first formulates the data
     * based on [username]:[password] format and then performs a Base64 encode on this data.
     *
     * @param userName
     *         User name.
     * @param password
     *         Password.
     *
     * @return Base64 encoding string that can be used as the value in the Authorization Header (for Basic
     * authentication)
     */
    public static byte[] basicAuthenticationBase64Encode(final String userName, final String password) {
        return Strings.base64Encode(MessageFormat.format(Strings.BASIC_AUTHENTICATION_FORMAT, userName, password).getBytes());
    }

    /**
     * This method attempts to encrypt the provided input string using the provided secret.
     *
     * @param stringToEncrypt
     *         Input string that needs to be encrypted.
     *
     * @return Encrypted string.
     *
     * @throws ServiceException
     *         Whenever any exceptions are encountered.
     */
    public static String encrypt(final String stringToEncrypt) {
        return Strings.encrypt(stringToEncrypt, Strings.SECRET);
    }

    /**
     * This method attempts to encrypt the provided input string using the provided secret.
     *
     * @param stringToEncrypt
     *         Input string that needs to be encrypted.
     * @param secret
     *         Secret key to be used for encryption.
     *
     * @return Encrypted string.
     *
     * @throws ServiceException
     *         Whenever any exceptions are encountered.
     */
    public static String encrypt(final String stringToEncrypt, final byte[] secret) {
        // Credits go to the author of the below article:
        // https://howtodoinjava.com/security/java-aes-encryption-example/

        try {
            final Cipher cipher = Cipher.getInstance(new String(Strings.ENCRYPTION_ALGORITHM));
            cipher.init(Cipher.ENCRYPT_MODE, Strings.generateSecretKeySpec(secret), new IvParameterSpec(new byte[16]));
            return Base64.getEncoder().encodeToString(cipher.doFinal(stringToEncrypt.getBytes(StandardCharsets.UTF_8)));
        } catch (final NoSuchAlgorithmException | InvalidKeyException | NoSuchPaddingException | BadPaddingException | IllegalBlockSizeException | InvalidAlgorithmParameterException e) {
            Strings.LOGGER.error(e.getMessage(), e);
            throw ServiceException.instance(CommonErrors.ENCRYPTION_FAILED, e.getMessage());
        }
    }

    /**
     * This method attempts to decrypt the provided input string using the provided secret.
     *
     * @param stringToDecrypt
     *         Input string that needs to be decrypted.
     *
     * @return Decrypted string.
     *
     * @throws ServiceException
     *         Whenever any exceptions are encountered.
     */
    public static String decrypt(final String stringToDecrypt) {
        return Strings.decrypt(stringToDecrypt, Strings.SECRET);
    }

    /**
     * This method attempts to decrypt the provided input string using the provided secret.
     *
     * @param stringToDecrypt
     *         Input string that needs to be decrypted.
     * @param secret
     *         Secret key to be used for decryption.
     *
     * @return Decrypted string.
     *
     * @throws ServiceException
     *         Whenever any exceptions are encountered.
     */
    public static String decrypt(final String stringToDecrypt, final byte[] secret) {
        // Credits go to the author of the below article:
        // https://howtodoinjava.com/security/java-aes-encryption-example/

        try {
            final Cipher cipher = Cipher.getInstance(new String(Strings.ENCRYPTION_ALGORITHM));
            cipher.init(Cipher.DECRYPT_MODE, Strings.generateSecretKeySpec(secret), new IvParameterSpec(new byte[16]));
            return new String(cipher.doFinal(Base64.getDecoder().decode(stringToDecrypt)));
        } catch (final NoSuchAlgorithmException | InvalidKeyException | NoSuchPaddingException | BadPaddingException | IllegalBlockSizeException | InvalidAlgorithmParameterException ex) {
            Strings.LOGGER.error(ex.getMessage(), ex);
            throw ServiceException.instance(CommonErrors.ENCRYPTION_FAILED, ex.getMessage());
        }
    }

    /**
     * This method compares two strings for equality ignoring case sensitivity.
     *
     * @param input1
     *         Input string 1.
     * @param input2
     *         Input string 2.
     *
     * @return True if the provided strings are the same.
     */
    public static boolean same(final String input1, final String input2) {
        return Strings.same(input1, input2, true);
    }

    /**
     * This method compares two strings for equality, either case-sensitive or case-insensitive based on the provided
     * {@code ignoreCase} parameter.
     *
     * @param input1
     *         Input string 1.
     * @param input2
     *         Input string 2.
     * @param ignoreCase
     *         Boolean indicating if the case comparison has to be ignored.
     *
     * @return True if the provided strings are the same.
     */
    public static boolean same(final String input1, final String input2, final boolean ignoreCase) {
        // Both are null, we return true
        if (Objects.isNull(input1) && Objects.isNull(input2)) {
            return true;
        }

        // Both are non-null, check based on ignoreCase parameter.
        if (Objects.nonNull(input1) && Objects.nonNull(input2)) {
            return ignoreCase
                    ? input1.equalsIgnoreCase(input2)
                    : input1.equals(input2);
        }
        return false;
    }

    /**
     * This method attempts to generate an uncapitalized name (lower camel-case) based on the provided {@code type}.
     * <p>
     * If the {@code type} is specified as {@code CreatePlatform.class}, this method returns {@code createPlatform}.
     *
     * @param type
     *         Target type based on which the input needs to be formulated.
     * @param <T>
     *         Type of the object.
     *
     * @return Input name for the specified {@code type}.
     */
    public static <T> String uncapitalizedTypeName(final Class<T> type) {
        if (Objects.isNull(type)) {
            return StringUtils.EMPTY;
        }

        return StringUtils.uncapitalize(type.getSimpleName());
    }

    // ---------------------
    // Private methods
    // ---------------------

    /**
     * Replaces the characters between {@code fromIndex} and {@code toIndex} with the specified {@code
     * maskedCharacter}.
     *
     * @param input
     *         Input containing the range of characters to be masked.
     * @param fromIndex
     *         From index and should be >= 0 and < input.length() - 1. This is inclusive.
     * @param toIndex
     *         To index and should be > 0 and < input.length. This is also inclusive.
     * @param maskedCharacter
     *         Character to be used for masking (ex: '*').
     *
     * @return Masked string.
     */
    private static String replaceChars(final String input, final int fromIndex, final int toIndex,
                                       final char maskedCharacter) {
        if (StringUtils.isBlank(input)) {
            return input;
        }

        final int inputLength = input.length();

        int startIndex = 0;
        int endIndex = inputLength;

        if (fromIndex < toIndex) {
            startIndex = fromIndex >= 0 && fromIndex < (inputLength - 1)
                    ? fromIndex
                    : 0;
            endIndex = toIndex > 0 && toIndex < inputLength
                    ? toIndex
                    : inputLength;

            if (startIndex >= endIndex) {
                startIndex = 0;
                endIndex = inputLength;
            }
        }

        final int maskedCharsLength = endIndex - startIndex;
        final StringBuilder onlyMaskedCharacters = new StringBuilder(maskedCharsLength);

        for (int i = 0; i < maskedCharsLength; i++) {
            onlyMaskedCharacters.append(maskedCharacter);
        }

        return input.substring(0, startIndex) + onlyMaskedCharacters.toString() + input.substring(endIndex);
    }

    /**
     * This method attempts to generate a {@link SecretKeySpec} for the provided secret.
     *
     * @param secret
     *         Secret for which the {@link SecretKeySpec} needs to be generated.
     *
     * @return Instance of type {@link SecretKeySpec}
     *
     * @throws NoSuchAlgorithmException
     *         When an invalid algorithm is specified for generation of {@link SecretKeySpec}.
     */
    private static SecretKeySpec generateSecretKeySpec(final byte[] secret) throws NoSuchAlgorithmException {
        final MessageDigest sha = MessageDigest.getInstance("SHA-1");
        return new SecretKeySpec(Arrays.copyOf(sha.digest(secret), 16), "AES");
    }
}
