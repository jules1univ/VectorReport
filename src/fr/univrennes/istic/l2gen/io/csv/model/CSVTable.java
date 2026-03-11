package fr.univrennes.istic.l2gen.io.csv.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

    public Optional<CSVRow> header() {
        return header;
    }

    public List<CSVRow> rows() {
        return rows;
    }

    public void setHeader(CSVRow header) {
        this.header = Optional.ofNullable(header);
    }

    public void addRow(CSVRow row) {
        rows.add(row);
    }

    public CSVRow row(int index) {
        return rows.get(index);
    }

    public String rangeToString(int offset, int limit) {
        StringBuilder sb = new StringBuilder();
        int columnCount = header.map(h -> h.cells().size())
                .orElse(rows.isEmpty() ? 0 : rows.get(0).cells().size());

        int[] columnWidths = new int[columnCount];

        header.ifPresent(h -> {
            for (int i = 0; i < h.cells().size(); i++) {
                columnWidths[i] = Math.max(columnWidths[i], h.cells().get(i).orElse("").length());
            }
        });

        for (int i = offset; i < Math.min(rows.size(), offset + limit); i++) {
            CSVRow row = rows.get(i);
            for (int j = 0; j < Math.min(row.cells().size(), columnCount); j++) {
                columnWidths[j] = Math.max(columnWidths[j], row.cells().get(j).orElse("").length());
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
                String value = i < h.cells().size() ? h.cells().get(i).orElse(CSVTable.DEFAULT_EMPTY_CELL)
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
                String value = j < row.cells().size() ? row.cells().get(j).orElse("") : "";
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
            if (colIndex < h.cells().size()) {
                sb.append(h.cells().get(colIndex).orElse(CSVTable.DEFAULT_EMPTY_CELL)).append("\n");
            }
        });
        for (int i = offset; i < Math.min(rows.size(), offset + limit); i++) {
            CSVRow row = rows.get(i);
            if (colIndex < row.cells().size()) {
                sb.append(row.cells().get(colIndex).orElse(CSVTable.DEFAULT_EMPTY_CELL)).append("\n");
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
            for (int i = offset; i < Math.min(h.cells().size(), offset + limit); i++) {
                String value = h.cells().get(i).orElse(CSVTable.DEFAULT_EMPTY_CELL);
                sb.append(String.format("Column %d: %s\n", i, value));
            }
            sb.append("----\n");
            return sb.toString() + rowToString(rowIndex, offset, limit);
        }

        CSVRow row = rows.get(rowIndex);
        StringBuilder sb = new StringBuilder();
        for (int i = offset; i < Math.min(row.cells().size(), offset + limit); i++) {
            String value = row.cells().get(i).orElse(CSVTable.DEFAULT_EMPTY_CELL);
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
