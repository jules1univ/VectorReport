package fr.univrennes.istic.l2gen.application.core.services.converter;

import java.util.List;
import java.util.Map;

import fr.univrennes.istic.l2gen.application.core.services.IService;
import fr.univrennes.istic.l2gen.io.csv.model.CSVTable;
import fr.univrennes.istic.l2gen.svg.color.Color;
import fr.univrennes.istic.l2gen.visustats.data.DataGroup;
import fr.univrennes.istic.l2gen.visustats.data.DataSet;

public interface IConverterService extends IService {

    public DataSet createDataSet(CSVTable table, int valColIndex, String title, Color defaultColor);

    public DataSet createDataSet(CSVTable table, int valueColIndex, int labelColIndex, String title,
            Map<String, Color> colorMap, Color defaultColor);

    public DataSet createSummaryDataSet(CSVTable table, int categoryColIndex, int valueColIndex, String title,
            SummaryType type);

    public DataGroup createDataGroup(List<CSVTable> tables, int valueColIndex, int labelColIndex, String title,
            Map<String, Color> colorMap, Color defaultColor);

    public DataGroup createSummaryDataGroup(List<CSVTable> tables, int categoryColIndex, int valueColIndex,
            String title, SummaryType type);
}