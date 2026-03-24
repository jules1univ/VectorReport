package fr.univrennes.istic.l2gen.application.core.table;

public enum DataType {
    STRING,
    INTEGER,
    DOUBLE,
    BOOLEAN,
    DATE,
    UNKNOWN;

    public boolean isNumeric() {
        return this == INTEGER || this == DOUBLE;
    }

    public static DataType fromSQL(String type) {
        type = type.toUpperCase();

        if (type.contains("INT"))
            return INTEGER;
        if (type.contains("DOUBLE") || type.contains("FLOAT"))
            return DOUBLE;
        if (type.contains("BOOL"))
            return BOOLEAN;
        if (type.contains("DATE") || type.contains("TIME"))
            return DATE;
        if (type.contains("CHAR") || type.contains("VARCHAR"))
            return STRING;

        return UNKNOWN;
    }

    public String toSQL() {
        return switch (this) {
            case STRING -> "VARCHAR";
            case INTEGER -> "INTEGER";
            case DOUBLE -> "DOUBLE";
            case BOOLEAN -> "BOOLEAN";
            case DATE -> "DATE";
            default -> "VARCHAR";
        };
    }
}
