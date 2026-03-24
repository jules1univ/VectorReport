package fr.univrennes.istic.l2gen.application.core.filter;

public class Filter {

    private final FilterType type;
    private final int columnIndex;

    private boolean ascending;
    private int n;
    private double min, max;
    private String searchTerm;

    private Filter(FilterType type, int columnIndex) {
        this.type = type;
        this.columnIndex = columnIndex;
    }

    public static Filter sort(int columnIndex, boolean ascending) {
        Filter f = new Filter(FilterType.SORT, columnIndex);
        f.ascending = ascending;
        return f;
    }

    public static Filter topN(int columnIndex, int n) {
        Filter f = new Filter(FilterType.TOP_N, columnIndex);
        f.n = n;
        return f;
    }

    public static Filter bottomN(int columnIndex, int n) {
        Filter f = new Filter(FilterType.BOTTOM_N, columnIndex);
        f.n = n;
        return f;
    }

    public static Filter byRange(int columnIndex, double min, double max) {
        Filter f = new Filter(FilterType.RANGE, columnIndex);
        f.min = min;
        f.max = max;
        return f;
    }

    public static Filter showEmpty(int columnIndex) {
        return new Filter(FilterType.EMPTY, columnIndex);
    }

    public static Filter hideEmpty(int columnIndex) {
        return new Filter(FilterType.NOT_EMPTY, columnIndex);
    }

    public static Filter search(int columnIndex, String searchTerm) {
        Filter f = new Filter(FilterType.SEARCH, columnIndex);
        f.searchTerm = searchTerm;
        f.min = Double.NaN;
        f.max = Double.NaN;
        return f;
    }

    public FilterType getType() {
        return type;
    }

    public int getColumnIndex() {
        return columnIndex;
    }

    public boolean isAscending() {
        return ascending;
    }

    public int getN() {
        return n;
    }

    public double getMin() {
        return min;
    }

    public double getMax() {
        return max;
    }

    public String getSearchTerm() {
        return searchTerm;
    }
}