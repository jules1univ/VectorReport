package fr.univrennes.istic.l2gen.application.core.filter.type;

import java.util.Optional;
import fr.univrennes.istic.l2gen.application.core.filter.IFilter;
import fr.univrennes.istic.l2gen.io.csv.model.CSVRow;
import fr.univrennes.istic.l2gen.io.csv.model.CSVSubtype;

public final class TypeFilter implements IFilter {

    private final int columnIndex;
    private final CSVSubtype valueType;

    public TypeFilter(int columnIndex, CSVSubtype valueType) {
        this.columnIndex = columnIndex;
        this.valueType = valueType;
    }

    public static TypeFilter name(String colName, CSVSubtype valueType, Optional<CSVRow> header) {
        if (header == null || header.isEmpty()) {
            throw new IllegalArgumentException("Header is required for column name filtering");
        }
        int idx = header.get().getCells().indexOf(Optional.of(colName));
        if (idx == -1) {
            throw new IllegalArgumentException("Column not found: " + colName);
        }
        return new TypeFilter(idx, valueType);
    }

    @Override
    public boolean matches(CSVRow row, Optional<CSVRow> header) {
        if (columnIndex < 0 || columnIndex >= row.size()) {
            return false;
        }

        CSVSubtype type = row.getCellSubtype(columnIndex);
        return type == valueType;
    }

}