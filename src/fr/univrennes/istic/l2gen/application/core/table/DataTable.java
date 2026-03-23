package fr.univrennes.istic.l2gen.application.core.table;

import java.io.File;
import java.io.IOException;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.duckdb.DuckDBConnection;

public final class DataTable {
    private static final double NUMERIC_COLUMN_THRESHOLD = 0.5;

    private static final int BLOCK_SIZE = 10_000;
    private static final int MAX_CACHED_BLOCKS = 8;

    private final LinkedHashMap<Integer, Object[][]> blockCache;
    private final ExecutorService prefetchExecutor;

    private DuckDBConnection sharedConnection;

    private final String tableName;
    private final File tablePath;
    private String alias;

    private final List<String> columnNames;

    private final long rowCount;
    private final long columnCount;
    private final int blockCount;

    public static DataTable of(File file) throws IOException {
        if (!file.exists() || !file.isFile() || !file.canRead()) {
            throw new IOException("File not found or not readable: " + file.getAbsolutePath());
        }

        String absolutePath = file.getAbsolutePath().replace("\\", "/");
        List<String> columnNames = new ArrayList<>();

        try (DuckDBConnection connection = (DuckDBConnection) DriverManager.getConnection("jdbc:duckdb:")) {
            try (Statement statement = connection.createStatement()) {

                ResultSet rowCountResult = statement.executeQuery(
                        String.format("SELECT COUNT(*) FROM '%s'", absolutePath));
                rowCountResult.next();
                long rowCount = rowCountResult.getLong(1);

                ResultSet describeResult = statement.executeQuery(
                        String.format("DESCRIBE SELECT * FROM '%s'", absolutePath));
                long columnCount = 0;
                while (describeResult.next()) {
                    columnCount++;
                    columnNames.add(describeResult.getString("column_name"));
                }

                String alias = file.getName().replaceFirst("[.][^.]+$", "");
                return new DataTable(file, alias, columnNames, rowCount, columnCount);
            }
        } catch (Exception e) {
            throw new IOException("Failed to read metadata from: " + file.getAbsolutePath(), e);
        }
    }

    public DataTable(File file, String alias, List<String> columnNames, long rowCount, long columnCount)
            throws IOException {
        this.tablePath = file;
        this.tableName = file.getAbsolutePath().replace("\\", "/");
        this.alias = alias;

        this.columnNames = columnNames;
        this.columnCount = columnCount;

        this.rowCount = rowCount;
        this.blockCount = (int) Math.ceil((double) rowCount / BLOCK_SIZE);
        this.blockCache = new LinkedHashMap<>(MAX_CACHED_BLOCKS, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<Integer, Object[][]> eldest) {
                return size() > MAX_CACHED_BLOCKS;
            }
        };

        this.prefetchExecutor = Executors.newSingleThreadExecutor(runnable -> {
            Thread thread = new Thread(runnable, "DataTable-Prefetch");
            thread.setDaemon(true);
            return thread;
        });

