package fr.univrennes.istic.l2gen.io.csv.model;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class CSVTable {

    private static final int TYPE_INFERENCE_SAMPLE_SIZE = 10;

    private Optional<CSVRow> header;

    private CSVColumn[] columns;
    private int columnCount;
    int rowCount;

    public CSVTable() {
        this.header = Optional.empty();
        this.columns = new CSVColumn[0];
        this.columnCount = 0;
        this.rowCount = 0;
    }

    public CSVTable(CSVRow header, List<CSVRow> rows) {
        this.header = Optional.ofNullable(header);
        this.rowCount = rows.size();
        this.columns = buildColumnsFromRows(rows);
        this.columnCount = columns.length;
    }

    public CSVTable(CSVTable other) {
        this.header = other.header;
        this.rowCount = other.rowCount;
        this.columnCount = other.columnCount;
        this.columns = new CSVColumn[other.columnCount];
        for (int i = 0; i < other.columnCount; i++) {
            this.columns[i] = other.columns[i].sharedCopy();
        }
    }

    private CSVColumn[] buildColumnsFromRows(List<CSVRow> rows) {
        if (rows.isEmpty()) {
            return new CSVColumn[0];
        }
        int colCount = rows.get(0).size();
        CSVColumn[] cols = new CSVColumn[colCount];
        for (int colIndex = 0; colIndex < colCount; colIndex++) {
            String[] columnCells = new String[rows.size()];
            for (int rowIndex = 0; rowIndex < rows.size(); rowIndex++) {
                columnCells[rowIndex] = rows.get(rowIndex).getRawCell(colIndex);
            }
            CSVType inferredType = inferColumnType(columnCells, rows.size());
            cols[colIndex] = new CSVColumn(inferredType, columnCells, rows.size());
        }
        return cols;
    }

    private static CSVType inferColumnType(String[] cells, int rowCount) {
        Map<CSVType, Integer> typeCounts = new HashMap<>();
        int sampleSize = Math.min(TYPE_INFERENCE_SAMPLE_SIZE, rowCount);
        for (int i = 0; i < sampleSize; i++) {
            CSVType cellType = CSVType.inferType(cells[i]);
            typeCounts.merge(cellType, 1, Integer::sum);
        }
        return typeCounts.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(CSVType.EMPTY);
    }

    public void setHeader(CSVRow header) {
        this.header = Optional.ofNullable(header);
    }

    public Optional<CSVRow> getHeader() {
        return header;
    }

    public Optional<String> getColumnName(int colIndex) {
        return header.flatMap(h -> h.getCell(colIndex));
    }

    public int getRowCount() {
        return rowCount;
    }

    public int getColumnCount() {
        return columnCount;
    }

    public CSVRow getRow(int index) {
        if (index < 0 || index >= rowCount) {
            throw new IndexOutOfBoundsException("Row index: " + index + ", rowCount: " + rowCount);
        }
        return CSVRow.virtualRow(columns, index);
    }

    public CSVColumn getColumn(int colIndex) {
        return columns[colIndex];
    }

    public void addRow(CSVRow row) {
        if (columnCount == 0) {
            throw new IllegalStateException("Cannot add rows to a table with no columns");
        }
        if (row.size() != columnCount) {
            throw new IllegalArgumentException(
                    "Row size " + row.size() + " does not match column count " + columnCount);
        }
        int newRowIndex = rowCount;
        for (int colIndex = 0; colIndex < columnCount; colIndex++) {
            CSVColumn column = columns[colIndex];
            column.growTo(newRowIndex + 1);
            column.setCell(newRowIndex, row.getRawCell(colIndex));
        }
        rowCount++;
    }

    public void addColumn(CSVColumn column) {
        if (rowCount > 0 && column.size() != rowCount) {
            throw new IllegalArgumentException(
                    "Column size " + column.size() + " does not match row count " + rowCount);
        }
        if (columnCount == columns.length) {
            columns = Arrays.copyOf(columns, Math.max(8, columnCount * 2));
        }
        columns[columnCount] = column;
        columnCount++;
        if (rowCount == 0) {
            rowCount = column.size();
        }
    }

    public void removeColumn(int colIndex) {
        if (colIndex < 0 || colIndex >= columnCount) {
            throw new IndexOutOfBoundsException("Column index: " + colIndex);
        }
        System.arraycopy(columns, colIndex + 1, columns, colIndex, columnCount - colIndex - 1);
        columns[columnCount - 1] = null;
        columnCount--;
    }

    public void removeRow(int rowIndex) {
        if (rowIndex < 0 || rowIndex >= rowCount) {
            throw new IndexOutOfBoundsException("Row index: " + rowIndex + ", rowCount: " + rowCount);
        }
        for (int colIndex = 0; colIndex < columnCount; colIndex++) {
            columns[colIndex].removeRowAt(rowIndex, rowCount);
        }
        rowCount--;
    }

    public List<CSVRow> getRows() {
        return new VirtualRowList(this);
    }

    public List<CSVColumn> getColumns() {
        return Collections.unmodifiableList(Arrays.asList(Arrays.copyOf(columns, columnCount)));
    }
}