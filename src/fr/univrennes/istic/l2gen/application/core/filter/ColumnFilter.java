package fr.univrennes.istic.l2gen.application.core.filter;

import fr.univrennes.istic.l2gen.io.csv.model.CSVRow;

public class ColumnFilter implements IFilter {
    private final int columnIndex;
    private final String expectedValue;
    private final boolean exactMatch;

    public ColumnFilter(int colIndex, String expectedValue) {
        this(colIndex, expectedValue, true);
    }

    public ColumnFilter(int colIndex, String expectedValue, boolean exactMatch) {
        this.columnIndex = colIndex;
        this.expectedValue = expectedValue;
        this.exactMatch = exactMatch;
    }

    public static ColumnFilter byName(String colName, String expectedValue, CSVRow header) {
        if (header == null) {
            throw new IllegalArgumentException("Header is required for column name filtering");
        }
        int index = header.values().indexOf(colName);
        if (index == -1) {
            throw new IllegalArgumentException("Column not found: " + colName);
        }
        return new ColumnFilter(index, expectedValue);
    }

    @Override
    public boolean matches(CSVRow row, CSVRow header) {
        if (columnIndex < 0 || columnIndex >= row.values().size()) {
            return false;
        }

        String cellValue = row.cell(columnIndex);
        if (cellValue == null) {
            return expectedValue == null;
        }

        if (exactMatch) {
            return cellValue.equals(expectedValue);
        } else {
            return cellValue.contains(expectedValue);
        }
    }
}
