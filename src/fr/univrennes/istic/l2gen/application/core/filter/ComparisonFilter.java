package fr.univrennes.istic.l2gen.application.core.filter;

import java.util.Optional;

import fr.univrennes.istic.l2gen.io.csv.model.CSVRow;

public final class ComparisonFilter implements IFilter {

    private final int columnIndex;
    private final ComparisonOperator operator;
    private final String expectedValue;
    private final boolean numeric;
    private final boolean ignoreCase;

    public ComparisonFilter(int columnIndex, ComparisonOperator operator, String expectedValue) {
        this(columnIndex, operator, expectedValue, false, true);
    }

    public ComparisonFilter(int columnIndex, ComparisonOperator operator, String expectedValue, boolean numeric,
            boolean ignoreCase) {
        this.columnIndex = columnIndex;
        this.operator = operator;
        this.expectedValue = expectedValue;
        this.numeric = numeric;
        this.ignoreCase = ignoreCase;
    }

    @Override
    public boolean matches(CSVRow row, Optional<CSVRow> header) {
        if (columnIndex < 0 || columnIndex >= row.size()) {
            return false;
        }

        Optional<String> cell = row.cell(columnIndex);
        String left = cell.orElse(null);

        if (operator == ComparisonOperator.EMPTY) {
            return left == null || left.trim().isEmpty();
        }
        if (operator == ComparisonOperator.NOT_EMPTY) {
            return left != null && !left.trim().isEmpty();
        }

        if (left == null) {
            return operator == ComparisonOperator.NEQ && expectedValue != null;
        }

        if (numeric) {
            return evaluateNumeric(left);
        }
        return evaluateText(left);
    }

    private boolean evaluateNumeric(String leftValue) {
        if (expectedValue == null) {
            return false;
        }

        try {
            double left = Double.parseDouble(leftValue.trim());
            double right = Double.parseDouble(expectedValue.trim());

            return switch (operator) {
                case EQ -> Double.compare(left, right) == 0;
                case NEQ -> Double.compare(left, right) != 0;
                case GT -> left > right;
                case GTE -> left >= right;
                case LT -> left < right;
                case LTE -> left <= right;
                default -> false;
            };
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean evaluateText(String left) {
        String right = expectedValue == null ? "" : expectedValue;
        String normalizedLeft = ignoreCase ? left.toLowerCase() : left;
        String normalizedRight = ignoreCase ? right.toLowerCase() : right;

        return switch (operator) {
            case EQ -> normalizedLeft.equals(normalizedRight);
            case NEQ -> !normalizedLeft.equals(normalizedRight);
            case GT -> normalizedLeft.compareTo(normalizedRight) > 0;
            case GTE -> normalizedLeft.compareTo(normalizedRight) >= 0;
            case LT -> normalizedLeft.compareTo(normalizedRight) < 0;
            case LTE -> normalizedLeft.compareTo(normalizedRight) <= 0;
            case CONTAINS -> normalizedLeft.contains(normalizedRight);
            case STARTS_WITH -> normalizedLeft.startsWith(normalizedRight);
            case ENDS_WITH -> normalizedLeft.endsWith(normalizedRight);
            default -> false;
        };
    }

}
