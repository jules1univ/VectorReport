package fr.univrennes.istic.l2gen.application.core.services;

import fr.univrennes.istic.l2gen.application.core.table.DataTable;
import org.duckdb.DuckDBConnection;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.OptionalDouble;

public final class StatisticService {

    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat(
            "#,##0.####", new DecimalFormatSymbols(Locale.FRANCE));

    private static final double NUMERIC_COLUMN_THRESHOLD = 0.5;

    private StatisticService() {
    }

    public static Optional<String> computeMin(DataTable table, int columnIndex) {
        String parquetPath = toParquetPath(table);
        String quotedColumn = quoteColumn(table.getColumnName(columnIndex));

        if (isNumericColumn(quotedColumn, parquetPath)) {
            return runScalarQuery(String.format(
                    "SELECT MIN(TRY_CAST(REPLACE(%s, ',', '.') AS DOUBLE)) FROM '%s'",
                    quotedColumn, parquetPath));
        }

        return runScalarQuery(String.format(
                "SELECT MIN(LENGTH(%s)) FROM '%s' WHERE %s IS NOT NULL",
                quotedColumn, parquetPath, quotedColumn))
                .map(value -> value + " car.");
    }

    public static Optional<String> computeMax(DataTable table, int columnIndex) {
        String parquetPath = toParquetPath(table);
        String quotedColumn = quoteColumn(table.getColumnName(columnIndex));

        if (isNumericColumn(quotedColumn, parquetPath)) {
            return runScalarQuery(String.format(
                    "SELECT MAX(TRY_CAST(REPLACE(%s, ',', '.') AS DOUBLE)) FROM '%s'",
                    quotedColumn, parquetPath));
        }

        return runScalarQuery(String.format(
                "SELECT MAX(LENGTH(%s)) FROM '%s' WHERE %s IS NOT NULL",
                quotedColumn, parquetPath, quotedColumn))
                .map(value -> value + " car.");
    }

    public static Optional<String> computeAvg(DataTable table, int columnIndex) {
        String parquetPath = toParquetPath(table);
        String quotedColumn = quoteColumn(table.getColumnName(columnIndex));

        if (isNumericColumn(quotedColumn, parquetPath)) {
            return runScalarQuery(String.format(
                    "SELECT AVG(TRY_CAST(REPLACE(%s, ',', '.') AS DOUBLE)) FROM '%s'",
                    quotedColumn, parquetPath));
        }

        return runScalarQuery(String.format(
                "SELECT AVG(LENGTH(%s)) FROM '%s' WHERE %s IS NOT NULL",
                quotedColumn, parquetPath, quotedColumn))
                .map(value -> value + " car.");
    }

    public static Optional<String> computeSum(DataTable table, int columnIndex) {
        String parquetPath = toParquetPath(table);
        String quotedColumn = quoteColumn(table.getColumnName(columnIndex));

        if (isNumericColumn(quotedColumn, parquetPath)) {
            return runScalarQuery(String.format(
                    "SELECT SUM(TRY_CAST(REPLACE(%s, ',', '.') AS DOUBLE)) FROM '%s'",
                    quotedColumn, parquetPath));
        }

        return runScalarQuery(String.format(
                "SELECT SUM(LENGTH(%s)) FROM '%s' WHERE %s IS NOT NULL",
                quotedColumn, parquetPath, quotedColumn))
                .map(value -> value + " car.");
    }

    public static OptionalDouble computeCorrelation(DataTable table, int columnIndexX, int columnIndexY) {
        String parquetPath = toParquetPath(table);
        String quotedX = quoteColumn(table.getColumnName(columnIndexX));
        String quotedY = quoteColumn(table.getColumnName(columnIndexY));

        if (!isNumericColumn(quotedX, parquetPath) || !isNumericColumn(quotedY, parquetPath)) {
            return OptionalDouble.empty();
        }

        String castX = String.format("TRY_CAST(REPLACE(%s, ',', '.') AS DOUBLE)", quotedX);
        String castY = String.format("TRY_CAST(REPLACE(%s, ',', '.') AS DOUBLE)", quotedY);

        String query = String.format(
                "SELECT CORR(%s, %s) FROM '%s' WHERE %s IS NOT NULL AND %s IS NOT NULL",
                castX, castY, parquetPath, quotedX, quotedY);

        try (DuckDBConnection connection = (DuckDBConnection) DriverManager.getConnection("jdbc:duckdb:")) {
            try (var statement = connection.createStatement()) {
                ResultSet resultSet = statement.executeQuery(query);
                if (resultSet.next()) {
                    double value = resultSet.getDouble(1);
                    if (resultSet.wasNull()) {
                        return OptionalDouble.empty();
                    }
                    return OptionalDouble.of(value);
                }
            }
        } catch (Exception ignored) {
        }
        return OptionalDouble.empty();
    }

