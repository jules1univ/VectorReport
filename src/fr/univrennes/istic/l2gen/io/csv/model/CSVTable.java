package fr.univrennes.istic.l2gen.io.csv.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class CSVTable {

    private String name;
    private String[] header;
    private List<String[]> data;
    private List<CSVType> columnTypes;

    public CSVTable() {
        this.name = null;
        this.header = null;
        this.data = new ArrayList<>();
        this.columnTypes = new ArrayList<>();
    }

    public CSVTable(List<String[]> rows, boolean hasHeader) {
        this.name = null;
        this.header = hasHeader && rows.size() > 0 ? rows.get(0) : null;
        this.data = hasHeader && rows.size() > 0
                ? new ArrayList<>(rows.subList(1, rows.size()))
                : new ArrayList<>(rows);

        String[] typeSourceRow = rows.size() > 0 ? rows.get(0) : new String[0];
        this.columnTypes = new ArrayList<>(Stream.of(typeSourceRow)
                .map(CSVType::inferType)
                .toList());
    }

    public CSVTable(CSVRow header, List<CSVRow> rows) {
        this.name = null;
        this.header = header != null ? header.toRawArray() : null;
        this.data = new ArrayList<>();
        this.columnTypes = new ArrayList<>(rows.get(0).streamRaw()
                .map(CSVType::inferType)
                .toList());

        if (header != null) {
            addRow(header);
        }
        for (CSVRow row : rows) {
            addRow(row);
        }
    }

    public CSVTable(CSVTable other) {
        this.name = other.name;
        this.header = other.header != null ? other.header.clone() : null;
        this.data = new ArrayList<>(other.data);
        this.columnTypes = new ArrayList<>(other.columnTypes);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setHeader(CSVRow newHeader) {
        if (newHeader == null) {
            header = null;
            return;
        }
        header = newHeader.toRawArray();
    }

    public Optional<CSVRow> getHeader() {
        if (header == null || data.size() == 0) {
            return Optional.empty();
        }
        return Optional.of(new CSVRow(header));
    }

    public String[] getRawHeader() {
        return header;
    }

    public Optional<String> getColumnName(int colIndex) {
        if (header == null || colIndex < 0 || colIndex >= getColumnCount()) {
            return Optional.empty();
        }

        return Optional.ofNullable(header[colIndex]);
    }

    public CSVType getColumnType(int colIndex) {
        if (colIndex < 0 || colIndex >= columnTypes.size()) {
            throw new IndexOutOfBoundsException("Column index out of bounds");
        }
        return columnTypes.get(colIndex);
    }

    public void setColumnType(int colIndex, CSVType type) {
        if (colIndex < 0 || colIndex >= columnTypes.size()) {
            throw new IndexOutOfBoundsException("Column index out of bounds");
        }
        columnTypes.set(colIndex, type);
    }

    public int getRowCount() {
        return data.size();
    }

    public int getColumnCount() {
        return data.size() > 0 ? data.get(0).length : 0;
    }

    public CSVRow getRow(int index) {
        return new CSVRow(this, index);
    }

    public CSVColumn getColumn(int colIndex) {
        return new CSVColumn(this, colIndex);
    }

    public void addRow(CSVRow row) {
        if (row.cells != null) {
            data.add(row.cells);
        } else {
            String[] newRow = new String[getColumnCount()];
            for (int i = 0; i < getColumnCount(); i++) {
                newRow[i] = row.getRawCell(i);
            }
            data.add(newRow);
        }
    }

    public void addColumn(CSVColumn column) {
        this.columnTypes.add(column.getType());

        for (int rowIndex = 0; rowIndex < getRowCount(); rowIndex++) {
            String[] existingRow = data.get(rowIndex);
            String[] expandedRow = new String[existingRow.length + 1];
            System.arraycopy(existingRow, 0, expandedRow, 0, existingRow.length);

            if (column.cells != null) {
                expandedRow[existingRow.length] = rowIndex < column.cells.length ? column.cells[rowIndex] : null;
            } else {
                expandedRow[existingRow.length] = column.getRawCell(rowIndex);
            }

            data.set(rowIndex, expandedRow);
        }
    }

    public void removeColumn(int colIndex) {
        if (colIndex < 0 || colIndex >= getColumnCount()) {
            return;
        }

        columnTypes.remove(colIndex);

        if (header != null) {
            String[] newHeader = new String[header.length - 1];
            System.arraycopy(header, 0, newHeader, 0, colIndex);
            System.arraycopy(header, colIndex + 1, newHeader, colIndex, header.length - colIndex - 1);
            header = newHeader;
        }

        for (int rowIndex = 0; rowIndex < data.size(); rowIndex++) {
            String[] existingRow = data.get(rowIndex);
            String[] newRow = new String[existingRow.length - 1];
            System.arraycopy(existingRow, 0, newRow, 0, colIndex);
            System.arraycopy(existingRow, colIndex + 1, newRow, colIndex, existingRow.length - colIndex - 1);
            data.set(rowIndex, newRow);
        }
    }

    public void removeRow(int rowIndex) {
        if (rowIndex >= 0 && rowIndex < getRowCount()) {
            data.remove(rowIndex);
        }
    }

    public Optional<String> getCell(int rowIndex, int colIndex) {
        return Optional.ofNullable(data.get(rowIndex)[colIndex]);
    }

    public String getRawCell(int rowIndex, int colIndex) {
        return data.get(rowIndex)[colIndex];
    }

    public List<String[]> getRawRows() {
        return data;
    }

    public List<CSVRow> getRows() {
        List<CSVRow> rows = new ArrayList<>();
        for (int i = 0; i < getRowCount(); i++) {
            rows.add(new CSVRow(this, i));
        }
        return rows;
    }
}