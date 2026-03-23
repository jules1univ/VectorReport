package fr.univrennes.istic.l2gen.application.core.services;

import org.duckdb.DuckDBConnection;

import fr.univrennes.istic.l2gen.application.VectorReport;
import fr.univrennes.istic.l2gen.application.core.table.DataTable;

import java.io.File;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public final class ConverterService {

    public static DataTable convert(File inputPath, File outputPath) {
        String formatIn = inputPath.getAbsolutePath().replace("\\", "/");
        String formatOut = outputPath.getAbsolutePath().replace("\\", "/");

        try (DuckDBConnection connection = (DuckDBConnection) DriverManager.getConnection("jdbc:duckdb:")) {
            try (Statement statement = connection.createStatement()) {

                statement.execute(String.format(
                        "COPY ("
                                + "SELECT * FROM read_csv('%s', "
                                + "header=true, "
                                + "all_varchar=true, "
                                + "ignore_errors=true, "
                                + "null_padding=true"
                                + ")"
                                + ") TO '%s' (FORMAT PARQUET, CODEC 'SNAPPY')",
                        formatIn,
                        formatOut));

                ResultSet countResult = statement.executeQuery(
                        String.format("SELECT COUNT(*) FROM '%s'", formatOut));
                countResult.next();
                long rowCount = countResult.getLong(1);

                List<String> columnNames = new ArrayList<>();
                ResultSet columnCountResult = statement.executeQuery(
                        String.format("DESCRIBE SELECT * FROM '%s'", formatOut));
                long columnCount = 0;
                while (columnCountResult.next()) {
                    columnCount++;
                    columnNames.add(columnCountResult.getString("column_name"));
                }

                String alias = inputPath.getName().replaceFirst("[.][^.]+$", "");
                return new DataTable(outputPath, alias, columnNames, rowCount, columnCount);
            }
        } catch (Exception e) {
            if (VectorReport.DEBUG_MODE) {
                e.printStackTrace();
            }
            return null;
        }
    }
}