package fr.univrennes.istic.l2gen.application.core.filter.builder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import fr.univrennes.istic.l2gen.application.core.filter.IFilter;
import fr.univrennes.istic.l2gen.application.core.filter.category.CategoryFilter;
import fr.univrennes.istic.l2gen.application.core.filter.comparaison.ComparisonFilter;
import fr.univrennes.istic.l2gen.application.core.filter.comparaison.ComparisonOperator;
import fr.univrennes.istic.l2gen.application.core.filter.group.ConditionOperator;
import fr.univrennes.istic.l2gen.application.core.filter.group.GroupFilter;
import fr.univrennes.istic.l2gen.application.core.filter.label.LabelFilter;
import fr.univrennes.istic.l2gen.application.core.filter.pattern.PatternFilter;
import fr.univrennes.istic.l2gen.application.core.filter.range.RangeFilter;
import fr.univrennes.istic.l2gen.application.core.filter.type.TypeFilter;
import fr.univrennes.istic.l2gen.io.csv.model.CSVRow;
import fr.univrennes.istic.l2gen.io.csv.model.CSVSubtype;
import fr.univrennes.istic.l2gen.io.csv.model.CSVTable;

public final class FilterBuilder {

    private final CSVTable table;
    private final List<IFilter> filters = new ArrayList<>();
    private ConditionOperator logic = ConditionOperator.AND;
    private String defaultColumn;

    public FilterBuilder(CSVTable table) {
        this.table = table;
    }

    public FilterBuilder category(String columnName) {
        this.defaultColumn = columnName;
        return this;
    }

    public FilterBuilder where(String column, ComparisonOperator operator, String value) {
        int idx = resolveColumn(column);
        filters.add(new ComparisonFilter(idx, operator, value));
        return this;
    }

    public FilterBuilder where(ComparisonOperator operator, String value) {
        if (defaultColumn == null) {
            throw new IllegalStateException("No default column set");
        }
        return where(defaultColumn, operator, value);
    }

    public FilterBuilder where_range(String column, Double min, Double max) {
        int idx = resolveColumn(column);
        filters.add(new RangeFilter(idx, min, max));
        return this;
    }

    public FilterBuilder where_range(Double min, Double max) {
        if (defaultColumn == null) {
            throw new IllegalStateException("No default column set");
        }
        return where_range(defaultColumn, min, max);
    }

    public FilterBuilder logic(ConditionOperator operator) {
        if (operator == null) {
            throw new IllegalArgumentException("Logic operator cannot be null");
        }
        this.logic = operator;
        return this;
    }

    public FilterBuilder in_category(String column, String categoryValue, String delimiter) {
        int idx = resolveColumn(column);
        CategoryFilter cf = new CategoryFilter(idx, delimiter);
        cf.acceptCategory(categoryValue);
        filters.add(cf);
        return this;
    }

    public FilterBuilder in_category(String column, String categoryValue) {
        return in_category(column, categoryValue, " ");
    }

    public FilterBuilder in_categories(String column, String... categories) {
        int idx = resolveColumn(column);
        CategoryFilter cf = new CategoryFilter(idx, " ");
        cf.acceptCategories(categories);
        filters.add(cf);
        return this;
    }

    public FilterBuilder in_labels(String column, List<String> labels) {
        int idx = resolveColumn(column);
        filters.add(new LabelFilter(idx, labels));
        return this;
    }

    public FilterBuilder matches_pattern(String column, String regex) {
        int idx = resolveColumn(column);
        filters.add(new PatternFilter(idx, regex));
        return this;
    }

    public FilterBuilder not_matches_pattern(String column, String regex) {
        int idx = resolveColumn(column);
        filters.add(new PatternFilter(idx, regex, true));
        return this;
    }

    public FilterBuilder is_type(String column, CSVSubtype type) {
        int idx = resolveColumn(column);
        filters.add(new TypeFilter(idx, type));
        return this;
    }

    public FilterBuilder add(IFilter filter) {
        if (filter == null) {
            throw new IllegalArgumentException("Filter cannot be null");
        }
        filters.add(filter);
        return this;
    }

    public CSVTable execute() {
        if (filters.isEmpty()) {
            return new CSVTable(table);
        }
        IFilter combined;
        if (filters.size() == 1) {
            combined = filters.get(0);
        } else {
            switch (logic) {
                case AND -> combined = GroupFilter.and(filters.toArray(new IFilter[0]));
                case OR -> combined = GroupFilter.or(filters.toArray(new IFilter[0]));
                case NOT -> {
                    if (filters.size() != 1) {
                        throw new IllegalStateException("NOT logic only supports a single filter");
                    }
                    combined = GroupFilter.not(filters.get(0));
                }
                default -> combined = GroupFilter.and(filters.toArray(new IFilter[0]));
            }
        }
        return combined.apply(table);
    }

    private int resolveColumn(String identifier) {
        try {
            return Integer.parseInt(identifier);
        } catch (NumberFormatException e) {
            if (table.getHeader().isPresent()) {
                CSVRow hdr = table.getHeader().get();
                int idx = hdr.getCells().indexOf(Optional.of(identifier));
                if (idx == -1) {
                    throw new IllegalArgumentException("Column not found: " + identifier);
                }
                return idx;
            } else {
                throw new IllegalArgumentException("Table has no header, column must be specified by index");
            }
        }
    }
}
