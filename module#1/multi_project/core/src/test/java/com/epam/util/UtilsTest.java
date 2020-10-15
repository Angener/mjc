package com.epam.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UtilsTest {
    @Test
    public void isAllPositiveNumberTest() {
        assertFalse(Utils.isAllPositiveNumbers("12", "1232", "-1", "23"));
        assertTrue(Utils.isAllPositiveNumbers("12", "24", "1"));
    }

    @Test
    public void isAllPositiveNumberThrowsNPE() {
        assertThrows(NullPointerException.class, () -> Utils.isAllPositiveNumbers((String) null));
    }
}
