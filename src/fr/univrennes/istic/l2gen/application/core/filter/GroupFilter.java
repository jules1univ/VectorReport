package fr.univrennes.istic.l2gen.application.core.filter;

import fr.univrennes.istic.l2gen.io.csv.model.CSVRow;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class GroupFilter implements IFilter {

    private final List<IFilter> filters;
    private final ConditionOperator operator;

    public GroupFilter(ConditionOperator operator, IFilter... filters) {
        this.operator = operator;
        this.filters = new ArrayList<>(Arrays.asList(filters));

        if (operator == ConditionOperator.NOT && filters.length != 1) {
            throw new IllegalArgumentException("NOT operator requires exactly one filter");
        }
        if (filters.length == 0) {
            throw new IllegalArgumentException("At least one filter is required");
        }
    }

    public static GroupFilter and(IFilter... filters) {
        return new GroupFilter(ConditionOperator.AND, filters);
    }

    public static GroupFilter or(IFilter... filters) {
        return new GroupFilter(ConditionOperator.OR, filters);
    }

    public static GroupFilter not(IFilter filter) {
        return new GroupFilter(ConditionOperator.NOT, filter);
    }

    public void addFilter(IFilter filter) {
        if (operator == ConditionOperator.NOT) {
            throw new UnsupportedOperationException("Cannot add filters to a NOT group");
        }
        filters.add(filter);
    }

    @Override
    public boolean matches(CSVRow row, Optional<CSVRow> header) {
        return switch (operator) {
            case AND -> filters.stream().allMatch(f -> f.matches(row, header));
            case OR -> filters.stream().anyMatch(f -> f.matches(row, header));
            case NOT -> !filters.get(0).matches(row, header);
        };
    }
}
