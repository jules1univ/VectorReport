package fr.univrennes.istic.l2gen.application.core.filter.category;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import fr.univrennes.istic.l2gen.application.core.filter.IFilter;
import fr.univrennes.istic.l2gen.io.csv.model.CSVRow;

public class CategoryFilter implements IFilter {
    private final int columnIndex;
    private final String delimiter;
    private final Set<String> acceptedCategories = new HashSet<>();
    private final boolean caseSensitive;

    public CategoryFilter(int columnIndex, String delimiter) {
        this(columnIndex, delimiter, true);
    }

    public CategoryFilter(int columnIndex, String delimiter, boolean caseSensitive) {
        this.columnIndex = columnIndex;
        this.delimiter = delimiter;
        this.caseSensitive = caseSensitive;
    }

    public static CategoryFilter name(String colName, String delimiter, CSVRow header) {
        if (header == null) {
            throw new IllegalArgumentException("Header is required for column name filtering");
        }
        int index = header.cells().indexOf(Optional.of(colName));
        if (index == -1) {
            throw new IllegalArgumentException("Column not found: " + colName);
        }
        return new CategoryFilter(index, delimiter);
    }

    public void acceptCategory(String category) {
        if (caseSensitive) {
            acceptedCategories.add(category);
        } else {
            acceptedCategories.add(category.toLowerCase());
        }
    }

    public void acceptCategories(String... categories) {
        for (String cat : categories) {
            acceptCategory(cat);
        }
    }

    @Override
    public boolean matches(CSVRow row, Optional<CSVRow> header) {
        if (columnIndex < 0 || columnIndex >= row.cells().size()) {
            return false;
        }

        Optional<String> cellValue = row.cell(columnIndex);
        if (!cellValue.isPresent() || cellValue.get().trim().isEmpty()) {
            return false;
        }

        String value = cellValue.get();
        String category = extractCategory(value);

        if (caseSensitive) {
            return acceptedCategories.contains(category);
        } else {
            return acceptedCategories.contains(category.toLowerCase());
        }
    }

    private String extractCategory(String value) {
        int delimiterIndex = value.indexOf(delimiter);
        if (delimiterIndex == -1) {
            return value;
        }
        return value.substring(0, delimiterIndex);
    }

}
