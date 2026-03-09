package fr.univrennes.istic.l2gen.application.core.services.filter;

import fr.univrennes.istic.l2gen.application.core.filter.IFilter;
import fr.univrennes.istic.l2gen.application.core.services.IService;
import fr.univrennes.istic.l2gen.io.csv.model.CSVTable;

import java.util.ArrayList;
import java.util.List;

public class FilterService implements IService {

    private final List<IFilter> filters = new ArrayList<>();

    public FilterService() {
    }

    public void add(IFilter filter) {
        this.filters.add(filter);
    }

    public void remove(IFilter filter) {
        this.filters.remove(filter);
    }

    public void clear() {
        this.filters.clear();
    }

    public List<IFilter> getAll() {
        return this.filters;
    }

    public CSVTable apply(CSVTable table) {
        CSVTable copy = new CSVTable(table);
        for (IFilter filter : filters) {
            copy = filter.apply(copy);
        }
        return copy;
    }

    public CSVTable apply(IFilter filter, CSVTable table) {
        return filter.apply(new CSVTable(table));
    }
}
