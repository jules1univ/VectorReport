package fr.univrennes.istic.l2gen.io.csv.model;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

public final class CSVColumn {

    private static final int INTERN_SAMPLE_SIZE = 200;
    private static final double INTERN_CARDINALITY_THRESHOLD = 0.3;

    private CSVType type;
    private SharedColumnBuffer buffer;

    public CSVColumn(CSVType type, String[] cells) {
        this.type = type;
        String[] interned = internIfLowCardinality(Arrays.copyOf(cells, cells.length));
        this.buffer = new SharedColumnBuffer(interned, cells.length);
    }

    CSVColumn(CSVType type, String[] cells, int size) {
        this.type = type;
        this.buffer = new SharedColumnBuffer(cells, size);
    }

    private CSVColumn(CSVType type, SharedColumnBuffer sharedBuffer) {
        this.type = type;
        this.buffer = sharedBuffer.retain();
    }

    CSVColumn sharedCopy() {
        return new CSVColumn(type, buffer);
    }

    private void ensureWritable(int requiredSize) {
        if (buffer.isShared()) {
            buffer = buffer.cloneForWrite(requiredSize);
        }
    }

    private static String[] internIfLowCardinality(String[] rawCells) {
        if (rawCells.length == 0) {
            return rawCells;
        }
        int sampleSize = Math.min(INTERN_SAMPLE_SIZE, rawCells.length);
        Map<String, Integer> sampleCounts = new HashMap<>();
        for (int i = 0; i < sampleSize; i++) {
            String cell = rawCells[i];
            if (cell != null) {
                sampleCounts.merge(cell, 1, Integer::sum);
            }
        }
        double cardinality = (double) sampleCounts.size() / sampleSize;
        if (cardinality > INTERN_CARDINALITY_THRESHOLD) {
            return rawCells;
        }
        Map<String, String> internPool = new HashMap<>(sampleCounts.size() * 2);
        for (int i = 0; i < rawCells.length; i++) {
            String cell = rawCells[i];
            if (cell != null) {
                rawCells[i] = internPool.computeIfAbsent(cell, k -> k);
            }
        }
        return rawCells;
    }

    public String getRawCell(int index) {
        return buffer.get(index);
    }

    public CSVType getType() {
        return type;
    }

    public void setType(CSVType type) {
        this.type = type;
    }

    public Optional<String> getCell(int index) {
        return Optional.ofNullable(buffer.get(index));
    }

    public String[] getCells() {
        return buffer.rawCells();
    }

    public Stream<String> streamCells() {
        int size = buffer.size();
        return Arrays.stream(buffer.rawCells(), 0, size).filter(cell -> cell != null);
    }

    @SuppressWarnings("unchecked")
    public <T> Stream<T> streamCells(Class<T> clazz) {
        return streamCells()
                .map(cell -> {
                    Optional<T> value = CSVType.parseValue(cell, clazz);
                    return value.orElse(null);
                })
                .filter(value -> value != null);
    }

    public int size() {
        return buffer.size();
    }

    public boolean isEmpty() {
        int size = buffer.size();
        for (int i = 0; i < size; i++) {
            if (buffer.get(i) != null) {
                return false;
            }
        }
        return true;
    }

    void growTo(int newSize) {
        ensureWritable(newSize);
        buffer.growTo(newSize);
    }

    void setCell(int index, String value) {
        ensureWritable(buffer.size());
        buffer.set(index, value);
    }

    void removeRowAt(int rowIndex, int currentRowCount) {
        ensureWritable(currentRowCount);
        String[] cells = buffer.rawCells();
        System.arraycopy(cells, rowIndex + 1, cells, rowIndex, currentRowCount - rowIndex - 1);
        cells[currentRowCount - 1] = null;
        buffer.growTo(currentRowCount - 1);
    }
}