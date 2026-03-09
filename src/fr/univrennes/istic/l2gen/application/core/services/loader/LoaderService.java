package fr.univrennes.istic.l2gen.application.core.services.loader;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import fr.univrennes.istic.l2gen.application.core.services.IService;
import fr.univrennes.istic.l2gen.io.csv.model.CSVTable;
import fr.univrennes.istic.l2gen.io.csv.parser.CSVParseException;
import fr.univrennes.istic.l2gen.io.csv.parser.CSVParser;

public final class LoaderService implements IService {

    private final HashMap<File, CSVTable> loadedTables = new HashMap<>();
    private List<CSVTable> lastLoaded = new ArrayList<>();

    public LoaderService() {

    }

    public boolean loadFolder(File folder, char delimiter, boolean hasHeader) {
        if (!folder.isDirectory()) {
            return false;
        }

        File[] files = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".csv"));
        if (files == null) {
            return false;
        }

        boolean allSuccess = true;

        for (File file : files) {
            CSVParser parser = new CSVParser(delimiter, '"', hasHeader, true);
            try {
                CSVTable table = parser.parse(file);
                this.loadedTables.put(file, table);
                this.lastLoaded.add(table);
            } catch (CSVParseException e) {
                allSuccess = false;
            }
        }
        return allSuccess;
    }

    public boolean loadFile(File file, char delimiter, boolean hasHeader) {
        CSVParser parser = new CSVParser(delimiter, '"', hasHeader, true);
        try {
            CSVTable table = parser.parse(file);
            this.loadedTables.put(file, table);
            this.lastLoaded.clear();
            this.lastLoaded.add(table);

            return true;
        } catch (CSVParseException e) {
            return false;
        }
    }

    public List<CSVTable> getLastLoaded() {
        return this.lastLoaded;
    }

    public CSVTable getTable(File file) {
        return this.loadedTables.get(file);
    }

    public CSVTable getTableByName(String name) {
        return this.loadedTables.entrySet().stream()
                .filter(entry -> entry.getKey().getName().equals(name))
                .map(entry -> entry.getValue())
                .findFirst()
                .orElse(null);
    }

    public List<File> getLoadedFiles() {
        return this.loadedTables.keySet().stream().toList();
    }

    public List<String> getLoadedFileNames() {
        return this.loadedTables.keySet().stream()
                .map(File::getName)
                .toList();
    }
}