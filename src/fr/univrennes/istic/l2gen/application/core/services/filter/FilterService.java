package fr.univrennes.istic.l2gen.application.core.services.filter;

import fr.univrennes.istic.l2gen.application.core.filter.IFilter;
import fr.univrennes.istic.l2gen.application.core.services.IService;
import fr.univrennes.istic.l2gen.io.csv.model.CSVRow;
import fr.univrennes.istic.l2gen.io.csv.model.CSVTable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class FilterService implements IService {

    private final List<IFilter> filters = new ArrayList<>();

    public FilterService() {
    }

    public void add(IFilter filter) {
        this.filters.add(filter);
    }

    public void remove(IFilter filter) {
        this.filters.remove(filter);
    }

    public void clear() {
        this.filters.clear();
    }

    public List<IFilter> getAll() {
        return this.filters;
    }

    public CSVTable apply(CSVTable table) {
        CSVTable copy = new CSVTable(table);
        for (IFilter filter : filters) {
            copy = filter.apply(copy);
        }
        return copy;
    }

    public CSVTable apply(IFilter filter, CSVTable table) {
        return filter.apply(new CSVTable(table));
    }

    public CSVTable removeEmptyRows(CSVTable table) {
        CSVTable cleaned = new CSVTable(table.header().orElse(null), new ArrayList<>());
        for (CSVRow row : table.rows()) {
            if (!isRowEmpty(row)) {
                cleaned.addRow(row);
            }
        }
        return cleaned;
    }

    public CSVTable removeEmptyColumns(CSVTable table) {
        int columnCount = table.header().map(CSVRow::size).orElseGet(() -> table.rows().stream()
                .mapToInt(CSVRow::size)
                .max()
                .orElse(0));

        boolean[] keepColumn = new boolean[columnCount];

        for (int i = 0; i < columnCount; i++) {
            if (table.header().isPresent() && hasValue(table.header().get(), i)) {
                keepColumn[i] = true;
                continue;
            }
            for (CSVRow row : table.rows()) {
                if (hasValue(row, i)) {
                    keepColumn[i] = true;
                    break;
                }
            }
        }

        CSVRow newHeader = table.header().map(row -> projectRow(row, keepColumn)).orElse(null);
        CSVTable result = new CSVTable(newHeader, new ArrayList<>());

        for (CSVRow row : table.rows()) {
            result.addRow(projectRow(row, keepColumn));
        }
        return result;
    }

    public CSVTable sortByColumn(CSVTable table, int columnIndex, boolean ascending, boolean numeric) {
        CSVTable copy = new CSVTable(table);
        Comparator<CSVRow> comparator = (a, b) -> compareRows(a, b, columnIndex, numeric);
        if (!ascending) {
            comparator = comparator.reversed();
        }
        copy.rows().sort(comparator);
        return copy;
    }

    private int compareRows(CSVRow a, CSVRow b, int columnIndex, boolean numeric) {
        String left = readCell(a, columnIndex).orElse("");
        String right = readCell(b, columnIndex).orElse("");

        if (numeric) {
            try {
                return Double.compare(Double.parseDouble(left.trim()), Double.parseDouble(right.trim()));
            } catch (NumberFormatException e) {
                // Fallback to text when one value is not numeric.
            }
        }
        return left.compareToIgnoreCase(right);
    }

    private CSVRow projectRow(CSVRow source, boolean[] keepColumn) {
        CSVRow projected = new CSVRow();
        for (int i = 0; i < keepColumn.length; i++) {
            if (!keepColumn[i]) {
                continue;
            }
            projected.addCell(readCell(source, i).orElse(null));
        }
        projected.trimToSize();
        return projected;
    }

    private boolean isRowEmpty(CSVRow row) {
        for (int i = 0; i < row.size(); i++) {
            if (hasValue(row, i)) {
                return false;
            }
        }
        return true;
    }

    private boolean hasValue(CSVRow row, int col) {
        return readCell(row, col)
                .map(value -> !value.trim().isEmpty())
                .orElse(false);
    }

    private Optional<String> readCell(CSVRow row, int col) {
        if (col < 0 || col >= row.size()) {
            return Optional.empty();
        }
        return row.cell(col);
    }
}
