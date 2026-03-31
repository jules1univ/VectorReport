package fr.univrennes.istic.l2gen.application.core.services;

import java.io.File;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import fr.univrennes.istic.l2gen.application.VectorReport;
import fr.univrennes.istic.l2gen.application.core.table.DataTable;

public final class TableService {
    private static TableService instance = new TableService();
    private HashMap<File, DataTable> tables = new HashMap<>();

    private TableService() {
    }

    public static void addTable(DataTable table) {
        instance.tables.put(table.getPath(), table);
    }

    public static void addTables(List<DataTable> tables) {
        for (DataTable table : tables) {
            addTable(table);
        }
    }

    public static DataTable getTable(File path) {
        return instance.tables.get(path);
    }

    public static List<DataTable> getTables() {
        return instance.tables.values().stream().toList();
    }

    public static void refresh() {
        instance.tables.clear();
        File folder = FileService.getAppDataDirectory();
        for (File file : folder.listFiles()) {
            if (FileService.isParquetFile(file)) {
                try {
                    DataTable table = DataTable.of(file);
                    if (table != null) {
                        instance.tables.put(file, table);
                    }
                } catch (Exception e) {
                    if (VectorReport.DEBUG_MODE) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public static void load() {
        File configFile = FileService.getAppDataFile("tables.cfg");
        if (!configFile.exists())
            return;

        try (Scanner scanner = new Scanner(configFile)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                File file = new File(line);
                if (file.exists() && FileService.isParquetFile(file)) {
                    try {
                        DataTable table = DataTable.of(file);
                        if (table != null) {
                            instance.tables.put(file, table);
                        }
                    } catch (Exception e) {
                        if (VectorReport.DEBUG_MODE) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        } catch (Exception e) {
            if (VectorReport.DEBUG_MODE) {
                e.printStackTrace();
            }
        }
    }

    public static void save() {
        try (FileWriter writer = new FileWriter(FileService.getAppDataFile("tables.cfg"))) {
            for (DataTable table : instance.tables.values()) {
                if (table.getPath().getParentFile().equals(FileService.getAppDataDirectory())) {
                    continue;
                }
                writer.write(table.getPath().getAbsolutePath() + "\n");
            }
        } catch (Exception e) {
            if (VectorReport.DEBUG_MODE) {
                e.printStackTrace();
            }
        }
    }

}
