package fr.univrennes.istic.l2gen.io.csv.model;

import java.util.ArrayList;
import java.util.List;

public record CSVRow(List<String> values) {

    public CSVRow() {
        this(new ArrayList<>());
    }

    public void addCell(String value) {
        values.add(value);
    }

    public String cell(int index) {
        return values.get(index);
    }
}
