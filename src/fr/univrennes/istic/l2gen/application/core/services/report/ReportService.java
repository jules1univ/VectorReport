package fr.univrennes.istic.l2gen.application.core.services.report;

import fr.univrennes.istic.l2gen.application.core.services.IService;
import fr.univrennes.istic.l2gen.io.csv.model.CSVRow;
import fr.univrennes.istic.l2gen.io.csv.model.CSVTable;
import fr.univrennes.istic.l2gen.visustats.data.DataGroup;
import fr.univrennes.istic.l2gen.visustats.data.DataSet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReportService implements IService {

    public ReportService() {
    }

    public Map<String, Object> generateBasicReport(CSVTable table) {
        Map<String, Object> report = new HashMap<>();

        report.put("totalRows", table.rows().size());
        report.put("totalColumns", table.rows().isEmpty() ? 0 : table.rows().get(0).values().size());
        report.put("hasHeader", table.header() != null);

        if (table.header() != null) {
            report.put("colNames", table.header().values());
        }

        return report;
    }

    public Map<String, Double> generateColumnStats(CSVTable table, int colIndex) {
        Map<String, Double> stats = new HashMap<>();

        List<Double> values = new ArrayList<>();

        for (CSVRow row : table.rows()) {
            if (colIndex < row.values().size()) {
                try {
                    double value = Double.parseDouble(row.cell(colIndex));
                    values.add(value);
                } catch (NumberFormatException e) {
                }
            }
        }

        if (values.isEmpty()) {
            stats.put("count", 0.0);
            stats.put("sum", 0.0);
            stats.put("average", 0.0);
            stats.put("min", 0.0);
            stats.put("max", 0.0);
        } else {
            double sum = values.stream().mapToDouble(Double::doubleValue).sum();
            double min = values.stream().mapToDouble(Double::doubleValue).min().orElse(0.0);
            double max = values.stream().mapToDouble(Double::doubleValue).max().orElse(0.0);

            stats.put("count", (double) values.size());
            stats.put("sum", sum);
            stats.put("average", sum / values.size());
            stats.put("min", min);
            stats.put("max", max);
        }

        return stats;
    }

    public Map<String, Integer> generateDistributionReport(CSVTable table, int colIndex) {
        Map<String, Integer> distr = new HashMap<>();

        for (CSVRow row : table.rows()) {
            if (colIndex < row.values().size()) {
                String value = row.cell(colIndex);
                distr.put(value, distr.getOrDefault(value, 0) + 1);
            }
        }

        return distr;
    }

    public Map<String, Double> generateCorrelationReport(CSVTable table,
            int colIndex1,
            int colIndex2) {
        Map<String, Double> report = new HashMap<>();

        List<Double> values1 = new ArrayList<>();
        List<Double> values2 = new ArrayList<>();

        for (CSVRow row : table.rows()) {
            if (colIndex1 < row.values().size() && colIndex2 < row.values().size()) {
                try {
                    double v1 = Double.parseDouble(row.cell(colIndex1));
                    double v2 = Double.parseDouble(row.cell(colIndex2));
                    values1.add(v1);
                    values2.add(v2);
                } catch (NumberFormatException e) {
                }
            }
        }

        if (values1.size() < 2) {
            report.put("correlation", 0.0);
            report.put("count", 0.0);
            return report;
        }

        double mean1 = values1.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
        double mean2 = values2.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);

        double numerator = 0.0;
        double denominator1 = 0.0;
        double denominator2 = 0.0;

        for (int i = 0; i < values1.size(); i++) {
            double diff1 = values1.get(i) - mean1;
            double diff2 = values2.get(i) - mean2;

            numerator += diff1 * diff2;
            denominator1 += diff1 * diff1;
            denominator2 += diff2 * diff2;
        }

        double correlation = numerator / Math.sqrt(denominator1 * denominator2);

        report.put("correlation", correlation);
        report.put("count", (double) values1.size());

        return report;
    }

    public Map<String, Object> generateDataSetReport(DataSet data) {
        Map<String, Object> report = new HashMap<>();

        report.put("title", data.title().name());
        report.put("size", data.size());
        report.put("sum", data.sum());
        report.put("min", data.min());
        report.put("max", data.max());
        report.put("average", data.average());

        return report;
    }

    public Map<String, Object> generateDataGroupReport(DataGroup data) {
        Map<String, Object> report = new HashMap<>();

        report.put("title", data.title().name());

        report.put("dataSets", data.size());
        report.put("totalValues", data.datasets().stream().mapToInt(DataSet::size).sum());

        report.put("sum", data.sum());
        report.put("min", data.min());
        report.put("max", data.max());

        List<Map<String, Object>> datasetReports = new ArrayList<>();
        for (int i = 0; i < data.size(); i++) {
            DataSet dataSet = data.get(i);
            datasetReports.add(generateDataSetReport(dataSet));
        }
        report.put("datasetReports", datasetReports);

        return report;
    }

    public Map<String, Object> compareTablesReport(CSVTable table1, CSVTable table2) {
        Map<String, Object> report = new HashMap<>();

        report.put("table1Rows", table1.rows().size());
        report.put("table2Rows", table2.rows().size());
        report.put("rowDifference", table2.rows().size() - table1.rows().size());

        int cols1 = table1.rows().isEmpty() ? 0 : table1.rows().get(0).values().size();
        int cols2 = table2.rows().isEmpty() ? 0 : table2.rows().get(0).values().size();

        report.put("table1Columns", cols1);
        report.put("table2Columns", cols2);
        report.put("columnDifference", cols2 - cols1);

        return report;
    }

}
