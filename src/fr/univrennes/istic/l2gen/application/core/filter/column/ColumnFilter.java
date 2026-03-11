package fr.univrennes.istic.l2gen.application.core.filter.column;

import java.util.Optional;

import fr.univrennes.istic.l2gen.application.core.filter.IFilter;
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
        int index = header.getCells().indexOf(Optional.of(colName));
        if (index == -1) {
            throw new IllegalArgumentException("Column not found: " + colName);
        }
        return new ColumnFilter(index, expectedValue);
    }

    @Override
    public boolean matches(CSVRow row, Optional<CSVRow> header) {
        if (columnIndex < 0 || columnIndex >= row.getCells().size()) {
            return false;
        }

        Optional<String> cellValue = row.getCell(columnIndex);
        if (!cellValue.isPresent()) {
            return expectedValue == null;
        }

        if (exactMatch) {
            return cellValue.get().equals(expectedValue);
        } else {
            return cellValue.get().contains(expectedValue);
        }
    }
}
