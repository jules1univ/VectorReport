package fr.univrennes.istic.l2gen.io.csv.model;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class CSVTable {
    public static final String DEFAULT_EMPTY_CELL = "(empty)";

    private Optional<CSVRow> header;
    private final List<CSVRow> rows;

    public CSVTable() {
        this.header = Optional.empty();
        this.rows = new ArrayList<>();
    }

    public CSVTable(CSVRow header, List<CSVRow> rows) {
        this.header = Optional.ofNullable(header);
        this.rows = rows;
    }

    public CSVTable(CSVTable other) {
        this.header = other.header;
        this.rows = new ArrayList<>(other.rows);
    }

    public Optional<CSVRow> getHeader() {
        return header;
    }

    public List<CSVRow> getRows() {
        return rows;
    }

    public CSVRow getRow(int index) {
        return rows.get(index);
    }

    public void addRow(CSVRow row) {
        rows.add(row);
    }

    public int getRowCount() {
        return rows.size();
    }

    public CSVType getColumnType(int colIndex) {
        return rows.isEmpty() ? CSVType.EMPTY : rows.get(0).getCellType(colIndex);
    }

    public CSVSubtype getColumnSubtype(int colIndex) {
        return rows.isEmpty() ? CSVSubtype.EMPTY : rows.get(0).getCellSubtype(colIndex);
    }

    public CSVColumn<String> getColumn(int colIndex) {
        CSVSubtype subtype = this.getColumnSubtype(colIndex);
        String[] cells = new String[rows.size()];
        for (int i = 0; i < rows.size(); i++) {
            CSVRow row = rows.get(i);
            cells[i] = row.getCell(colIndex).orElse(null);
        }
        return new CSVColumn<>(subtype, cells);
    }

    public <T> CSVColumn<T> getTypedColumn(int colIndex, Function<String, T> parser, Class<T> type) {
        CSVSubtype subtype = this.getColumnSubtype(colIndex);
        @SuppressWarnings("unchecked")
        T[] cells = (T[]) Array.newInstance(type, rows.size());

        for (int i = 0; i < rows.size(); i++) {
            CSVRow row = rows.get(i);
            String value = row.getCell(colIndex).orElse(null);
            try {
                cells[i] = value == null ? null : parser.apply(value);
            } catch (Exception e) {
                cells[i] = null;
            }
        }
        return new CSVColumn<>(subtype, cells);
    }

    public int getColumnCount() {
        if (header.isPresent()) {
            return header.get().getCells().size();
        } else if (!rows.isEmpty()) {
            return rows.get(0).getCells().size();
        } else {
            return 0;
        }
    }

    public void setHeader(CSVRow header) {
        this.header = Optional.ofNullable(header);
    }

    public String rangeToString(int offset, int limit) {
        StringBuilder sb = new StringBuilder();
        int columnCount = header.map(h -> h.getCells().size())
                .orElse(rows.isEmpty() ? 0 : rows.get(0).getCells().size());

        int[] columnWidths = new int[columnCount];

        header.ifPresent(h -> {
            for (int i = 0; i < h.getCells().size(); i++) {
                columnWidths[i] = Math.max(columnWidths[i], h.getCells().get(i).orElse("").length());
            }
        });

        for (int i = offset; i < Math.min(rows.size(), offset + limit); i++) {
            CSVRow row = rows.get(i);
            for (int j = 0; j < Math.min(row.getCells().size(), columnCount); j++) {
                columnWidths[j] = Math.max(columnWidths[j], row.getCells().get(j).orElse("").length());
            }
        }

        StringBuilder separator = new StringBuilder("+");
        for (int width : columnWidths) {
            separator.append("-".repeat(width + 2)).append("+");
        }
        separator.append("\n");

        if (header.isPresent()) {
            sb.append(separator);
            sb.append("|");
            CSVRow h = header.get();
            for (int i = 0; i < columnCount; i++) {
                String value = i < h.getCells().size() ? h.getCells().get(i).orElse(CSVTable.DEFAULT_EMPTY_CELL)
                        : CSVTable.DEFAULT_EMPTY_CELL;
                sb.append(" ").append(centerString(value, columnWidths[i])).append(" |");
            }
            sb.append("\n");
            sb.append(separator);
        }

        for (int i = offset; i < Math.min(rows.size(), offset + limit); i++) {
            CSVRow row = rows.get(i);
            if (header.isEmpty()) {
                sb.append(separator);
            }
            sb.append("|");
            for (int j = 0; j < columnCount; j++) {
                String value = j < row.getCells().size() ? row.getCells().get(j).orElse("") : "";
                sb.append(" ").append(centerString(value, columnWidths[j])).append(" |");
            }
            sb.append("\n");
        }
        sb.append(separator);

        return sb.toString();
    }

    public String columnToString(int colIndex, int offset, int limit) {
        StringBuilder sb = new StringBuilder();
        header.ifPresent(h -> {
            if (colIndex < h.getCells().size()) {
                sb.append(h.getCells().get(colIndex).orElse(CSVTable.DEFAULT_EMPTY_CELL)).append("\n");
            }
        });
        for (int i = offset; i < Math.min(rows.size(), offset + limit); i++) {
            CSVRow row = rows.get(i);
            if (colIndex < row.getCells().size()) {
                sb.append(row.getCells().get(colIndex).orElse(CSVTable.DEFAULT_EMPTY_CELL)).append("\n");
            }
        }
        return sb.toString();
    }

    public String rowToString(int rowIndex, int offset, int limit) {
        if (rowIndex < 0 || rowIndex >= rows.size()) {
            return "Row index out of bounds";
        }
        if (header.isPresent()) {
            StringBuilder sb = new StringBuilder();
            CSVRow h = header.get();
            for (int i = offset; i < Math.min(h.getCells().size(), offset + limit); i++) {
                String value = h.getCells().get(i).orElse(CSVTable.DEFAULT_EMPTY_CELL);
                sb.append(String.format("Column %d: %s\n", i, value));
            }
            sb.append("----\n");
            return sb.toString() + rowToString(rowIndex, offset, limit);
        }

        CSVRow row = rows.get(rowIndex);
        StringBuilder sb = new StringBuilder();
        for (int i = offset; i < Math.min(row.getCells().size(), offset + limit); i++) {
            String value = row.getCells().get(i).orElse(CSVTable.DEFAULT_EMPTY_CELL);
            sb.append(String.format("Column %d: %s\n", i, value));
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return rangeToString(0, this.rows.size());
    }

    private String centerString(String text, int width) {
        if (text.length() >= width) {
            return text;
        }
        int leftPadding = (width - text.length()) / 2;
        int rightPadding = width - text.length() - leftPadding;
        return " ".repeat(leftPadding) + text + " ".repeat(rightPadding);
    }
}
