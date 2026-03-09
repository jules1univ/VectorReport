package fr.univrennes.istic.l2gen.application.core.filter;

import fr.univrennes.istic.l2gen.io.csv.model.CSVRow;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

public class LabelFilter implements IFilter {
    private final int columnIndex;
    private final Set<String> allowedLabels;
    private final boolean caseSensitive;

    public LabelFilter(int colIndex, List<String> allowedLabels) {
        this(colIndex, allowedLabels, true);
    }

    public LabelFilter(int colIndex, List<String> allowedLabels, boolean caseSensitive) {
        this.columnIndex = colIndex;
        this.caseSensitive = caseSensitive;
        this.allowedLabels = new HashSet<>();

        for (String label : allowedLabels) {
            if (caseSensitive) {
                this.allowedLabels.add(label);
            } else {
                this.allowedLabels.add(label.toLowerCase());
            }
        }
    }

    public static LabelFilter byName(String colName, List<String> allowedLabels, CSVRow header) {
        if (header == null) {
            throw new IllegalArgumentException("Header is required for column name filtering");
        }
        int index = header.values().indexOf(colName);
        if (index == -1) {
            throw new IllegalArgumentException("Column not found: " + colName);
        }
        return new LabelFilter(index, allowedLabels);
    }

    @Override
    public boolean matches(CSVRow row, CSVRow header) {
        if (columnIndex < 0 || columnIndex >= row.values().size()) {
            return false;
        }

        String cellValue = row.cell(columnIndex);
        if (cellValue == null) {
            return false;
        }

        if (caseSensitive) {
            return allowedLabels.contains(cellValue);
        } else {
            return allowedLabels.contains(cellValue.toLowerCase());
        }
    }
}
