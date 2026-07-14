package com.clinica.auth.utils;

/**
 * Utility class for formatting common data types.
 * This class is isolated and does not affect the main application flow.
 */
public class FormatUtils {

    private FormatUtils() {
        // Private constructor to prevent instantiation
    }

    /**
     * Capitalizes the first letter of a given string.
     *
     * @param input the string to capitalize
     * @return the capitalized string, or the original input if it's null or empty
     */
    public static String capitalizeFirstLetter(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        return input.substring(0, 1).toUpperCase() + input.substring(1).toLowerCase();
    }
}
