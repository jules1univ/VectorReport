package fr.univrennes.istic.l2gen.application.core.services.loader;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import fr.univrennes.istic.l2gen.application.cli.util.log.Log;
import fr.univrennes.istic.l2gen.io.csv.model.CSVTable;
import fr.univrennes.istic.l2gen.io.csv.parser.CSVParseException;
import fr.univrennes.istic.l2gen.io.csv.parser.CSVParser;

public final class LoaderService implements ILoaderService {

    private final Map<File, CSVTable> loadedTables = new LinkedHashMap<>();
    private List<CSVTable> lastLoaded = new ArrayList<>();

    public LoaderService() {

    }

    @Override
    public boolean loadFolder(File folder, Character delimiter, boolean hasHeader) {
        if (!folder.isDirectory()) {
            return false;
        }

        File[] files = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".csv"));
        if (files == null) {
            return false;
        }

        this.lastLoaded = new ArrayList<>();
        boolean allSuccess = true;
        for (File file : files) {
            CSVParser parser = new CSVParser(delimiter, '"', hasHeader, true);
            try {
                CSVTable table = parser.parse(file);
                this.loadedTables.put(file, table);
                this.lastLoaded.add(table);
            } catch (CSVParseException e) {
                Log.error(e, "Invalid CSV format in file: %s", file.getAbsolutePath());
                allSuccess = false;
            }
        }
        return allSuccess;
    }

    @Override
    public boolean loadFile(File file, Character delimiter, boolean hasHeader) {
        CSVParser parser = new CSVParser(delimiter, '"', hasHeader, true);
        try {
            CSVTable table = parser.parse(file);
            this.loadedTables.put(file, table);
            this.lastLoaded.clear();
            this.lastLoaded.add(table);

            return true;
        } catch (CSVParseException e) {
            Log.error(e, "Invalid CSV format in file: %s", file.getAbsolutePath());
            return false;
        }
    }

    @Override
    public boolean loadUrl(URI url, Character delimiter, boolean hasHeader) {
        CSVParser parser = new CSVParser(delimiter, '"', hasHeader, true);
        try {
            URLConnection connection = url.toURL().openConnection();
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);

            String contentType = connection.getContentType();
            if (contentType != null && contentType.equals("application/zip")) {
                try (BufferedInputStream bis = new BufferedInputStream(connection.getInputStream());
                        ZipInputStream zis = new ZipInputStream(bis)) {

                    ZipEntry entry;
                    while ((entry = zis.getNextEntry()) != null) {
                        if (!entry.isDirectory()) {
                            InputStream entryStream = new FilterInputStream(zis) {
                                @Override
                                public void close() {
                                }
                            };

                            CSVTable table = parser.parse(new InputStreamReader(entryStream));

                            File pseudoFile = new File(entry.getName());
                            this.loadedTables.put(pseudoFile, table);
                            this.lastLoaded.add(table);
                        }
                        zis.closeEntry();
                    }
                } catch (IOException e) {
                    Log.error(e, "Error reading ZIP from URL: %s", url.toString());
                    return false;
                }

            } else {
                try (Reader reader = new InputStreamReader(new BufferedInputStream(connection.getInputStream()),
                        StandardCharsets.UTF_8)) {

                    CSVTable table = parser.parse(reader);
                    File pseudoFile = new File(url.getPath());
                    this.loadedTables.put(pseudoFile, table);
                    this.lastLoaded.clear();
                    this.lastLoaded.add(table);
                } catch (IOException e) {
                    Log.error(e, "Error reading from URL: %s", url.toString());
                    return false;
                }
            }

            return true;
        } catch (Exception e) {
            Log.error(e, "Invalid CSV format in URL: %s", url.toString());
            return false;
        }
    }

    @Override
    public List<CSVTable> getLatestLoadedTables() {
        return List.copyOf(this.lastLoaded);
    }

    @Override
    public CSVTable getTable(File file) {
        return this.loadedTables.get(file);
    }

    @Override
    public CSVTable getTableByName(String name) {
        return this.loadedTables.entrySet().stream()
                .filter(entry -> entry.getKey().getName().equals(name))
                .map(entry -> entry.getValue())
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<File> getLoadedFiles() {
        return this.loadedTables.keySet().stream().toList();
    }

    @Override
    public List<String> getLoadedFileNames() {
        return this.loadedTables.keySet().stream()
                .map(File::getName)
                .toList();
    }

}