    public static OptionalDouble computeInformationGain(DataTable table, int columnIndex) {
        String parquetPath = toParquetPath(table);
        String quotedColumn = quoteColumn(table.getColumnName(columnIndex));

        String frequencyQuery = String.format(
                "SELECT COUNT(*) AS frequency "
                        + "FROM '%s' "
                        + "WHERE %s IS NOT NULL "
                        + "GROUP BY %s",
                parquetPath, quotedColumn, quotedColumn);

        String totalQuery = String.format(
                "SELECT COUNT(*) FROM '%s' WHERE %s IS NOT NULL",
                parquetPath, quotedColumn);

        try (DuckDBConnection connection = (DuckDBConnection) DriverManager.getConnection("jdbc:duckdb:")) {
            try (var statement = connection.createStatement()) {

                ResultSet totalResult = statement.executeQuery(totalQuery);
                totalResult.next();
                long totalCount = totalResult.getLong(1);
                if (totalCount == 0) {
                    return OptionalDouble.empty();
                }

                try (var freqStatement = connection.createStatement()) {
                    ResultSet frequencyResult = freqStatement.executeQuery(frequencyQuery);
                    List<Long> frequencies = new ArrayList<>();
                    while (frequencyResult.next()) {
                        frequencies.add(frequencyResult.getLong("frequency"));
                    }

                    double entropy = 0.0;
                    for (long frequency : frequencies) {
                        double probability = (double) frequency / totalCount;
                        if (probability > 0.0) {
                            entropy -= probability * (Math.log(probability) / Math.log(2.0));
                        }
                    }
                    return OptionalDouble.of(entropy);
                }
            }
        } catch (Exception ignored) {
        }
        return OptionalDouble.empty();
    }

    public static OptionalDouble computeCoefficientOfVariation(DataTable table, int columnIndex) {
        String parquetPath = toParquetPath(table);
        String quotedColumn = quoteColumn(table.getColumnName(columnIndex));

        if (!isNumericColumn(quotedColumn, parquetPath)) {
            return OptionalDouble.empty();
        }

        String castValue = String.format("TRY_CAST(REPLACE(%s, ',', '.') AS DOUBLE)", quotedColumn);

        String query = String.format(
                "SELECT STDDEV(%s) / NULLIF(AVG(%s), 0) * 100.0 FROM '%s' WHERE %s IS NOT NULL",
                castValue, castValue, parquetPath, quotedColumn);

        return runDoubleQuery(query);
    }

    public static OptionalDouble computeMedian(DataTable table, int columnIndex) {
        String parquetPath = toParquetPath(table);
        String quotedColumn = quoteColumn(table.getColumnName(columnIndex));

        if (!isNumericColumn(quotedColumn, parquetPath)) {
            return OptionalDouble.empty();
        }

        String castValue = String.format("TRY_CAST(REPLACE(%s, ',', '.') AS DOUBLE)", quotedColumn);

        String query = String.format(
                "SELECT MEDIAN(%s) FROM '%s' WHERE %s IS NOT NULL",
                castValue, parquetPath, quotedColumn);

        return runDoubleQuery(query);
    }

    public static OptionalDouble computeSkewness(DataTable table, int columnIndex) {
        String parquetPath = toParquetPath(table);
        String quotedColumn = quoteColumn(table.getColumnName(columnIndex));

        if (!isNumericColumn(quotedColumn, parquetPath)) {
            return OptionalDouble.empty();
        }

        String castValue = String.format("TRY_CAST(REPLACE(%s, ',', '.') AS DOUBLE)", quotedColumn);

        String query = String.format(
                "SELECT SKEWNESS(%s) FROM '%s' WHERE %s IS NOT NULL",
                castValue, parquetPath, quotedColumn);

        return runDoubleQuery(query);
    }

