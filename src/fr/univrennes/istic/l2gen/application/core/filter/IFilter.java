package fr.univrennes.istic.l2gen.application.core.filter;

import java.util.ArrayList;

import fr.univrennes.istic.l2gen.io.csv.model.CSVRow;
import fr.univrennes.istic.l2gen.io.csv.model.CSVTable;

public interface IFilter {

    public boolean matches(CSVRow row, CSVRow header);

    public default CSVTable apply(CSVTable table) {
        CSVTable filtered = new CSVTable(table.header(), new ArrayList<>());
        for (CSVRow row : table.rows()) {
            if (this.matches(row, table.header())) {
                filtered.addRow(row);
            }
        }
        return filtered;
    }
}
