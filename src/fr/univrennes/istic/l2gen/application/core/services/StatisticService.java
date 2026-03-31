package fr.univrennes.istic.l2gen.application.core.services;

import fr.univrennes.istic.l2gen.application.VectorReport;
import fr.univrennes.istic.l2gen.application.core.table.DataTable;
import fr.univrennes.istic.l2gen.application.core.table.DataType;

import org.duckdb.DuckDBConnection;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Optional;
import java.util.OptionalDouble;

public final class StatisticService {

    private StatisticService() {
    }

    private static String formatDouble(double value) {
        return String.format("%,.2f", value);
    }

    private static String numericExpr(DataTable table, int columnIndex) {
        String col = table.getSQLColumnName(columnIndex);
        return String.format("TRY_CAST(REPLACE(%s, ',', '.') AS DOUBLE)", col);
    }

    public static Optional<String> computeBase(DataTable table, int columnIndex, StatisticOp action) {
        String query;

        switch (table.getColumnType(columnIndex)) {
            case DOUBLE, INTEGER -> {
                String expr = numericExpr(table, columnIndex);
                query = String.format(
                        "SELECT %s(val) FROM (" +
                                "SELECT %s AS val FROM '%s') WHERE val IS NOT NULL",
                        action.name(), expr, table.getSQLName());
            }
            case DATE -> {
                query = String.format(
                        "SELECT %s(%s) FROM '%s'",
                        action.name(),
                        table.getSQLColumnName(columnIndex),
                        table.getSQLName());

                String val = executeStringQuery(query).orElse("");
                return val.isEmpty() ? Optional.empty() : Optional.of(val);
            }
            default -> {
                query = String.format(
                        "SELECT %s(LENGTH(%s)) FROM '%s' WHERE %s IS NOT NULL",
                        action.name(),
                        table.getSQLColumnName(columnIndex),
                        table.getSQLName(),
                        table.getSQLColumnName(columnIndex));
            }
        }

        Double val = executeDoubleQuery(query).orElse(Double.NaN);
        if (Double.isNaN(val) || Double.isInfinite(val)) {
            return Optional.empty();
        } else {
            return Optional
                    .of(formatDouble(val) + (table.getColumnType(columnIndex) == DataType.STRING ? " length" : ""));
        }
    }

    public static OptionalDouble computeCorrelation(DataTable table, int columnIndexX, int columnIndexY) {
        if (!table.getColumnType(columnIndexX).isNumeric()
                || !table.getColumnType(columnIndexY).isNumeric()) {
            return OptionalDouble.empty();
        }

        String exprX = numericExpr(table, columnIndexX);
        String exprY = numericExpr(table, columnIndexY);

        String query = String.format(
                "SELECT CORR(x, y) FROM (" +
                        "SELECT %s AS x, %s AS y FROM '%s') " +
                        "WHERE x IS NOT NULL AND y IS NOT NULL",
                exprX, exprY, table.getSQLName());

        return executeDoubleQuery(query);
    }

    public static OptionalDouble computeCoefficientOfVariation(DataTable table, int columnIndex) {
        if (!table.getColumnType(columnIndex).isNumeric())
            return OptionalDouble.empty();

        String expr = numericExpr(table, columnIndex);

        String query = String.format(
                "SELECT CASE WHEN AVG(val) = 0 THEN NULL " +
                        "ELSE STDDEV_SAMP(val) / AVG(val) END " +
                        "FROM (SELECT %s AS val FROM '%s') WHERE val IS NOT NULL",
                expr, table.getSQLName());

        return executeDoubleQuery(query);
    }

    public static OptionalDouble computeSkewness(DataTable table, int columnIndex) {
        if (!table.getColumnType(columnIndex).isNumeric())
            return OptionalDouble.empty();

        String expr = numericExpr(table, columnIndex);

        String query = String.format(
                "SELECT SKEWNESS(val) FROM (" +
                        "SELECT %s AS val FROM '%s') WHERE val IS NOT NULL",
                expr, table.getSQLName());

        return executeDoubleQuery(query);
    }

    public static OptionalDouble computeInterquartileRange(DataTable table, int columnIndex) {
        if (!table.getColumnType(columnIndex).isNumeric())
            return OptionalDouble.empty();

        String expr = numericExpr(table, columnIndex);

        String query = String.format(
                "SELECT QUANTILE_CONT(val, 0.75) - QUANTILE_CONT(val, 0.25) " +
                        "FROM (SELECT %s AS val FROM '%s') WHERE val IS NOT NULL",
                expr, table.getSQLName());

        return executeDoubleQuery(query);
    }

    public static OptionalDouble computeNullRate(DataTable table, int columnIndex) {
        String col = table.getSQLColumnName(columnIndex);

        String query = String.format(
                "SELECT COUNT(*) FILTER (WHERE %s IS NULL) * 1.0 / NULLIF(COUNT(*), 0) FROM '%s'",
                col, table.getSQLName());

        return executeDoubleQuery(query);
    }

    public static OptionalDouble computeCardinalityRatio(DataTable table, int columnIndex) {
        String col = table.getSQLColumnName(columnIndex);

        String query = String.format(
                "SELECT COUNT(DISTINCT %s) * 1.0 / NULLIF(COUNT(*), 0) FROM '%s'",
                col, table.getSQLName());

        return executeDoubleQuery(query);
    }

    private static OptionalDouble executeDoubleQuery(String query) {
        try (DuckDBConnection connection = (DuckDBConnection) DriverManager.getConnection("jdbc:duckdb:");
                Statement statement = connection.createStatement()) {

            ResultSet resultSet = statement.executeQuery(query);
            if (resultSet.next()) {
                double value = resultSet.getDouble(1);
                return resultSet.wasNull() ? OptionalDouble.empty() : OptionalDouble.of(value);
            }

        } catch (Exception e) {
            if (VectorReport.DEBUG_MODE)
                e.printStackTrace();
        }
        return OptionalDouble.empty();
    }

    private static Optional<String> executeStringQuery(String query) {
        try (DuckDBConnection connection = (DuckDBConnection) DriverManager.getConnection("jdbc:duckdb:");
                Statement statement = connection.createStatement()) {

            ResultSet resultSet = statement.executeQuery(query);
            if (resultSet.next()) {
                String value = resultSet.getString(1);
                return resultSet.wasNull() ? Optional.empty() : Optional.of(value);
            }

        } catch (Exception e) {
            if (VectorReport.DEBUG_MODE)
                e.printStackTrace();
        }
        return Optional.empty();
    }

    public static String computeSummary(DataTable currentTable, int columnIndex) {
        StringBuilder summary = new StringBuilder();
        for (StatisticOp action : StatisticOp.values()) {
            Optional<String> result = computeBase(currentTable, columnIndex, action);
            if (result.isPresent()) {
                summary.append(action.getDisplayName()).append(": ").append(result.get()).append("\n");
            } else {
                summary.append(action.getDisplayName()).append(": N/A\n");
            }
        }

        OptionalDouble nullRate = computeNullRate(currentTable, columnIndex);
        if (nullRate.isPresent()) {
            summary.append("Null rate: ").append(formatDouble(nullRate.getAsDouble() * 100)).append("%\n");
        } else {
            summary.append("Null rate: N/A\n");
        }
        summary.append("Type: ").append(currentTable.getColumnType(columnIndex).name()).append("\n");
        summary.append("Native Type: ").append(currentTable.getColumnType(columnIndex).toSQL()).append("\n");

        return summary.toString();
    }
}