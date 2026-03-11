package fr.univrennes.istic.l2gen.application.core.filter;

import java.util.Optional;
import java.util.ArrayList;

import fr.univrennes.istic.l2gen.io.csv.model.CSVRow;
import fr.univrennes.istic.l2gen.io.csv.model.CSVTable;

public interface IFilter {

    public boolean matches(CSVRow row, Optional<CSVRow> header);

    public default CSVTable apply(CSVTable table) {
        CSVTable filtered = new CSVTable(table.getHeader().orElse(null), new ArrayList<>());
        for (CSVRow row : table.getRows()) {
            if (this.matches(row, table.getHeader())) {
                filtered.addRow(row);
            }
        }
        return filtered;
    }
}
