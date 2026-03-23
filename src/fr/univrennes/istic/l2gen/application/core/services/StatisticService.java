package fr.univrennes.istic.l2gen.application.core.services;

import fr.univrennes.istic.l2gen.application.VectorReport;
import fr.univrennes.istic.l2gen.application.core.table.DataTable;
import org.duckdb.DuckDBConnection;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.util.Optional;
import java.util.OptionalDouble;

public final class StatisticService {

    private StatisticService() {
    }

    private static String formatDouble(double value) {
        return String.format("%,.2f", value);
    }

    public static Optional<String> computeBase(DataTable table, int columnIndex, StaticsticAction action) {
        String function;
        switch (action) {
            case MIN:
                function = "MIN";
                break;
            case MAX:
                function = "MAX";
                break;
            case AVG:
                function = "AVG";
                break;
            case SUM:
                function = "SUM";
                break;
            default:
                throw new IllegalArgumentException("Unsupported action: " + action);
        }

        String ext = "";
        String query = "SELECT %s(TRY_CAST(REPLACE(%s, ',', '.') AS DOUBLE)) FROM '%s'";
        Object[] params = new Object[] {
                function, table.getTableColumnName(columnIndex), table.getTableName()
        };
        if (!table.isNumericColumn(columnIndex)) {
            ext = " length";
            query = "SELECT %s(LENGTH(%s)) FROM '%s' WHERE %s IS NOT NULL";
            params = new Object[] {
                    function, table.getTableColumnName(columnIndex), table.getTableName(),
                    table.getTableColumnName(columnIndex)
            };
        }

        Double val = executeDoubleQuery(String.format(query, params)).orElse(Double.NaN);
        if (Double.isNaN(val) || Double.isInfinite(val)) {
            return Optional.empty();
        } else {
            return Optional.of(formatDouble(val) + ext);
        }
    }

    public static OptionalDouble computeCorrelation(DataTable table, int columnIndexX, int columnIndexY) {
        if (!table.isNumericColumn(columnIndexX) || !table.isNumericColumn(columnIndexY)) {
            return OptionalDouble.empty();
        }

        String columnX = table.getTableColumnName(columnIndexX);
        String columnY = table.getTableColumnName(columnIndexY);
        String tableName = table.getTableName();

        String query = String.format(
                "SELECT CORR("
                        + "TRY_CAST(REPLACE(%s, ',', '.') AS DOUBLE), "
                        + "TRY_CAST(REPLACE(%s, ',', '.') AS DOUBLE)"
                        + ") FROM '%s'",
                columnX, columnY, tableName);

        return executeDoubleQuery(query);
    }

    public static OptionalDouble computeInformationGain(DataTable table, int columnIndex) {
        String columnName = table.getTableColumnName(columnIndex);
        String tableName = table.getTableName();

        String query = String.format(
                "WITH value_counts AS ("
                        + "  SELECT %s AS value, COUNT(*) AS cnt FROM '%s' GROUP BY %s"
                        + "), "
                        + "total AS (SELECT SUM(cnt) AS total_count FROM value_counts) "
                        + "SELECT -SUM((cnt * 1.0 / total_count) * LN(cnt * 1.0 / total_count)) "
                        + "FROM value_counts CROSS JOIN total",
                columnName, tableName, columnName);

        return executeDoubleQuery(query);
    }

    public static OptionalDouble computeCoefficientOfVariation(DataTable table, int columnIndex) {
        if (!table.isNumericColumn(columnIndex)) {
            return OptionalDouble.empty();
        }

        String columnName = table.getTableColumnName(columnIndex);
        String tableName = table.getTableName();
        String castExpression = String.format("TRY_CAST(REPLACE(%s, ',', '.') AS DOUBLE)", columnName);

        String query = String.format(
                "SELECT CASE WHEN AVG(%s) = 0 THEN NULL "
                        + "ELSE STDDEV_SAMP(%s) / AVG(%s) END "
                        + "FROM '%s'",
                castExpression, castExpression, castExpression, tableName);

        return executeDoubleQuery(query);
    }

    public static OptionalDouble computeSkewness(DataTable table, int columnIndex) {
        if (!table.isNumericColumn(columnIndex)) {
            return OptionalDouble.empty();
        }

        String columnName = table.getTableColumnName(columnIndex);
        String tableName = table.getTableName();
        String castExpression = String.format("TRY_CAST(REPLACE(%s, ',', '.') AS DOUBLE)", columnName);

        String query = String.format(
                "SELECT SKEWNESS(%s) FROM '%s'",
                castExpression, tableName);

        return executeDoubleQuery(query);
    }

    public static OptionalDouble computeNullRate(DataTable table, int columnIndex) {
        String columnName = table.getTableColumnName(columnIndex);
        String tableName = table.getTableName();

        String query = String.format(
                "SELECT COUNT(*) FILTER (WHERE %s IS NULL) * 1.0 / NULLIF(COUNT(*), 0) "
                        + "FROM '%s'",
                columnName, tableName);

        return executeDoubleQuery(query);
    }

    public static OptionalDouble computeCardinalityRatio(DataTable table, int columnIndex) {
        String columnName = table.getTableColumnName(columnIndex);
        String tableName = table.getTableName();

        String query = String.format(
                "SELECT COUNT(DISTINCT %s) * 1.0 / NULLIF(COUNT(*), 0) "
                        + "FROM '%s'",
                columnName, tableName);

        return executeDoubleQuery(query);
    }

    public static OptionalDouble computeInterquartileRange(DataTable table, int columnIndex) {
        if (!table.isNumericColumn(columnIndex)) {
            return OptionalDouble.empty();
        }

        String columnName = table.getTableColumnName(columnIndex);
        String tableName = table.getTableName();
        String castExpression = String.format("TRY_CAST(REPLACE(%s, ',', '.') AS DOUBLE)", columnName);

        String query = String.format(
                "SELECT QUANTILE_CONT(%s, 0.75) - QUANTILE_CONT(%s, 0.25) "
                        + "FROM '%s'",
                castExpression, castExpression, tableName);

        return executeDoubleQuery(query);
    }

    private static OptionalDouble executeDoubleQuery(String query) {
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
        } catch (Exception e) {
            if (VectorReport.DEBUG_MODE) {
                e.printStackTrace();
            }
        }
        return OptionalDouble.empty();
    }

}