        try {
            this.sharedConnection = (DuckDBConnection) DriverManager.getConnection("jdbc:duckdb:");
        } catch (Exception e) {
            throw new IOException("Failed to open DuckDB connection", e);
        }
    }

    public File getTablePath() {
        return tablePath;
    }

    public String getTableName() {
        return tableName;
    }

    public String getTableColumnName(int columnIndex) {
        if (columnIndex < 0 || columnIndex >= columnNames.size()) {
            return "";
        }
        return "\"" + getRawColumnName(columnIndex).replace("\"", "\"\"") + "\"";
    }

    public String getRawColumnName(int columnIndex) {
        if (columnIndex < 0 || columnIndex >= columnNames.size()) {
            return "";
        }
        return columnNames.get(columnIndex);
    }

    public boolean isNumericColumn(int columnIndex) {
        String query = String.format(
                "SELECT "
                        + "COUNT(*) FILTER (WHERE TRY_CAST(REPLACE(%s, ',', '.') AS DOUBLE) IS NOT NULL) AS numeric_count, "
                        + "COUNT(%s) AS non_null_count "
                        + "FROM '%s'",
                this.getTableColumnName(columnIndex), this.getTableColumnName(columnIndex), this.getTableName());

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

    public int getBlockCount() {
        return blockCount;
    }

    public long getBlockStartRow(int blockIndex) {
        return (long) blockIndex * BLOCK_SIZE;
    }

    public long getBlockRowCount(int blockIndex) {
        long start = getBlockStartRow(blockIndex);
        long remaining = getRowCount() - start;
        return Math.min(remaining, BLOCK_SIZE);
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        if (rowIndex < 0 || rowIndex >= getRowCount()) {
            return null;
        }
        int blockIndex = rowIndex / BLOCK_SIZE;
        int rowWithinBlock = rowIndex % BLOCK_SIZE;

        Object[][] blockData = getBlock(blockIndex);
        if (blockData == null || rowWithinBlock >= blockData.length) {
            return null;
        }
        if (columnIndex < 0 || columnIndex >= blockData[rowWithinBlock].length) {
            return null;
        }
        return blockData[rowWithinBlock][columnIndex];
    }

    public void prefetch(int startRow, int endRow) {
        int firstBlock = startRow / BLOCK_SIZE;
        int lastBlock = Math.max(0, (endRow - 1)) / BLOCK_SIZE;

        for (int blockIndex = firstBlock; blockIndex <= lastBlock; blockIndex++) {
            final int blockToFetch = blockIndex;
            synchronized (blockCache) {
                if (blockCache.containsKey(blockToFetch)) {
                    continue;
                }
            }
            prefetchExecutor.submit(() -> loadBlock(blockToFetch));
        }

        int nextBlock = lastBlock + 1;
        if (nextBlock < blockCount) {
            final int aheadBlock = nextBlock;
            prefetchExecutor.submit(() -> loadBlock(aheadBlock));
        }
    }

    public void close() {
        prefetchExecutor.shutdownNow();
        synchronized (blockCache) {
            blockCache.clear();
        }
        try {
            if (sharedConnection != null && !sharedConnection.isClosed()) {
                sharedConnection.close();
            }
        } catch (Exception ignored) {
        }
    }

    private Object[][] getBlock(int blockIndex) {
        synchronized (blockCache) {
            if (blockCache.containsKey(blockIndex)) {
                return blockCache.get(blockIndex);
            }
        }
        return loadBlock(blockIndex);
    }

    private Object[][] loadBlock(int blockIndex) {
        synchronized (blockCache) {
            if (blockCache.containsKey(blockIndex)) {
                return blockCache.get(blockIndex);
            }
        }

        long offsetRow = getBlockStartRow(blockIndex);
        long limitCount = getBlockRowCount(blockIndex);

        String query = String.format(
                "SELECT * FROM '%s' LIMIT %d OFFSET %d",
                tableName, limitCount, offsetRow);

        try {
            Object[][] blockData;
            synchronized (sharedConnection) {
                try (var statement = sharedConnection.createStatement()) {
                    ResultSet resultSet = statement.executeQuery(query);
                    ResultSetMetaData metaData = resultSet.getMetaData();
                    int fetchedColumnCount = metaData.getColumnCount();

                    List<Object[]> rows = new ArrayList<>((int) limitCount);
                    while (resultSet.next()) {
                        Object[] row = new Object[fetchedColumnCount];
                        for (int col = 1; col <= fetchedColumnCount; col++) {
                            row[col - 1] = resultSet.getObject(col);
                        }
                        rows.add(row);
                    }
                    blockData = rows.toArray(new Object[0][]);
                }
            }

            synchronized (blockCache) {
                blockCache.put(blockIndex, blockData);
            }
            return blockData;

        } catch (Exception e) {
            return new Object[0][];
        }
    }
}