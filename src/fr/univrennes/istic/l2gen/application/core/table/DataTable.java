package fr.univrennes.istic.l2gen.application.core.table;

import java.io.File;
import java.io.IOException;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.duckdb.DuckDBConnection;

public final class DataTable {

    private static final int BLOCK_SIZE = 10_000;
    private static final int MAX_CACHED_BLOCKS = 8;

    private final DataTableInfo info;
    private final String absoluteParquetPath;
    private final List<String> columnNames;
    private final int blockCount;

    private final LinkedHashMap<Integer, Object[][]> blockCache;
    private final ExecutorService prefetchExecutor;

    private DuckDBConnection sharedConnection;

    @SuppressWarnings("serial")
    private DataTable(DataTableInfo info, List<String> columnNames) throws IOException {
        this.info = info;
        this.columnNames = columnNames;
        this.absoluteParquetPath = info.getSource().getAbsolutePath().replace("\\", "/");
        this.blockCount = (int) Math.ceil((double) info.getRowCount() / BLOCK_SIZE);

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

    public static DataTable of(File file) throws IOException {
        if (!file.exists() || !file.isFile() || !file.canRead()) {
            throw new IOException("File not found or not readable: " + file.getAbsolutePath());
        }

        DataTableInfo tableInfo = DataTableInfo.of(file);
        if (tableInfo == null) {
            throw new IOException("Failed to read metadata from: " + file.getAbsolutePath());
        }

        String absolutePath = file.getAbsolutePath().replace("\\", "/");
        List<String> columnNames = new ArrayList<>();

        try (DuckDBConnection connection = (DuckDBConnection) DriverManager.getConnection("jdbc:duckdb:")) {
            try (var statement = connection.createStatement()) {
                ResultSet describeResult = statement.executeQuery(
                        String.format("DESCRIBE SELECT * FROM '%s'", absolutePath));
                while (describeResult.next()) {
                    columnNames.add(describeResult.getString("column_name"));
                }
            }
        } catch (Exception e) {
            throw new IOException("Failed to read column names from: " + file.getAbsolutePath(), e);
        }

        return new DataTable(tableInfo, columnNames);
    }

    public DataTableInfo getInfo() {
        return info;
    }

    public int getBlockCount() {
        return blockCount;
    }

    public long getBlockStartRow(int blockIndex) {
        return (long) blockIndex * BLOCK_SIZE;
    }

    public long getBlockRowCount(int blockIndex) {
        long start = getBlockStartRow(blockIndex);
        long remaining = info.getRowCount() - start;
        return Math.min(remaining, BLOCK_SIZE);
    }

    public String getColumnName(int columnIndex) {
        if (columnIndex < 0 || columnIndex >= columnNames.size()) {
            return "";
        }
        return columnNames.get(columnIndex);
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        if (rowIndex < 0 || rowIndex >= info.getRowCount()) {
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
        // int columnCount = columnNames.size();

        String query = String.format(
                "SELECT * FROM '%s' LIMIT %d OFFSET %d",
                absoluteParquetPath, limitCount, offsetRow);

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