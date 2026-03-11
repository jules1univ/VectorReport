package fr.univrennes.istic.l2gen.io.csv.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class CSVColumn<Type> {

    private CSVSubtype type;
    private Type[] cells;

    private int size;

    public CSVColumn(CSVSubtype type, Type[] cells) {
        this.type = type;
        this.cells = cells;
    }

    public Optional<Type> getCell(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException(index);

        }
        return Optional.ofNullable(cells[index]);
    }

    public CSVType getType() {
        return CSVType.from(type);
    }

    public CSVSubtype getSubtype() {
        return type;
    }

    public int size() {
        return size;
    }

    public List<Optional<Type>> getCells() {
        List<Optional<Type>> list = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            list.add(Optional.ofNullable(cells[i]));
        }
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
