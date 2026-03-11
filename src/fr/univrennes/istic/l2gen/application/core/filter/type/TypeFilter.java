package fr.univrennes.istic.l2gen.application.core.filter.type;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Optional;
import java.util.regex.Pattern;

import fr.univrennes.istic.l2gen.application.core.filter.IFilter;
import fr.univrennes.istic.l2gen.io.csv.model.CSVRow;

public final class TypeFilter implements IFilter {

    private static final Pattern INTEGER_PATTERN = Pattern.compile("^[+-]?\\d+$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private final int columnIndex;
    private final FilterValueType valueType;

    public TypeFilter(int columnIndex, FilterValueType valueType) {
        this.columnIndex = columnIndex;
        this.valueType = valueType;
    }

    @Override
    public boolean matches(CSVRow row, Optional<CSVRow> header) {
        if (columnIndex < 0 || columnIndex >= row.size()) {
            return false;
        }

        Optional<String> cell = row.cell(columnIndex);
        String value = cell.orElse(null);

        if (valueType == FilterValueType.EMPTY) {
            return value == null || value.trim().isEmpty();
        }
        if (valueType == FilterValueType.NOT_EMPTY) {
            return value != null && !value.trim().isEmpty();
        }
        if (value == null || value.trim().isEmpty()) {
            return false;
        }

        String trimmed = value.trim();
        switch (valueType) {
            case STRING -> {
                return isString(trimmed);
            }
            case NUMERIC -> {
                return isNumeric(trimmed);
            }
            case INTEGER -> {
                return isInteger(trimmed);
            }
            case FLOATING -> {
                return isFloating(trimmed);
            }
            case PERCENTAGE -> {
                return isPercentage(trimmed);
            }
            case URL -> {
                return isUrl(trimmed);
            }
            case EMAIL -> {
                return isEmail(trimmed);
            }
            case BOOLEAN -> {
                return isBoolean(trimmed);
            }
            case DATE -> {
                return isDate(trimmed);
            }
            default -> {
                return false;
            }
        }
    }

    private boolean isString(String value) {
        return !isNumeric(value) && !isBoolean(value) && !isUrl(value) && !isEmail(value) && !isDate(value);
    }

    private boolean isNumeric(String value) {
        try {
            double parsed = Double.parseDouble(value);
            return Double.isFinite(parsed);
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean isPercentage(String value) {
        if (value.endsWith("%")) {
            String numberPart = value.substring(0, value.length() - 1).trim();
            return isNumeric(numberPart);
        }
        return false;
    }

    private boolean isInteger(String value) {
        return INTEGER_PATTERN.matcher(value).matches();
    }

    private boolean isFloating(String value) {
        if (!isNumeric(value) || isInteger(value)) {
            return false;
        }
        return value.contains(".") || value.contains("e") || value.contains("E");
    }

    private boolean isUrl(String value) {
        try {
            URI uri = new URI(value);
            String scheme = uri.getScheme();
            return scheme != null && !scheme.isBlank() && (uri.getHost() != null || uri.getRawAuthority() != null);
        } catch (URISyntaxException e) {
            return false;
        }
    }

    private boolean isEmail(String value) {
        return EMAIL_PATTERN.matcher(value).matches();
    }

    private boolean isBoolean(String value) {
        String normalized = value.toLowerCase();
        return normalized.equals("true")
                || normalized.equals("false")
                || normalized.equals("yes")
                || normalized.equals("no")
                || normalized.equals("1")
                || normalized.equals("0");
    }

    private boolean isDate(String value) {
        try {
            LocalDate.parse(value);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    @Override
    public String toString() {
        return String.format("TypeFilter(column=%d, type=%s)", columnIndex, valueType.name().toLowerCase());
    }
}