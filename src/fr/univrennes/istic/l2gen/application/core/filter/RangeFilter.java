package fr.univrennes.istic.l2gen.application.core.filter;

import fr.univrennes.istic.l2gen.io.csv.model.CSVRow;

public class RangeFilter implements IFilter {
    private final int columnIndex;
    private final Double minValue;
    private final Double maxValue;
    private final boolean inclusive;

    public RangeFilter(int colIndex, Double minValue, Double maxValue) {
        this(colIndex, minValue, maxValue, true);
    }

    public RangeFilter(int colIndex, Double minValue, Double maxValue, boolean inclusive) {
        this.columnIndex = colIndex;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.inclusive = inclusive;
    }

    public static RangeFilter byName(String colName, Double minValue, Double maxValue, CSVRow header) {
        if (header == null) {
            throw new IllegalArgumentException("Header is required for column name filtering");
        }
        int index = header.values().indexOf(colName);
        if (index == -1) {
            throw new IllegalArgumentException("Column not found: " + colName);
        }
        return new RangeFilter(index, minValue, maxValue);
    }

    @Override
    public boolean matches(CSVRow row, CSVRow header) {
        if (columnIndex < 0 || columnIndex >= row.values().size()) {
            return false;
        }

        String cellValue = row.cell(columnIndex);
        if (cellValue == null || cellValue.trim().isEmpty()) {
            return false;
        }

        try {
            double value = Double.parseDouble(cellValue);

            if (minValue != null) {
                if (inclusive) {
                    if (value < minValue)
                        return false;
                } else {
                    if (value <= minValue)
                        return false;
                }
            }

            if (maxValue != null) {
                if (inclusive) {
                    if (value > maxValue)
                        return false;
                } else {
                    if (value >= maxValue)
                        return false;
                }
            }

            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
