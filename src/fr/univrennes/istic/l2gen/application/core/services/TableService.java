package fr.univrennes.istic.l2gen.application.core.services;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.file.Path;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.duckdb.DuckDBConnection;

import fr.univrennes.istic.l2gen.application.VectorReport;
import fr.univrennes.istic.l2gen.application.core.table.DataTable;
import fr.univrennes.istic.l2gen.application.core.table.DataType;

public final class TableService {
    private static final HashMap<File, DataTable> loaded = new HashMap<>();
    private static File[] recents = null;

    public static DataTable get(File file) {
        if (loaded.containsKey(file)) {
            return loaded.get(file);
        }

        List<DataTable> tables = load(file);
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

    public static List<DataTable> load(File file) {
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
                        return processZip(file);
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

    public static List<DataTable> load(URI uri) {
        try {
            URL url = uri.toURL();
            return processURL(url);
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
            result.addAll(load(f));
        }
        return result;
    }

    private static List<DataTable> processURL(URL url) {
        try {
            File downloadDir = FileService.getDownloadDirectory();

            HttpURLConnection conn;
            conn = (HttpURLConnection) url.openConnection();
            conn.setInstanceFollowRedirects(true);

            File file = new File(downloadDir, FileService.getRemoteFileName(conn, url));
            try (InputStream in = conn.getInputStream();
                    FileOutputStream fos = new FileOutputStream(file)) {
                in.transferTo(fos);
            }
            return load(file);
        } catch (Exception e) {
            if (VectorReport.DEBUG_MODE) {
                e.printStackTrace();
            }
        }

        return List.of();
    }

    private static List<DataTable> processZip(File zipFile) throws IOException {

        Path dir = FileService.getDownloadDirectory().toPath();
        List<DataTable> result = new ArrayList<>();
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {

                File file = new File(dir.toFile(), entry.getName());
                if (entry.isDirectory()) {
                    file.mkdirs();
                    continue;
                }

                new File(file.getParent()).mkdirs();

                try (FileOutputStream fos = new FileOutputStream(file)) {
                    zis.transferTo(fos);
                }

                result.addAll(load(file));
            }
        }

        return result;
    }

    public static void addRecent(File file) {
        if (recents == null) {
            loadRecents();
        }
        for (int i = recents.length - 1; i > 0; i--) {
            recents[i] = recents[i - 1];
        }
        recents[0] = file;
    }

    public static List<File> getRecentTables() {
        if (recents == null) {
            loadRecents();
        }
        return List.of(recents);
    }

    public static void loadRecents() {
        recents = new File[10];

        File configFile = FileService.getAppDataFile("tables.cfg");
        if (!configFile.exists()) {
            return;
        }

        try (Scanner scanner = new Scanner(configFile)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                File file = new File(line);
                if (file.exists() && file.canRead()) {
                    addRecent(file);
                }
            }
        } catch (Exception e) {
            if (VectorReport.DEBUG_MODE) {
                e.printStackTrace();
            }
        }
    }

    public static void saveRecents() {
        try (FileWriter writer = new FileWriter(FileService.getAppDataFile("tables.cfg"))) {
            for (File file : recents) {
                if (file != null) {
                    writer.write(file.getAbsolutePath() + System.lineSeparator());
                }
            }
        } catch (Exception e) {
            if (VectorReport.DEBUG_MODE) {
                e.printStackTrace();
            }
        }
    }

}
