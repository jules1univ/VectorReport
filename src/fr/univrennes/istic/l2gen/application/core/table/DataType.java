package fr.univrennes.istic.l2gen.application.core.table;

import java.util.List;

public enum DataType {
    STRING,
    INTEGER,
    DOUBLE,
    BOOLEAN,
    DATE,
    EMPTY;

    public static final List<DataType> INFERENCE_ORDER = List.of(
            EMPTY,
            BOOLEAN,
            INTEGER,
            DOUBLE,
            DATE);

    public boolean isNumeric() {
        return this == INTEGER || this == DOUBLE;
    }

    public String toCastSQL(String columnName) {
        String col = "\"" + columnName + "\"";

        return switch (this) {
            case EMPTY ->
                col + " IS NULL";

            case INTEGER ->
                "(TRY_CAST(" + col + " AS BIGINT) IS NOT NULL OR " + col + " IS NULL)";

            case DOUBLE ->
                "(TRY_CAST(" + col + " AS DOUBLE) IS NOT NULL OR " + col + " IS NULL)";

            case BOOLEAN ->
                "(" +
                        "TRY_CAST(" + col + " AS BOOLEAN) IS NOT NULL " +
                        "OR " + col + " IN ('0','1','true','false','TRUE','FALSE') " +
                        "OR " + col + " IS NULL" +
                        ")";

            case DATE ->
                "(TRY_CAST(" + col + " AS DATE) IS NOT NULL OR " + col + " IS NULL)";

            default ->
                "TRUE";
        };
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

        return STRING;
    }

    public String toSQL() {
        return switch (this) {
            case STRING -> "VARCHAR";
            case INTEGER -> "INTEGER";
            case DOUBLE -> "DOUBLE";
            case BOOLEAN -> "BOOLEAN";
            case DATE -> "DATE";
            case EMPTY -> "VARCHAR";
        };
    }
}