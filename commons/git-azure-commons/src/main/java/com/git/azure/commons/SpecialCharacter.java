/*
 * Copyright (c) 2021 REPLACE_CUSTOMER_NAME. All rights reserved.
 *
 * This file is part of test-git-azure.
 *
 * test-git-azure project and associated code cannot be copied
 * and/or distributed without a written permission of REPLACE_CUSTOMER_NAME,
 * and/or its subsidiaries.
 */
package com.git.azure.commons;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Enum that contains the constants used across the application (e.g. DOT, COMMA, etc.).
 *
 * @author Mahalingam Iyer
 */
@Getter
@AllArgsConstructor
public enum SpecialCharacter implements IEnumValueProvider<SpecialCharacter> {
    AMPERSAND("&"),
    ARROW("->"),
    BACKWARD_SLASH("\\"),
    CLOSE_PARENTHESIS(")"),
    CLOSE_SQUARE_BRACKET("]"),
    COLON(":"),
    COMMA(","),
    COMMA_SPACE(", "),
    DASH("-"),
    DOLLAR("$"),
    DOT("."),
    DOT_IN_REGEXP("\\."),
    FORWARD_SLASH("/"),
    OPEN_PARENTHESIS("("),
    OPEN_SQUARE_BRACKET("["),
    QUESTION_MARK("?"),
    SEMI_COLON(";"),
    SPACE(" "),
    UNDERSCORE("_");

    /** Value for this enum constant. */
    private final String value;
}
