package fr.univrennes.istic.l2gen.application.core.services.filter;

import java.util.List;

import fr.univrennes.istic.l2gen.application.core.filter.IFilter;
import fr.univrennes.istic.l2gen.application.core.services.IService;
import fr.univrennes.istic.l2gen.io.csv.model.CSVTable;

public interface IFilterService extends IService {

    public void add(IFilter filter);

    public void remove(IFilter filter);

    public void clear();

    public List<IFilter> getAll();

    public CSVTable apply(CSVTable table);

    public CSVTable apply(IFilter filter, CSVTable table);

    public CSVTable removeEmptyRows(CSVTable table);

    public CSVTable removeEmptyColumns(CSVTable table);

    public CSVTable sortByColumn(CSVTable table, int columnIndex, boolean ascending, boolean numeric);
}