    public static OptionalDouble computeNullRate(DataTable table, int columnIndex) {
        String parquetPath = toParquetPath(table);
        String quotedColumn = quoteColumn(table.getColumnName(columnIndex));

        String query = String.format(
                "SELECT (COUNT(*) - COUNT(%s)) * 1.0 / NULLIF(COUNT(*), 0) FROM '%s'",
                quotedColumn, parquetPath);

        return runDoubleQuery(query);
    }

    public static OptionalDouble computeCardinalityRatio(DataTable table, int columnIndex) {
        String parquetPath = toParquetPath(table);
        String quotedColumn = quoteColumn(table.getColumnName(columnIndex));

        String query = String.format(
                "SELECT COUNT(DISTINCT %s) * 1.0 / NULLIF(COUNT(%s), 0) FROM '%s'",
                quotedColumn, quotedColumn, parquetPath);

        return runDoubleQuery(query);
    }

    public static OptionalDouble computeInterquartileRange(DataTable table, int columnIndex) {
        String parquetPath = toParquetPath(table);
        String quotedColumn = quoteColumn(table.getColumnName(columnIndex));

        if (!isNumericColumn(quotedColumn, parquetPath)) {
            return OptionalDouble.empty();
        }

        String castValue = String.format("TRY_CAST(REPLACE(%s, ',', '.') AS DOUBLE)", quotedColumn);

        String query = String.format(
                "SELECT QUANTILE_CONT(%s, 0.75) - QUANTILE_CONT(%s, 0.25) FROM '%s' WHERE %s IS NOT NULL",
                castValue, castValue, parquetPath, quotedColumn);

        return runDoubleQuery(query);
    }

    private static boolean isNumericColumn(String quotedColumn, String parquetPath) {
        String query = String.format(
                "SELECT "
                        + "COUNT(*) FILTER (WHERE TRY_CAST(REPLACE(%s, ',', '.') AS DOUBLE) IS NOT NULL) AS numeric_count, "
                        + "COUNT(%s) AS non_null_count "
                        + "FROM '%s'",
                quotedColumn, quotedColumn, parquetPath);

        try (DuckDBConnection connection = (DuckDBConnection) DriverManager.getConnection("jdbc:duckdb:")) {
            try (var statement = connection.createStatement()) {
                ResultSet resultSet = statement.executeQuery(query);
                if (resultSet.next()) {
                    long numericCount = resultSet.getLong("numeric_count");
                    long nonNullCount = resultSet.getLong("non_null_count");
                    if (nonNullCount == 0) {
                        return false;
                    }
                    return (double) numericCount / nonNullCount >= NUMERIC_COLUMN_THRESHOLD;
                }
            }
        } catch (Exception ignored) {
        }
        return false;
    }

    private static Optional<String> runScalarQuery(String query) {
        try (DuckDBConnection connection = (DuckDBConnection) DriverManager.getConnection("jdbc:duckdb:")) {
            try (var statement = connection.createStatement()) {
                ResultSet resultSet = statement.executeQuery(query);
                if (resultSet.next()) {
                    double value = resultSet.getDouble(1);
                    if (resultSet.wasNull()) {
                        return Optional.empty();
                    }
                    return Optional.of(DECIMAL_FORMAT.format(value));
                }
            }
        } catch (Exception ignored) {
        }
        return Optional.empty();
    }

    private static OptionalDouble runDoubleQuery(String query) {
        try (DuckDBConnection connection = (DuckDBConnection) DriverManager.getConnection("jdbc:duckdb:")) {
            try (var statement = connection.createStatement()) {
                ResultSet resultSet = statement.executeQuery(query);
                if (resultSet.next()) {
                    double value = resultSet.getDouble(1);
                    if (resultSet.wasNull()) {
                        return OptionalDouble.empty();
                    }
                    return OptionalDouble.of(value);
                }
            }
        } catch (Exception ignored) {
        }
        return OptionalDouble.empty();
    }

    private static String toParquetPath(DataTable table) {
        return table.getInfo().getSource().getAbsolutePath().replace("\\", "/");
    }

    private static String quoteColumn(String columnName) {
        return "\"" + columnName.replace("\"", "\"\"") + "\"";
    }
}