package com.epam.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class StringUtilsTest {
    @Test
    public void isPositiveNumberTest() {
        assertFalse(StringUtils.isPositiveNumber("-12"));
        assertTrue(StringUtils.isPositiveNumber("22"));
    }
}
