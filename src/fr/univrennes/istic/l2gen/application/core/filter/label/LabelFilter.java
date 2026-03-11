package fr.univrennes.istic.l2gen.application.core.filter.label;

import fr.univrennes.istic.l2gen.application.core.filter.IFilter;
import fr.univrennes.istic.l2gen.io.csv.model.CSVRow;
import java.util.List;
import java.util.Optional;
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

    public static LabelFilter name(String colName, List<String> allowedLabels, CSVRow header) {
        if (header == null) {
            throw new IllegalArgumentException("Header is required for column name filtering");
        }
        int index = header.getCells().indexOf(Optional.of(colName));
        if (index == -1) {
            throw new IllegalArgumentException("Column not found: " + colName);
        }
        return new LabelFilter(index, allowedLabels);
    }

    @Override
    public boolean matches(CSVRow row, Optional<CSVRow> header) {
        if (columnIndex < 0 || columnIndex >= row.getCells().size()) {
            return false;
        }

        Optional<String> cellValue = row.getCell(columnIndex);
        if (!cellValue.isPresent()) {
            return false;
        }

        if (caseSensitive) {
            return allowedLabels.contains(cellValue.get());
        } else {
            return allowedLabels.contains(cellValue.get().toLowerCase());
        }
    }
}
