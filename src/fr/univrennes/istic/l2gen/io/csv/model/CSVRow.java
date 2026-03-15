package fr.univrennes.istic.l2gen.io.csv.model;

import java.util.Iterator;
import java.util.Optional;

public final class CSVRow extends CSVVirtualData {

    public CSVRow(CSVTable table, int rowIndex) {
        super(table, rowIndex);
    }

    public CSVRow(String[] cells) {
        super(cells);
    }

    @Override
    public Optional<String> getCell(int colIndex) {
        if (cells != null) {
            if (colIndex < 0 || colIndex >= cells.length) {
                throw new IndexOutOfBoundsException("Column index out of bounds");
            }
            return Optional.ofNullable(cells[colIndex]);
        }

        if (table == null) {
            throw new IllegalStateException("Row is not associated with a table");
        }
        if (colIndex < 0 || colIndex >= table.getColumnCount()) {
            throw new IndexOutOfBoundsException("Column index out of bounds");
        }
        return table.getCell(this.virtualIndex, colIndex);
    }

    @Override
    public String getRawCell(int colIndex) {
        if (cells != null) {
            if (colIndex < 0 || colIndex >= cells.length) {
                throw new IndexOutOfBoundsException("Column index out of bounds");
            }
            return cells[colIndex];
        }
        if (table == null) {
            throw new IllegalStateException("Row is not associated with a table");
        }
        if (colIndex < 0 || colIndex >= table.getColumnCount()) {
            throw new IndexOutOfBoundsException("Column index out of bounds");
        }
        return table.getRawCell(this.virtualIndex, colIndex);
    }

    @Override
    public int size() {
        if (cells != null) {
            return cells.length;
        }
        if (table == null) {
            throw new IllegalStateException("Row is not associated with a table");
        }
        return table.getColumnCount();
    }

    @Override
    public Iterator<Optional<String>> iterator() {
        return new Iterator<Optional<String>>() {
            private int currentIndex = 0;

            @Override
            public boolean hasNext() {
                if (cells != null) {
                    return currentIndex < cells.length;
                }
                return currentIndex < table.getColumnCount();
            }

            @Override
            public Optional<String> next() {
                if (cells != null) {
                    return Optional.ofNullable(cells[currentIndex++]);
                }
                return table.getCell(virtualIndex, currentIndex++);
            }
        };
    }
}
