package fr.univrennes.istic.l2gen.application.core.services.converter;

import fr.univrennes.istic.l2gen.application.core.services.IService;
import fr.univrennes.istic.l2gen.io.csv.model.CSVRow;
import fr.univrennes.istic.l2gen.io.csv.model.CSVTable;
import fr.univrennes.istic.l2gen.svg.color.Color;
import fr.univrennes.istic.l2gen.visustats.data.DataGroup;
import fr.univrennes.istic.l2gen.visustats.data.DataSet;
import fr.univrennes.istic.l2gen.visustats.data.Label;
import fr.univrennes.istic.l2gen.visustats.data.Value;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConverterService implements IService {

    public DataSet createDataSet(CSVTable table, int valColIndex, String title, Color defaultColor) {
        Label label = new Label(title);
        DataSet data = new DataSet(label);

        for (CSVRow row : table.rows()) {
            if (valColIndex < row.cells().size() && !row.cell(valColIndex).isEmpty()) {
                try {
                    double value = Double.parseDouble(row.cell(valColIndex).get());
                    data.values().add(new Value(value, defaultColor));
                } catch (NumberFormatException e) {
                }
            }
        }
        return data;
    }

    public DataSet createDataSet(CSVTable table, int valueColIndex, int labelColIndex, String title,
            Map<String, Color> colorMap, Color defaultColor) {

        Label label = new Label(title);
        DataSet data = new DataSet(label);

        for (CSVRow row : table.rows()) {
            if (valueColIndex < row.cells().size() && labelColIndex < row.cells().size()
                    && !row.cell(valueColIndex).isEmpty() && !row.cell(labelColIndex).isEmpty()) {
                try {
                    double value = Double.parseDouble(row.cell(valueColIndex).get());
                    Color color = colorMap.getOrDefault(row.cell(labelColIndex).get(), defaultColor);

                    data.values().add(new Value(value, color));
                } catch (NumberFormatException e) {
                }
            }
        }

        return data;
    }

    public DataSet createSummaryDataSet(CSVTable table, int categoryColIndex, int valueColIndex, String title,
            SummaryType type) {
        Label label = new Label(title);
        DataSet data = new DataSet(label);

        Map<String, List<Double>> categoryValues = new HashMap<>();

        for (CSVRow row : table.rows()) {
            if (categoryColIndex < row.cells().size() && valueColIndex < row.cells().size()
                    && !row.cell(categoryColIndex).isEmpty() && !row.cell(valueColIndex).isEmpty()) {
                try {
                    String category = row.cell(categoryColIndex).get();
                    double value = Double.parseDouble(row.cell(valueColIndex).get());

                    categoryValues.computeIfAbsent(category, k -> new ArrayList<>()).add(value);
                } catch (NumberFormatException e) {
                }
            }
        }

        for (Map.Entry<String, List<Double>> entry : categoryValues.entrySet()) {
            List<Double> values = entry.getValue();
            double result = this.calculateSummary(values, type);

            data.values().add(new Value(result, Color.random()));
        }

        return data;
    }

    public DataGroup createDataGroup(List<CSVTable> tables, int valueColIndex, int labelColIndex, String title,
            Map<String, Color> colorMap, Color defaultColor) {

        Label label = new Label(title);
        DataGroup group = new DataGroup(label);

        for (CSVTable table : tables) {
            DataSet dataSet = createDataSet(table, valueColIndex, labelColIndex, title, colorMap, defaultColor);
            group.datasets().add(dataSet);
        }
        return group;
    }

    public DataGroup createSummaryDataGroup(List<CSVTable> tables, int categoryColIndex, int valueColIndex,
            String title,
            SummaryType type) {

        Label label = new Label(title);
        DataGroup group = new DataGroup(label);

        for (CSVTable table : tables) {
            DataSet dataSet = createSummaryDataSet(table, categoryColIndex, valueColIndex, title, type);
            group.datasets().add(dataSet);
        }
        return group;
    }

    private double calculateSummary(List<Double> values, SummaryType type) {
        if (values.isEmpty()) {
            return 0.0;
        }

        return switch (type) {
            case SUM -> values.stream().mapToDouble(Double::doubleValue).sum();
            case AVG -> values.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
            case MIN -> values.stream().mapToDouble(Double::doubleValue).min().orElse(0.0);
            case MAX -> values.stream().mapToDouble(Double::doubleValue).max().orElse(0.0);
            case COUNT -> values.size();
        };
    }
}
