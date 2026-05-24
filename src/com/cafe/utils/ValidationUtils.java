package com.cafe.utils;

import java.math.BigDecimal;

public final class ValidationUtils {
    private ValidationUtils() {
    }

    public static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    public static String cleanText(String value) {
        return value == null ? "" : value.trim().replaceAll("\\s+", " ");
    }

    public static String optionalText(String value) {
        return isBlank(value) ? null : cleanText(value);
    }

    public static boolean isValidPhone(String phone) {
        return phone != null && phone.matches("[0-9+\\- ]{7,20}");
    }

    public static boolean isValidEmail(String email) {
        return isBlank(email) || email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    }

    public static boolean isPositiveNumber(String value) {
        try {
            return new BigDecimal(value).compareTo(BigDecimal.ZERO) > 0;
        } catch (Exception ex) {
            return false;
        }
    }

    public static boolean isPositiveInteger(String value) {
        try {
            return Integer.parseInt(value) > 0;
        } catch (Exception ex) {
            return false;
        }
    }
}
