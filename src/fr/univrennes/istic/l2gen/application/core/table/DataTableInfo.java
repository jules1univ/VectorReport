package fr.univrennes.istic.l2gen.application.core.table;

import java.io.File;
import java.sql.DriverManager;
import java.sql.ResultSet;

import org.duckdb.DuckDBConnection;

public class DataTableInfo {
    private final File source;

    private String alias;

    private final long rowCount;
    private final long columnCount;
    private final long byteSize;

    public DataTableInfo(File source, String alias, long rowCount, long columnCount, long size) {
        this.source = source;
        this.alias = alias;
        this.rowCount = rowCount;
        this.columnCount = columnCount;
        this.byteSize = size;
    }

    public static DataTableInfo of(File source) {
        if (!source.exists() || !source.isFile() || !source.canRead()) {
            return null;
        }

        String absolutePath = source.getAbsolutePath().replace("\\", "/");

        try (DuckDBConnection connection = (DuckDBConnection) DriverManager.getConnection("jdbc:duckdb:")) {
            try (var statement = connection.createStatement()) {

                ResultSet rowCountResult = statement.executeQuery(
                        String.format("SELECT COUNT(*) FROM '%s'", absolutePath));
                rowCountResult.next();
                long rowCount = rowCountResult.getLong(1);

                ResultSet describeResult = statement.executeQuery(
                        String.format("DESCRIBE SELECT * FROM '%s'", absolutePath));
                long columnCount = 0;
                while (describeResult.next()) {
                    columnCount++;
                }

                String alias = source.getName().replaceFirst("[.][^.]+$", "");
                return new DataTableInfo(source, alias, rowCount, columnCount, source.length());
            }
        } catch (Exception e) {
            return null;
        }
    }

    public File getSource() {
        return source;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public long getRowCount() {
        return rowCount;
    }

    public long getColumnCount() {
        return columnCount;
    }

    public long getSize() {
        return byteSize;
    }
}