package fr.univrennes.istic.l2gen.application.core.filter.comparaison;

public enum ComparisonOperator {
    EQ,
    NEQ,
    GT,
    GTE,
    LT,
    LTE,
    CONTAINS,
    STARTS_WITH,
    ENDS_WITH,
    EMPTY,
    NOT_EMPTY;

    public static ComparisonOperator parse(String value) {
        return switch (value.toLowerCase()) {
            case "=", "==", "eq", "equal" -> EQ;
            case "!=", "<>", "neq", "notequal" -> NEQ;
            case ">", "gt", "greater" -> GT;
            case ">=", "gte", "ge" -> GTE;
            case "<", "lt", "less" -> LT;
            case "<=", "lte", "le" -> LTE;
            case "contains" -> CONTAINS;
            case "starts", "startswith" -> STARTS_WITH;
            case "ends", "endswith" -> ENDS_WITH;
            case "empty", "isempty" -> EMPTY;
            case "notempty", "isnotempty" -> NOT_EMPTY;
            default -> throw new IllegalArgumentException("Unsupported comparison operator: " + value);
        };
    }
}
