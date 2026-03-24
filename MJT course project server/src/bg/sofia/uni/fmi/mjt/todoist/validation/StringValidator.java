package bg.sofia.uni.fmi.mjt.todoist.validation;

import java.util.Arrays;

public class StringValidator {
    private static final String NULL_OR_EMPTY_MESSAGE = " was null or empty.";
    private static final String MUST_NOT_INCLUDE_MESSAGE = "%s must not include any of the following: %s";

    public static void assertNotNullOrEmpty(String str, String argName) {
        if (str == null || str.isEmpty()) {
            throw new IllegalArgumentException(argName + NULL_OR_EMPTY_MESSAGE);
        }
    }

    public static boolean assertDoesNotInclude(String str, String[] invalidStrings, String argName) {
        assertNotNullOrEmpty(str, argName);

        for (String s : invalidStrings) {
            if (str.contains(s)) {
                throw new IllegalArgumentException(String.format(MUST_NOT_INCLUDE_MESSAGE,
                        argName, Arrays.toString(invalidStrings)));
            }
        }
        return true;
    }
}
