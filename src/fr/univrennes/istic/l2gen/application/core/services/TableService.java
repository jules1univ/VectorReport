package fr.univrennes.istic.l2gen.application.core.services;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.duckdb.DuckDBConnection;

import fr.univrennes.istic.l2gen.application.VectorReport;
import fr.univrennes.istic.l2gen.application.core.config.Config;
import fr.univrennes.istic.l2gen.application.core.table.DataTable;
import fr.univrennes.istic.l2gen.application.core.table.DataType;

public final class TableService {
    private static final Map<File, DataTable> loaded = Collections.synchronizedMap(new HashMap<>());
    private static File[] recents = null;

    public static DataTable get(File file) {
        if (loaded.containsKey(file)) {
            return loaded.get(file);
        }

        List<DataTable> tables = load(file, file.getParentFile());
        if (!tables.isEmpty()) {
            DataTable table = tables.get(0);
            loaded.put(file, table);
            return table;
        }

        return null;
    }

    public static List<DataTable> get() {
        return loaded.values().stream().toList();
    }

    public static List<DataTable> load(File file, File targetDir) {
        if (!file.exists()) {
            return List.of();
        }

        try {
            if (file.isDirectory()) {
                return processDirectory(file);
            }

            if (file.isFile() && file.canRead()) {
                String ext = FileService.getExtension(file);

                switch (ext) {
                    case "zip":
                        return processZip(file, targetDir);
                    case "csv":
                    case "tsv":
                    case "txt": {
                        File outputFile = new File(file.getAbsolutePath() + ".parquet");
                        DataTable table = convert(file, outputFile);
                        if (table != null) {
                            loaded.put(file, table);
                            return List.of(table);
                        }
                    }
                        break;
                    case "parquet": {
                        DataTable table = DataTable.of(file);
                        if (table != null) {
                            loaded.put(file, table);
                            return List.of(table);
                        }
                    }
                        break;
                }
            }

        } catch (Exception e) {
        }

        return List.of();
    }

    public static List<DataTable> load(URI uri, File targetDir) {
        try {
            URL url = uri.toURL();
            return processURL(targetDir, url);
        } catch (Exception e) {
            if (VectorReport.DEBUG_MODE) {
                e.printStackTrace();
            }
            return List.of();
        }
    }

    private static DataTable convert(File inputPath, File outputPath) {
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

    private static List<DataTable> processDirectory(File dir) {
        File[] files = dir.listFiles();
        if (files == null) {
            return List.of();
        }

        List<DataTable> result = new ArrayList<>();
        for (File f : files) {
            result.addAll(load(f, dir));
        }
        return result;
    }

    private static List<DataTable> processURL(File targetDir, URL url) {
        try {
            HttpURLConnection conn;
            conn = (HttpURLConnection) url.openConnection();
            conn.setInstanceFollowRedirects(true);

            File file = new File(targetDir, FileService.getRemoteFileName(conn, url));
            try (InputStream in = conn.getInputStream();
                    FileOutputStream fos = new FileOutputStream(file)) {
                in.transferTo(fos);
            }
            return load(file, targetDir);
        } catch (Exception e) {
            if (VectorReport.DEBUG_MODE) {
                e.printStackTrace();
            }
        }

        return List.of();
    }

    private static List<DataTable> processZip(File zipFile, File targetDir) throws IOException {

        List<DataTable> result = new ArrayList<>();
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {

                File resolvedFile = new File(targetDir, entry.getName()).getCanonicalFile();

                if (!resolvedFile.toPath().startsWith(targetDir.getCanonicalFile().toPath())) {
                    throw new IOException("Zip Slip detected: " + entry.getName());
                }

                if (entry.isDirectory()) {
                    resolvedFile.mkdirs();
                    continue;
                }

                resolvedFile.getParentFile().mkdirs();
                try (FileOutputStream fos = new FileOutputStream(resolvedFile)) {
                    zis.transferTo(fos);
                }

                result.addAll(load(resolvedFile, targetDir));
            }
        }

        return result;
    }

    public static void addRecent(File file) {
        if (recents == null) {
            loadRecents();
        }
        for (int index = recents.length - 1; index > 0; index--) {
            recents[index] = recents[index - 1];
        }
        recents[0] = file;
        saveRecents();
    }

    public static List<File> getRecentTables() {
        if (recents == null) {
            loadRecents();
        }
        return List.of(recents);
    }

    public static void loadRecents() {
        recents = new File[10];
        Config.get().getByteArray("recents", new byte[0]);
        try (Scanner scanner = new Scanner(
                new ByteArrayInputStream(Config.get().getByteArray("recents", new byte[0])))) {
            int i = 0;
            while (scanner.hasNextLine() && i < recents.length) {
                recents[i] = new File(scanner.nextLine());
                i++;
            }
        }
    }

    public static void saveRecents() {
        StringBuilder sb = new StringBuilder();
        for (File f : recents) {
            if (f != null) {
                sb.append(f.getAbsolutePath()).append("\n");
            }
        }
        Config.get().putByteArray("recents", sb.toString().getBytes());
    }

}
