package fr.univrennes.istic.l2gen.io.csv.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class CSVRow {

    private String[] cells;
    private int size;

    public CSVRow() {
        this.cells = new String[8];
        this.size = 0;
    }

    public CSVRow(String[] cells) {
        this.cells = cells;
        this.size = cells.length;
    }

    public Optional<String> cell(int index) {
        if (index < 0 || index >= size)
            throw new IndexOutOfBoundsException(index);
        return Optional.ofNullable(cells[index]);
    }

    public String cellRaw(int index) {
        if (index < 0 || index >= size)
            throw new IndexOutOfBoundsException(index);
        return cells[index];
    }

    public void addCell(String value) {
        if (size == cells.length) {
            String[] grown = new String[(size * 3 / 2) + 1];
            System.arraycopy(cells, 0, grown, 0, size);
            cells = grown;
        }
        cells[size++] = (value != null && value.isEmpty()) ? null : value;
    }

    public void trimToSize() {
        if (size < cells.length) {
            String[] trimmed = new String[size];
            System.arraycopy(cells, 0, trimmed, 0, size);
            cells = trimmed;
        }
    }

    public int size() {
        return size;
    }

    public List<Optional<String>> cells() {
        List<Optional<String>> list = new ArrayList<>(size);
        for (int i = 0; i < size; i++)
            list.add(Optional.ofNullable(cells[i]));
        return list;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < size; i++) {
            if (i > 0)
                sb.append(", ");
            sb.append(cells[i] != null ? cells[i] : "(empty)");
        }
        return sb.toString();
    }
}