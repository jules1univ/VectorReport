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
            if (valColIndex < row.values().size()) {
                try {
                    double value = Double.parseDouble(row.cell(valColIndex));
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
            if (valueColIndex < row.values().size() && labelColIndex < row.values().size()) {
                try {
                    double value = Double.parseDouble(row.cell(valueColIndex));
                    Color color = colorMap.getOrDefault(row.cell(labelColIndex), defaultColor);

                    data.values().add(new Value(value, color));
                } catch (NumberFormatException e) {
                }
            }
        }

        return data;
    }

    public DataGroup createDataGroup(CSVTable table, List<Integer> valueColIndices, String title) {
        Label label = new Label(title);
        DataGroup group = new DataGroup(label);

        CSVRow header = table.header();
        for (int i = 0; i < valueColIndices.size(); i++) {
            int colIndex = valueColIndices.get(i);
            String dataTitle = (header != null && colIndex < header.values().size())
                    ? header.cell(colIndex)
                    : "Series " + (i + 1);

            Color color = Color.random();
            DataSet data = this.createDataSet(table, colIndex, dataTitle, color);

            group.add(data);
            group.add(new Label(dataTitle, color));
        }

        return group;
    }

    public DataGroup createDataGroup(CSVTable table, int categoryColIndex,
            int valueColIndex, String title) {
        Label groupLabel = new Label(title);
        DataGroup dataGroup = new DataGroup(groupLabel);

        Map<String, List<Double>> categoryValues = new HashMap<>();

        for (CSVRow row : table.rows()) {
            if (categoryColIndex < row.values().size() && valueColIndex < row.values().size()) {
                try {
                    String category = row.cell(categoryColIndex);
                    double value = Double.parseDouble(row.cell(valueColIndex));

                    categoryValues.computeIfAbsent(category, k -> new ArrayList<>()).add(value);
                } catch (NumberFormatException e) {
                }
            }
        }

        for (Map.Entry<String, List<Double>> entry : categoryValues.entrySet()) {
            String category = entry.getKey();
            List<Double> values = entry.getValue();

            Color color = Color.random();
            Label dataSetLabel = new Label(category, color);
            DataSet dataSet = new DataSet(dataSetLabel);

            for (double value : values) {
                dataSet.values().add(new Value(value, color));
            }

            dataGroup.add(dataSet);
            dataGroup.add(new Label(category, color));
        }

        return dataGroup;
    }

    public DataSet createSummaryDataSet(CSVTable table, int categoryColIndex, int valueColIndex, String title,
            SummaryType type) {
        Label label = new Label(title);
        DataSet data = new DataSet(label);

        Map<String, List<Double>> categoryValues = new HashMap<>();

        for (CSVRow row : table.rows()) {
            if (categoryColIndex < row.values().size() && valueColIndex < row.values().size()) {
                try {
                    String category = row.cell(categoryColIndex);
                    double value = Double.parseDouble(row.cell(valueColIndex));

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

    public DataGroup createSummaryDataGroup(CSVTable table, int categoryColIndex, List<Integer> valueColIndices,
            String title,
            SummaryType type) {
        Label label = new Label(title);
        DataGroup group = new DataGroup(label);

        Map<String, List<Double>> categoryValues = new HashMap<>();

        for (CSVRow row : table.rows()) {
            if (categoryColIndex < row.values().size()) {
                String category = row.cell(categoryColIndex);

                for (int valueColIndex : valueColIndices) {
                    if (valueColIndex < row.values().size()) {
                        try {
                            double value = Double.parseDouble(row.cell(valueColIndex));
                            categoryValues.computeIfAbsent(category, k -> new ArrayList<>()).add(value);
                        } catch (NumberFormatException e) {
                        }
                    }
                }
            }
        }

        for (Map.Entry<String, List<Double>> entry : categoryValues.entrySet()) {
            List<Double> values = entry.getValue();
            double result = this.calculateSummary(values, type);

            group.add(
                    new DataSet(List.of(new Value(result, Color.random())),
                            new Label(entry.getKey(), Color.random())));
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
