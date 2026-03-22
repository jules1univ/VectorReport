package fr.univrennes.istic.l2gen.application.core.services;

import org.duckdb.DuckDBConnection;

import fr.univrennes.istic.l2gen.application.VectorReport;
import fr.univrennes.istic.l2gen.application.core.table.DataTableInfo;

import java.io.File;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public final class ConverterService {

    public static DataTableInfo convert(File inputPath, File outputPath) {
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

                ResultSet columnCountResult = statement.executeQuery(
                        String.format("DESCRIBE SELECT * FROM '%s'", formatOut));
                long columnCount = 0;
                while (columnCountResult.next()) {
                    columnCount++;
                }

                String alias = inputPath.getName().replaceFirst("[.][^.]+$", "");
                return new DataTableInfo(outputPath, alias, rowCount, columnCount, outputPath.length());
            }
        } catch (Exception e) {
            if (VectorReport.DEBUG_MODE) {
                e.printStackTrace();
            }
            return null;
        }
    }
}