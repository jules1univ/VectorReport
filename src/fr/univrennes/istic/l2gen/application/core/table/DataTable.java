package fr.univrennes.istic.l2gen.application.core.table;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
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

import fr.univrennes.istic.l2gen.application.VectorReport;
import fr.univrennes.istic.l2gen.application.core.filter.Filter;
import fr.univrennes.istic.l2gen.application.core.filter.FilterType;

public final class DataTable {
    private static final int BLOCK_SIZE = 10_000;
    private static final int MAX_CACHED_BLOCKS = 8;

    private final LinkedHashMap<Integer, Object[][]> blockCache;
    private final ExecutorService prefetchExecutor;

    private DuckDBConnection sharedConnection;

    private final String tableName;
    private final File tablePath;
    private String alias;

    private final List<String> columnNames;
    private final List<DataType> columnTypes;

    private final long rowCount;
    private final long columnCount;
    private final int blockCount;

    private final List<Filter> filters = new ArrayList<>();

    public static DataTable of(File file) throws IOException {
        if (!file.exists() || !file.isFile() || !file.canRead()) {
            throw new IOException("File not found or not readable: " + file.getAbsolutePath());
        }

        String absolutePath = file.getAbsolutePath().replace("\\", "/");

        try (DuckDBConnection connection = (DuckDBConnection) DriverManager.getConnection("jdbc:duckdb:")) {
            try (Statement statement = connection.createStatement()) {

                ResultSet rowCountResult = statement.executeQuery(
                        String.format("SELECT COUNT(*) FROM '%s'", absolutePath));
                rowCountResult.next();
                long rowCount = rowCountResult.getLong(1);

                ResultSet columnResult = statement.executeQuery(
                        String.format("DESCRIBE SELECT * FROM '%s'", absolutePath));

                List<String> columnNames = new ArrayList<>();
                List<DataType> columnTypes = new ArrayList<>();

                long columnCount = 0;
                while (columnResult.next()) {
                    columnCount++;
                    columnNames.add(columnResult.getString("column_name"));
                    columnTypes.add(DataType.fromSQL(columnResult.getString("column_type")));
                }

                String alias = file.getName().replaceFirst("[.][^.]+$", "");
                return new DataTable(file, alias, columnNames, columnTypes, rowCount, columnCount);
            }
        } catch (Exception e) {
            throw new IOException("Failed to read metadata from: " + file.getAbsolutePath(), e);
        }
    }

