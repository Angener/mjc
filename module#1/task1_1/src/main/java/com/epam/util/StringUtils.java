package com.epam.util;

public class StringUtils {
    private StringUtils() {
    }

    public static boolean isPositiveNumber(String number) {
        return !number.startsWith("-");
    }
}
