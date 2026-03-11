package fr.univrennes.istic.l2gen.application.core.filter.type;

public enum FilterValueType {
    STRING,
    NUMERIC,
    INTEGER,
    FLOATING,
    PERCENTAGE,
    URL,
    EMAIL,
    BOOLEAN,
    DATE,
    EMPTY,
    NOT_EMPTY;

    public static FilterValueType parse(String value) {
        return switch (value.toLowerCase()) {
            case "string", "text" -> STRING;
            case "numeric", "number" -> NUMERIC;
            case "integer", "int" -> INTEGER;
            case "floating", "float", "decimal", "double" -> FLOATING;
            case "percentage", "percent" -> PERCENTAGE;
            case "url", "uri", "link" -> URL;
            case "email", "mail" -> EMAIL;
            case "boolean", "bool" -> BOOLEAN;
            case "date" -> DATE;
            case "empty" -> EMPTY;
            case "notempty" -> NOT_EMPTY;
            default -> throw new IllegalArgumentException("Unsupported value type: " + value);
        };
    }
}