    public DataTable(File file, String alias, List<String> columnNames, List<DataType> columnTypes, long rowCount,
            long columnCount)
            throws IOException {
        this.tablePath = file;
        this.tableName = file.getAbsolutePath().replace("\\", "/");
        this.alias = alias;

        this.columnNames = columnNames;
        this.columnTypes = columnTypes;
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

    public File getPath() {
        return tablePath;
    }

    public String getSQLName() {
        return tableName;
    }

    public String getSQLColumnName(int columnIndex) {
        if (columnIndex < 0 || columnIndex >= columnNames.size()) {
            return "";
        }
        return "\"" + getColumnName(columnIndex).replace("\"", "\"\"") + "\"";
    }

    public String getColumnName(int columnIndex) {
        if (columnIndex < 0 || columnIndex >= columnNames.size()) {
            return "";
        }
        return columnNames.get(columnIndex);
    }

    public DataType getColumnType(int columnIndex) {
        if (columnIndex < 0 || columnIndex >= columnTypes.size()) {
            return DataType.STRING;
        }
        return columnTypes.get(columnIndex);
    }

    public void setColumnType(int columnIndex, DataType newType) {
        if (columnIndex < 0 || columnIndex >= columnTypes.size())
            return;

        String colName = getSQLColumnName(columnIndex);
        String castType = newType.toSQL();

        File tempFile = new File(tablePath.getAbsolutePath() + ".tmp.parquet");
        String tempName = tempFile.getAbsolutePath().replace("\\", "/");

        StringBuilder select = new StringBuilder("SELECT ");
        for (int i = 0; i < columnNames.size(); i++) {
            String name = getSQLColumnName(i);

            if (i == columnIndex) {
                select.append(String.format("TRY_CAST(%s AS %s) AS %s", name, castType, name));
            } else {
                select.append(name);
            }

            if (i < columnNames.size() - 1)
                select.append(", ");
        }

        String query = String.format(
                "COPY (%s FROM '%s') TO '%s' (FORMAT PARQUET, CODEC 'SNAPPY')",
                select.toString(),
                tableName,
                tempName);

        try (Connection conn = DriverManager.getConnection("jdbc:duckdb:");
                Statement stmt = conn.createStatement()) {

            stmt.execute(query);

            tablePath.delete();
            tempFile.renameTo(tablePath);

            columnTypes.set(columnIndex, newType);

            synchronized (blockCache) {
                blockCache.clear();
            }

        } catch (Exception e) {
            if (VectorReport.DEBUG_MODE) {
                e.printStackTrace();
            }
        }
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
        this.invalidateCache();
        try {
            if (sharedConnection != null && !sharedConnection.isClosed()) {
                sharedConnection.close();
            }
        } catch (Exception ignored) {
        }
    }

    public void addFilter(Filter filter) {
        clearFilters(filter.getColumnIndex());
        filters.add(filter);
        invalidateCache();
    }

    public List<Filter> getFilter(int columnIndex) {
        return filters.stream()
                .filter(f -> f.getColumnIndex() == columnIndex)
                .toList();
    }

    public List<Filter> getAllFilters() {
        return filters;
    }

    public void clearFilters(int columnIndex) {
        filters.removeIf(f -> f.getColumnIndex() == columnIndex);
        invalidateCache();
    }

    public void clearAllFilters() {
        filters.clear();
        invalidateCache();
    }

    private void invalidateCache() {
        synchronized (blockCache) {
            blockCache.clear();
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

        String query = buildQuery(limitCount, offsetRow);
        try {
            Object[][] blockData;
            synchronized (sharedConnection) {
                try (Statement statement = sharedConnection.createStatement()) {
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

    private String buildQuery(long limit, long offset) {
        StringBuilder sql = new StringBuilder("SELECT * FROM '")
                .append(tableName)
                .append("'");

        boolean hasWhere = false;

        for (Filter f : filters) {
            String col = getSQLColumnName(f.getColumnIndex());

            switch (f.getType()) {
                case RANGE -> {
                    sql.append(hasWhere ? " AND " : " WHERE ");
                    sql.append(String.format(
                            "TRY_CAST(%s AS DOUBLE) BETWEEN %f AND %f",
                            col, f.getMin(), f.getMax()));
                    hasWhere = true;
                }
                case EMPTY -> {
                    sql.append(hasWhere ? " AND " : " WHERE ");
                    sql.append(col).append(" IS NULL");
                    hasWhere = true;
                }
                case NOT_EMPTY -> {
                    sql.append(hasWhere ? " AND " : " WHERE ");
                    sql.append(col).append(" IS NOT NULL");
                    hasWhere = true;
                }
                case SEARCH -> {
                    sql.append(hasWhere ? " AND " : " WHERE ");
                    sql.append(col).append("::TEXT ILIKE '%").append(f.getSearchTerm().replace("'", "''")).append("%'");
                    hasWhere = true;
                }
                default -> {
                }
            }
        }

        for (Filter f : filters) {
            String col = getSQLColumnName(f.getColumnIndex());

            switch (f.getType()) {
                case SORT -> {
                    sql.append(" ORDER BY ")
                            .append(col)
                            .append(f.isAscending() ? " ASC" : " DESC");
                }
                case TOP_N -> {
                    sql.append(" ORDER BY ")
                            .append(col)
                            .append(" DESC LIMIT ")
                            .append(f.getN());
                }
                case BOTTOM_N -> {
                    sql.append(" ORDER BY ")
                            .append(col)
                            .append(" ASC LIMIT ")
                            .append(f.getN());
                }
                default -> {
                }
            }
        }

        if (filters.stream()
                .noneMatch(f -> f.getType() == FilterType.TOP_N || f.getType() == FilterType.BOTTOM_N)) {
            sql.append(" LIMIT ").append(limit)
                    .append(" OFFSET ").append(offset);
        }

        return sql.toString();
    }
}