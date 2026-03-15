package fr.univrennes.istic.l2gen.io.csv.model;

import java.util.Iterator;
import java.util.Optional;

public final class CSVColumn extends CSVVirtualData {
    private CSVType type;

    public CSVColumn(CSVTable table, int colIndex) {
        super(table, colIndex);
        this.type = table.getColumnType(colIndex);
    }

    public CSVColumn(String[] cells) {
        super(cells);
        this.type = cells.length == 0 ? CSVType.EMPTY : CSVType.inferType(cells[0]);
    }

    public CSVType getType() {
        return type;
    }

    @Override
    public Optional<String> getCell(int rowIndex) {
        if (cells != null) {
            if (rowIndex < 0 || rowIndex >= cells.length) {
                throw new IndexOutOfBoundsException("Row index out of bounds");
            }
            return Optional.ofNullable(cells[rowIndex]);
        }

        if (table == null) {
            throw new IllegalStateException("Column is not associated with a table");
        }
        if (rowIndex < 0 || rowIndex >= table.getRowCount()) {
            throw new IndexOutOfBoundsException("Row index out of bounds");
        }
        return table.getCell(rowIndex, this.virtualIndex);
    }

    @Override
    public String getRawCell(int rowIndex) {
        if (cells != null) {
            if (rowIndex < 0 || rowIndex >= cells.length) {
                throw new IndexOutOfBoundsException("Row index out of bounds");
            }
            return cells[rowIndex];
        }
        if (table == null) {
            throw new IllegalStateException("Column is not associated with a table");
        }
        if (rowIndex < 0 || rowIndex >= table.getRowCount()) {
            throw new IndexOutOfBoundsException("Row index out of bounds");
        }
        return table.getRawCell(rowIndex, this.virtualIndex);
    }

    @Override
    public int size() {
        if (cells != null) {
            return cells.length;
        }
        if (table == null) {
            throw new IllegalStateException("Column is not associated with a table");
        }
        return table.getRowCount();
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
                return currentIndex < table.getRowCount();
            }

            @Override
            public Optional<String> next() {
                if (cells != null) {
                    return Optional.ofNullable(cells[currentIndex++]);
                }
                return table.getCell(currentIndex++, virtualIndex);
            }
        };
    }

}
