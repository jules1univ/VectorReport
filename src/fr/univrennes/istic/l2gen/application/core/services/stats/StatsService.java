package fr.univrennes.istic.l2gen.application.core.services.stats;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import fr.univrennes.istic.l2gen.application.core.services.IService;
import fr.univrennes.istic.l2gen.io.csv.model.CSVColumn;
import fr.univrennes.istic.l2gen.io.csv.model.CSVTable;
import fr.univrennes.istic.l2gen.io.csv.model.CSVType;

public final class StatsService implements IService {

    public Optional<String> getColumnMin(CSVTable table, int colIndex) {
        CSVColumn column = table.getColumn(colIndex);
        CSVType type = column.getType();
        if (type == CSVType.EMPTY)
            return Optional.empty();

        switch (type) {
            case BOOLEAN:
                return Optional.of(computeBooleanMajority(column));
            case DATE:
                return column.streamCells(LocalDate.class)
                        .mapToLong(LocalDate::toEpochDay)
                        .min()
                        .stream()
                        .mapToObj(epochDay -> LocalDate.ofEpochDay(epochDay).toString())
                        .findFirst();
            case STRING:
            case URL:
                return column.streamCells()
                        .mapToInt(String::length)
                        .min()
                        .stream()
                        .mapToObj(length -> length + " length")
                        .findFirst();
            case DOUBLE:
            case INTEGER:
            case PERCENTAGE:
                return computeNumericStat(column, type, NumericOperation.MIN);
            default:
                return Optional.empty();
        }
    }

    public Optional<String> getColumnMax(CSVTable table, int colIndex) {
        CSVColumn column = table.getColumn(colIndex);
        CSVType type = column.getType();
        if (type == CSVType.EMPTY)
            return Optional.empty();

        switch (type) {
            case BOOLEAN:
                return Optional.of(computeBooleanMajority(column));
            case DATE:
                return column.streamCells(LocalDate.class)
                        .mapToLong(LocalDate::toEpochDay)
                        .max()
                        .stream()
                        .mapToObj(epochDay -> LocalDate.ofEpochDay(epochDay).toString())
                        .findFirst();
            case STRING:
            case URL:
                return column.streamCells()
                        .mapToInt(String::length)
                        .max()
                        .stream()
                        .mapToObj(length -> length + " length")
                        .findFirst();
            case DOUBLE:
            case INTEGER:
            case PERCENTAGE:
                return computeNumericStat(column, type, NumericOperation.MAX);
            default:
                return Optional.empty();
        }
    }

    public Optional<String> getColumnAvg(CSVTable table, int colIndex) {
        CSVColumn column = table.getColumn(colIndex);
        CSVType type = column.getType();
        if (type == CSVType.EMPTY)
            return Optional.empty();

        return computeNumericStat(column, type, NumericOperation.AVG);
    }

    public Optional<String> getColumnSum(CSVTable table, int colIndex) {
        CSVColumn column = table.getColumn(colIndex);
        CSVType type = column.getType();
        if (type == CSVType.EMPTY)
            return Optional.empty();

        return computeNumericStat(column, type, NumericOperation.SUM);
    }

    public double getCorrelation(CSVTable table, int colIndex1, int colIndex2, CorrelationType corrType) {
        CSVColumn column1 = table.getColumn(colIndex1);
        CSVColumn column2 = table.getColumn(colIndex2);
        CSVType type = column1.getType();

        if (type != column2.getType())
            return 0;

        switch (type) {
            case DOUBLE:
            case INTEGER:
            case PERCENTAGE:
                return computeNumericCorrelation(column1, column2, corrType);
            case DATE:
                return computeDateCorrelation(column1, column2, corrType);
            case STRING:
            case URL:
                return computeStringCorrelation(column1, column2, corrType);
            default:
                return 0;
        }
    }

    private double computeNumericCorrelation(CSVColumn column1, CSVColumn column2, CorrelationType corrType) {
        double[] values1 = column1.streamCells(Double.class).mapToDouble(Double::doubleValue).toArray();
        double[] values2 = column2.streamCells(Double.class).mapToDouble(Double::doubleValue).toArray();
        return dispatchCorrelation(trimToSharedLength(values1, values2)[0], trimToSharedLength(values1, values2)[1],
                corrType);
    }

    private double computeDateCorrelation(CSVColumn column1, CSVColumn column2, CorrelationType corrType) {
        double[] epochDays1 = column1.streamCells(LocalDate.class).mapToDouble(LocalDate::toEpochDay).toArray();
        double[] epochDays2 = column2.streamCells(LocalDate.class).mapToDouble(LocalDate::toEpochDay).toArray();
        return dispatchCorrelation(trimToSharedLength(epochDays1, epochDays2)[0],
                trimToSharedLength(epochDays1, epochDays2)[1], corrType);
    }

    private double computeStringCorrelation(CSVColumn column1, CSVColumn column2, CorrelationType corrType) {
        List<String> strings1 = column1.streamCells().collect(Collectors.toList());
        List<String> strings2 = column2.streamCells().collect(Collectors.toList());

        int sharedLength = Math.min(strings1.size(), strings2.size());
        strings1 = strings1.subList(0, sharedLength);
        strings2 = strings2.subList(0, sharedLength);

        Map<String, Integer> encodingMap = buildStringEncodingMap(strings1, strings2);

        double[] encoded1 = strings1.stream().mapToDouble(encodingMap::get).toArray();
        double[] encoded2 = strings2.stream().mapToDouble(encodingMap::get).toArray();

        return dispatchCorrelation(encoded1, encoded2, corrType);
    }

