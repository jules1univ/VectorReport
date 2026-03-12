package fr.univrennes.istic.l2gen.io.csv.model;

import java.util.Arrays;

final class SharedColumnBuffer {

    private String[] cells;
    private int size;
    private int refCount;

    public SharedColumnBuffer(String[] cells, int size) {
        this.cells = cells;
        this.size = size;
        this.refCount = 1;
    }

    public SharedColumnBuffer retain() {
        refCount++;
        return this;
    }

    public void release() {
        refCount--;
    }

    public boolean isShared() {
        return refCount > 1;
    }

    public String get(int index) {
        return cells[index];
    }

    public void set(int index, String value) {
        cells[index] = value;
    }

    public int size() {
        return size;
    }

    public String[] rawCells() {
        return cells;
    }

    public SharedColumnBuffer cloneForWrite(int newSize) {
        String[] copy = Arrays.copyOf(cells, Math.max(newSize, (int) (newSize * 1.5) + 1));
        SharedColumnBuffer cloned = new SharedColumnBuffer(copy, newSize);
        release();
        return cloned;
    }

    public void growTo(int newSize) {
        if (newSize > cells.length) {
            cells = Arrays.copyOf(cells, Math.max(newSize, (int) (cells.length * 1.5) + 1));
        }
        size = newSize;
    }
}