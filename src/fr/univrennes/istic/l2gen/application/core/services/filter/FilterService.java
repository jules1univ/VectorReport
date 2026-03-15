package fr.univrennes.istic.l2gen.application.core.services.filter;

import fr.univrennes.istic.l2gen.application.core.services.IService;
import fr.univrennes.istic.l2gen.io.csv.model.CSVRow;
import fr.univrennes.istic.l2gen.io.csv.model.CSVTable;

import java.util.ArrayList;
import java.util.Comparator;

public final class FilterService implements IService {

    public FilterService() {
    }

    public CSVTable sortByColumn(CSVTable table, int columnIndex, boolean ascending) {
        if (table.getRowCount() == 0 || columnIndex < 0
                || columnIndex >= table.getColumnCount()) {
            return table;
        }

        Comparator<CSVRow> comparator = Comparator.comparing(row -> row.getCell(columnIndex).orElse(""));
        if (!ascending) {
            comparator = comparator.reversed();
        }

        ArrayList<CSVRow> sortedRows = new ArrayList<>(table.getRows());
        sortedRows.sort(comparator);
        return new CSVTable(table.getHeader().orElse(null), sortedRows);
    }

    public CSVTable filterByCategory(CSVTable table, int columnIndex, boolean percentage) {
        // group all the same value from the column X and count or percentage the other
        // columns values:
        return table;
    }
}
