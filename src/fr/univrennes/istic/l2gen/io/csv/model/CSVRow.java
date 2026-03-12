package fr.univrennes.istic.l2gen.io.csv.model;

import java.util.Arrays;
import java.util.Optional;

public final class CSVRow {

    private static final int DEFAULT_CAPACITY = 8;
    private static final float GROWTH_FACTOR = 1.5f;

    private String[] cells;
    private int size;

    private CSVColumn[] columnView;
    private int rowIndexInColumns;
    private boolean isMaterialized;

    public CSVRow() {
        this.cells = new String[DEFAULT_CAPACITY];
        this.size = 0;
        this.isMaterialized = true;
    }

    public CSVRow(String[] cells) {
        this.size = cells.length;
        this.cells = new String[this.size];
        for (int i = 0; i < this.size; i++) {
            this.cells[i] = normalize(cells[i]);
        }
        this.isMaterialized = true;
    }

    public CSVRow(CSVRow other) {
        if (other.isMaterialized) {
            this.size = other.size;
            this.cells = new String[other.size];
            System.arraycopy(other.cells, 0, this.cells, 0, other.size);
        } else {
            this.size = other.size;
            this.cells = new String[other.size];
            for (int i = 0; i < other.size; i++) {
                this.cells[i] = other.columnView[i].getRawCell(other.rowIndexInColumns);
            }
        }
        this.isMaterialized = true;
    }

    static CSVRow virtualRow(CSVColumn[] columns, int rowIndex) {
        CSVRow row = new CSVRow();
        row.columnView = columns;
        row.rowIndexInColumns = rowIndex;
        row.size = columns.length;
        row.cells = null;
        row.isMaterialized = false;
        return row;
    }

    public String getRawCell(int index) {
        if (!isMaterialized) {
            return columnView[index].getRawCell(rowIndexInColumns);
        }
        return cells[index];
    }

    public Optional<String> getCell(int index) {
        return Optional.ofNullable(getRawCell(index));
    }

    public String[] getCells() {
        if (!isMaterialized) {
            materialize();
        }
        return cells;
    }

    public int size() {
        return size;
    }

    public void addCell(String value) {
        if (!isMaterialized) {
            materialize();
        }
        ensureCapacity(size + 1);
        cells[size] = normalize(value);
        size++;
    }

    public void removeCell(int index) {
        if (!isMaterialized) {
            materialize();
        }
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
        System.arraycopy(cells, index + 1, cells, index, size - index - 1);
        cells[size - 1] = null;
        size--;
    }

    public void trimToSize() {
        if (!isMaterialized) {
            return;
        }
        if (size == cells.length) {
            return;
        }
        cells = Arrays.copyOf(cells, size);
    }

    private void materialize() {
        cells = new String[size];
        for (int i = 0; i < size; i++) {
            cells[i] = columnView[i].getRawCell(rowIndexInColumns);
        }
        columnView = null;
        isMaterialized = true;
    }

    private void ensureCapacity(int minCapacity) {
        if (minCapacity <= cells.length) {
            return;
        }
        int newCapacity = Math.max(minCapacity, (int) (cells.length * GROWTH_FACTOR) + 1);
        cells = Arrays.copyOf(cells, newCapacity);
    }

    private static String normalize(String value) {
        return (value == null || value.isEmpty()) ? null : value;
    }
}