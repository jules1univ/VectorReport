package fr.univrennes.istic.l2gen.application.core.filter.pattern;

import fr.univrennes.istic.l2gen.application.core.filter.IFilter;
import fr.univrennes.istic.l2gen.io.csv.model.CSVRow;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class PatternFilter implements IFilter {
    private final int columnIndex;
    private final Pattern pattern;
    private final boolean invert;

    public PatternFilter(int columnIndex, String regex) {
        this(columnIndex, regex, false);
    }

    public PatternFilter(int columnIndex, String regex, boolean invert) {
        this.columnIndex = columnIndex;
        this.invert = invert;
        try {
            this.pattern = Pattern.compile(regex);
        } catch (PatternSyntaxException e) {
            throw new IllegalArgumentException("Invalid regex pattern: " + regex, e);
        }
    }

    public static PatternFilter name(String colName, String regex, CSVRow header) {
        if (header == null) {
            throw new IllegalArgumentException("Header is required for column name filtering");
        }
        int index = header.getCells().indexOf(Optional.of(colName));
        if (index == -1) {
            throw new IllegalArgumentException("Column not found: " + colName);
        }
        return new PatternFilter(index, regex);
    }

    @Override
    public boolean matches(CSVRow row, Optional<CSVRow> header) {
        if (columnIndex < 0 || columnIndex >= row.getCells().size()) {
            return invert;
        }

        Optional<String> cell = row.getCell(columnIndex);
        String value = cell.orElse("");

        boolean matches = pattern.matcher(value).find();
        return invert ? !matches : matches;
    }

}
