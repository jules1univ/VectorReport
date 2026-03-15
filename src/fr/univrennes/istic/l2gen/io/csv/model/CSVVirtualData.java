package fr.univrennes.istic.l2gen.io.csv.model;

import java.util.Optional;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public abstract class CSVVirtualData implements Iterable<Optional<String>> {
    protected CSVTable table;
    protected int virtualIndex;

    protected final String[] cells;

    public CSVVirtualData() {
        this.table = null;
        this.virtualIndex = -1;
        this.cells = null;
    }

    public CSVVirtualData(CSVTable table, int rowIndex) {
        this.table = table;
        this.virtualIndex = rowIndex;
        this.cells = null;
    }

    public CSVVirtualData(String[] cells) {
        this.table = null;
        this.virtualIndex = -1;
        this.cells = cells;
    }

    public abstract Optional<String> getCell(int index);

    public abstract String getRawCell(int index);

    public abstract int size();

    public Stream<String> streamRaw() {
        if (cells != null) {
            return Stream.of(cells);
        }
        return IntStream.range(0, size()).mapToObj(this::getRawCell);
    }

    public <T> Stream<T> streamTyped(Class<T> clazz) {
        return streamRaw()
                .map(cell -> CSVType.parseValue(cell, clazz))
                .filter(value -> value != null);
    }

    public String[] toRawArray() {
        if (cells != null) {
            return cells;
        }

        String[] result = new String[size()];
        for (int i = 0; i < size(); i++) {
            result[i] = getRawCell(i);
        }
        return result;
    }

    public Optional<String>[] toArray() {
        @SuppressWarnings("unchecked")
        Optional<String>[] result = (Optional<String>[]) new Optional<?>[size()];
        for (int i = 0; i < size(); i++) {
            result[i] = getCell(i);
        }
        return result;
    }
}
