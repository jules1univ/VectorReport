package fr.univrennes.istic.l2gen.io.csv.model;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.regex.Pattern;

public enum CSVType {
    STRING,
    DATE,
    NUMERIC,
    BOOLEAN,
    EMPTY;

    public static CSVType from(CSVSubtype subtype) {
        return switch (subtype) {
            case STRING -> STRING;
            case URL -> STRING;
            case EMAIL -> STRING;

            case INTEGER -> NUMERIC;
            case FLOATING -> NUMERIC;
            case PERCENTAGE -> NUMERIC;

            case BOOLEAN -> BOOLEAN;

            case DATE -> DATE;

            case EMPTY -> EMPTY;
            default -> throw new IllegalArgumentException("Unsupported CSV subtype: " + subtype);
        };
    }

    private static final Pattern INTEGER_PATTERN = Pattern.compile("^[+-]?\\d+$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    public static boolean isNumeric(String value) {
        try {
            double parsed = Double.parseDouble(value);
            return Double.isFinite(parsed);
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isPercentage(String value) {
        if (value.endsWith("%")) {
            String numberPart = value.substring(0, value.length() - 1).trim();
            return isNumeric(numberPart);
        }
        return false;
    }

    public static boolean isInteger(String value) {
        return INTEGER_PATTERN.matcher(value).matches();
    }

    public static boolean isFloating(String value) {
        if (!isNumeric(value) || isInteger(value)) {
            return false;
        }
        return value.contains(".") || value.contains("e") || value.contains("E");
    }

    public static boolean isUrl(String value) {
        try {
            URI uri = new URI(value);
            String scheme = uri.getScheme();
            return scheme != null && !scheme.isBlank() && (uri.getHost() != null || uri.getRawAuthority() != null);
        } catch (URISyntaxException e) {
            return false;
        }
    }

    public static boolean isEmail(String value) {
        return EMAIL_PATTERN.matcher(value).matches();
    }

    public static boolean isBoolean(String value) {
        String normalized = value.toLowerCase();
        return normalized.equals("true")
                || normalized.equals("false")
                || normalized.equals("yes")
                || normalized.equals("no")
                || normalized.equals("1")
                || normalized.equals("0");
    }

    public static boolean isDate(String value) {
        try {
            LocalDate.parse(value);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }
}