    private double dispatchCorrelation(double[] values1, double[] values2, CorrelationType corrType) {
        switch (corrType) {
            case PEARSON:
                return calculatePearsonCorrelation(values1, values2);
            case SPEARMAN:
                return calculateSpearmanCorrelation(values1, values2);
            case KENDALL:
                return calculateKendallCorrelation(values1, values2);
            default:
                return 0;
        }
    }

    private double calculatePearsonCorrelation(double[] xValues, double[] yValues) {
        int n = xValues.length;
        if (n == 0)
            return 0;

        double sumX = 0, sumY = 0, sumXSquared = 0, sumYSquared = 0, sumXY = 0;
        for (int index = 0; index < n; index++) {
            sumX += xValues[index];
            sumY += yValues[index];
            sumXSquared += xValues[index] * xValues[index];
            sumYSquared += yValues[index] * yValues[index];
            sumXY += xValues[index] * yValues[index];
        }

        double numerator = n * sumXY - sumX * sumY;
        double denominator = Math.sqrt((n * sumXSquared - sumX * sumX) * (n * sumYSquared - sumY * sumY));
        return denominator == 0 ? 0 : numerator / denominator;
    }

    private double calculateSpearmanCorrelation(double[] xValues, double[] yValues) {
        if (xValues.length == 0)
            return 0;
        return calculatePearsonCorrelation(rank(xValues), rank(yValues));
    }

    private double calculateKendallCorrelation(double[] xValues, double[] yValues) {
        int n = xValues.length;
        if (n == 0)
            return 0;

        long concordantPairs = 0, discordantPairs = 0;
        for (int i = 0; i < n - 1; i++) {
            for (int j = i + 1; j < n; j++) {
                double deltaX = xValues[i] - xValues[j];
                double deltaY = yValues[i] - yValues[j];
                if (deltaX * deltaY > 0) {
                    concordantPairs++;
                } else if (deltaX * deltaY < 0) {
                    discordantPairs++;
                }
            }
        }

        long totalPairs = concordantPairs + discordantPairs;
        return totalPairs == 0 ? 0 : (double) (concordantPairs - discordantPairs) / totalPairs;
    }

    private Optional<String> computeNumericStat(CSVColumn column, CSVType type, NumericOperation operation) {
        Class<?> columnClass = CSVType.fromType(type).orElse(String.class);
        if (!Double.class.equals(columnClass) && !Number.class.isAssignableFrom(columnClass)) {
            return Optional.empty();
        }

        double result;
        switch (operation) {
            case MIN:
                result = column.streamCells(Double.class).mapToDouble(Double::doubleValue).min().orElse(0);
                break;
            case MAX:
                result = column.streamCells(Double.class).mapToDouble(Double::doubleValue).max().orElse(0);
                break;
            case AVG:
                result = column.streamCells(Double.class).mapToDouble(Double::doubleValue).average().orElse(0);
                break;
            case SUM:
                result = column.streamCells(Double.class).mapToDouble(Double::doubleValue).sum();
                break;
            default:
                return Optional.empty();
        }

        return Optional.of(formatNumericValue(result, type));
    }

    private String formatNumericValue(double value, CSVType type) {
        return String.format("%.2f%s", value, type == CSVType.PERCENTAGE ? "%" : "");
    }

    private String computeBooleanMajority(CSVColumn column) {
        long trueCount = column.streamCells(Boolean.class).filter(Boolean::booleanValue).count();
        return trueCount >= column.size() / 2 ? "true" : "false";
    }

    private double[][] trimToSharedLength(double[] values1, double[] values2) {
        int sharedLength = Math.min(values1.length, values2.length);
        return new double[][] {
                Arrays.copyOf(values1, sharedLength),
                Arrays.copyOf(values2, sharedLength)
        };
    }

    private Map<String, Integer> buildStringEncodingMap(List<String> strings1, List<String> strings2) {
        Map<String, Integer> encodingMap = new HashMap<>();
        int encodedIndex = 0;
        for (String value : strings1) {
            if (!encodingMap.containsKey(value))
                encodingMap.put(value, encodedIndex++);
        }
        for (String value : strings2) {
            if (!encodingMap.containsKey(value))
                encodingMap.put(value, encodedIndex++);
        }
        return encodingMap;
    }

    private double[] rank(double[] values) {
        int n = values.length;
        double[] ranks = new double[n];
        Integer[] sortedIndices = new Integer[n];
        for (int index = 0; index < n; index++)
            sortedIndices[index] = index;
        Arrays.sort(sortedIndices, Comparator.comparingDouble(index -> values[index]));

        int groupStart = 0;
        while (groupStart < n) {
            int groupEnd = groupStart;
            while (groupEnd < n - 1 && values[sortedIndices[groupEnd]] == values[sortedIndices[groupEnd + 1]]) {
                groupEnd++;
            }
            double averageRank = (groupStart + groupEnd) / 2.0 + 1;
            for (int index = groupStart; index <= groupEnd; index++) {
                ranks[sortedIndices[index]] = averageRank;
            }
            groupStart = groupEnd + 1;
        }

        return ranks;
    }
}