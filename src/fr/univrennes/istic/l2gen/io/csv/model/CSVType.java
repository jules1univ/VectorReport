package fr.univrennes.istic.l2gen.io.csv.model;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Optional;
import java.util.regex.Pattern;

public enum CSVType {
    EMPTY,
    BOOLEAN,
    INTEGER,
    DOUBLE,
    PERCENTAGE,
    URL,
    DATE,
    STRING;

    private static final Pattern INTEGER_PATTERN = Pattern.compile("^[+-]?\\d+$");

    public static Optional<Class<?>> inferClass(String value) {
        if (value == null || value.isBlank()) {
            return Optional.empty();
        }

        if (isBoolean(value)) {
            return Optional.of(Boolean.class);
        }
        if (isInteger(value)) {
            return Optional.of(Integer.class);
        }
        if (isFloating(value)) {
            return Optional.of(Double.class);
        }
        if (isPercentage(value)) {
            return Optional.of(Double.class);
        }
        if (isUrl(value)) {
            return Optional.of(URI.class);
        }
        if (isDate(value)) {
            return Optional.of(LocalDate.class);
        }
        return Optional.of(String.class);
    }

    public static CSVType inferType(String value) {
        if (value == null || value.isBlank()) {
            return EMPTY;
        }

        if (isBoolean(value)) {
            return BOOLEAN;
        }
        if (isInteger(value)) {
            return INTEGER;
        }
        if (isFloating(value)) {
            return DOUBLE;
        }
        if (isPercentage(value)) {
            return PERCENTAGE;
        }
        if (isUrl(value)) {
            return URL;
        }
        if (isDate(value)) {
            return DATE;
        }
        return STRING;
    }

    public static Optional<Class<?>> fromType(CSVType type) {
        switch (type) {
            case BOOLEAN:
                return Optional.of(Boolean.class);
            case INTEGER:
                return Optional.of(Integer.class);
            case DOUBLE:
                return Optional.of(Double.class);
            case PERCENTAGE:
                return Optional.of(Double.class);
            case URL:
                return Optional.of(URI.class);
            case DATE:
                return Optional.of(LocalDate.class);
            default:
                return Optional.empty();
        }
    }

    public static CSVType fromClass(Class<?> cls) {
        if (cls == Boolean.class) {
            return CSVType.BOOLEAN;
        }
        if (cls == Integer.class) {
            return CSVType.INTEGER;
        }
        if (cls == Double.class) {
            return CSVType.DOUBLE;
        }
        if (cls == URI.class) {
            return CSVType.URL;
        }
        if (cls == LocalDate.class) {
            return CSVType.DATE;
        }
        if (cls == String.class) {
            return CSVType.STRING;
        }
        return CSVType.EMPTY;
    }

    public static <T> T parseValue(String value, Class<T> cls) {
        try {
            if (cls == Boolean.class) {
                String normalized = value.toLowerCase();
                if (normalized.equals("true") || normalized.equals("yes") || normalized.equals("1")) {
                    return cls.cast(Boolean.TRUE);
                } else if (normalized.equals("false") || normalized.equals("no") || normalized.equals("0")) {
                    return cls.cast(Boolean.FALSE);
                }
            } else if (cls == Integer.class) {
                return cls.cast(Integer.parseInt(value));
            } else if (cls == Double.class) {
                return cls.cast(Double.parseDouble(value));
            } else if (cls == URI.class) {
                return cls.cast(new URI(value));
            } else if (cls == LocalDate.class) {
                return cls.cast(LocalDate.parse(value));
            } else if (cls == String.class) {
                return cls.cast(value);
            }
        } catch (Exception e) {
        }

        return null;
    }

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

    private static boolean isFloating(String value) {
        if (!isNumeric(value) || isInteger(value)) {
            return false;
        }
        return value.contains(".") || value.contains("e") || value.contains("E");
    }

    private static boolean isUrl(String value) {
        try {
            URI uri = new URI(value);
            String scheme = uri.getScheme();
            return scheme != null && !scheme.isBlank() && (uri.getHost() != null || uri.getRawAuthority() != null);
        } catch (URISyntaxException e) {
            return false;
        }
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