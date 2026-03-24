package fr.univrennes.istic.l2gen.application.core.services;

import org.duckdb.DuckDBConnection;

import fr.univrennes.istic.l2gen.application.VectorReport;
import fr.univrennes.istic.l2gen.application.core.table.DataTable;
import fr.univrennes.istic.l2gen.application.core.table.DataType;

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
                                + "null_padding=true,"
                                + "nullstr=['',' ']"
                                + ")"
                                + ") TO '%s' (FORMAT PARQUET, CODEC 'SNAPPY')",
                        formatIn,
                        formatOut));

                ResultSet countResult = statement.executeQuery(
                        String.format("SELECT COUNT(*) FROM '%s'", formatOut));
                countResult.next();
                long rowCount = countResult.getLong(1);

                ResultSet columnResult = statement.executeQuery(
                        String.format("DESCRIBE SELECT * FROM '%s'", formatOut));

                List<String> columnNames = new ArrayList<>();
                long columnCount = 0;
                while (columnResult.next()) {
                    columnCount++;

                    columnNames.add(columnResult.getString("column_name"));
                }

                List<DataType> columnTypes = TypeInferenceService.inferColumnTypes(statement, formatOut, columnNames);

                String alias = inputPath.getName().replaceFirst("[.][^.]+$", "");
                return new DataTable(outputPath, alias, columnNames, columnTypes, rowCount, columnCount);
            }
        } catch (Exception e) {
            if (VectorReport.DEBUG_MODE) {
                e.printStackTrace();
            }
            return null;
        }
    }
}