package fr.univrennes.istic.l2gen.io.csv.model;

import java.util.ArrayList;
import java.util.List;

public record CSVTable(CSVRow header, List<CSVRow> rows) {
    public CSVTable() {
        this(null, new ArrayList<>());
    }

    public CSVTable(CSVTable other) {
        this(other.header, new ArrayList<>(other.rows));
    }

    public void addRow(CSVRow row) {
        rows.add(row);
    }

    public CSVRow row(int index) {
        return rows.get(index);
    }
}
