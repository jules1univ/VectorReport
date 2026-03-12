package fr.univrennes.istic.l2gen.io.csv.model;

import java.util.AbstractList;

public final class VirtualRowList extends AbstractList<CSVRow> {

    private final CSVTable csvTable;

    public VirtualRowList(CSVTable csvTable) {
        this.csvTable = csvTable;
    }

    @Override
    public CSVRow get(int index) {
        return this.csvTable.getRow(index);
    }

    @Override
    public int size() {
        return this.csvTable.rowCount;
    }
}