package fr.univrennes.istic.l2gen.io.csv.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class CSVRow {

    private String[] cells;
    private CSVSubtype[] subtypes;

    private int size;

    public CSVRow() {
        this.cells = new String[8];
        this.size = 0;
    }

    public CSVRow(String[] cells) {
        this.cells = cells;
        this.size = cells.length;
    }

    public Optional<String> getCell(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException(index);

        }
        return Optional.ofNullable(cells[index]);
    }

    public CSVType getCellType(int index) {
        return CSVType.from(getCellSubtype(index));
    }

    public CSVSubtype getCellSubtype(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException(index);
        }
        return subtypes != null && index < subtypes.length ? subtypes[index] : CSVSubtype.EMPTY;
    }

    public String getRawCell(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException(index);
        }
        return cells[index];
    }

    public void addCell(String value) {
        if (size == cells.length) {
            String[] grown = new String[(size * 3 / 2) + 1];
            System.arraycopy(cells, 0, grown, 0, size);
            cells = grown;
        }

        String stored = (value != null && value.isEmpty()) ? null : value;
        cells[size] = stored;

        if (subtypes == null) {
            subtypes = new CSVSubtype[cells.length];
        } else if (size >= subtypes.length) {
            CSVSubtype[] grownTypes = new CSVSubtype[(size * 3 / 2) + 1];
            System.arraycopy(subtypes, 0, grownTypes, 0, subtypes.length);
            subtypes = grownTypes;
        }

        subtypes[size] = detectSubtype(stored);
        size++;
    }

    private CSVSubtype detectSubtype(String value) {
        if (value == null) {
            return CSVSubtype.EMPTY;
        }
        if (CSVType.isBoolean(value)) {
            return CSVSubtype.BOOLEAN;
        }
        if (CSVType.isDate(value)) {
            return CSVSubtype.DATE;
        }
        if (CSVType.isEmail(value)) {
            return CSVSubtype.EMAIL;
        }
        if (CSVType.isUrl(value)) {
            return CSVSubtype.URL;
        }
        if (CSVType.isPercentage(value)) {
            return CSVSubtype.PERCENTAGE;
        }
        if (CSVType.isInteger(value)) {
            return CSVSubtype.INTEGER;
        }
        if (CSVType.isFloating(value)) {
            return CSVSubtype.FLOATING;
        }
        if (CSVType.isNumeric(value)) {
            return CSVSubtype.NUMERIC;
        }
        return CSVSubtype.STRING;
    }

    public void trimToSize() {
        if (size < cells.length) {
            String[] trimmed = new String[size];
            System.arraycopy(cells, 0, trimmed, 0, size);
            cells = trimmed;
        }
        if (subtypes != null && size < subtypes.length) {
            CSVSubtype[] trimmedTypes = new CSVSubtype[size];
            System.arraycopy(subtypes, 0, trimmedTypes, 0, size);
            subtypes = trimmedTypes;
        }
    }

    public int size() {
        return size;
    }

    public List<Optional<String>> getCells() {
        List<Optional<String>> list = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            list.add(Optional.ofNullable(cells[i]));
        }
        return list;
    }

    public List<CSVType> getCellTypes() {
        List<CSVType> types = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            types.add(getCellType(i));
        }
        return types;
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