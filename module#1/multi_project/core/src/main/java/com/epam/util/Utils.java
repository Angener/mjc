package com.epam.util;

import java.util.Arrays;

public final class Utils {

    private Utils() {
    }

    public static boolean isAllPositiveNumbers(String... number) {
        return Arrays.stream(number)
                .parallel()
                .allMatch(StringUtils::isPositiveNumber);
    }
}
