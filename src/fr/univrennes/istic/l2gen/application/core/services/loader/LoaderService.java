package fr.univrennes.istic.l2gen.application.core.services.loader;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
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
import java.util.function.Function;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import fr.univrennes.istic.l2gen.application.core.services.IService;
import fr.univrennes.istic.l2gen.application.core.util.log.Log;
import fr.univrennes.istic.l2gen.io.csv.model.CSVTable;
import fr.univrennes.istic.l2gen.io.csv.parser.CSVParseException;
import fr.univrennes.istic.l2gen.io.csv.parser.CSVParser;

public final class LoaderService implements IService {

    private final Map<String, CSVTable> tables = new LinkedHashMap<>();

    public LoaderService() {
    }

    public List<String> getTablesName() {
        return new ArrayList<>(this.tables.keySet());
    }

    public CSVTable getTable(String key) {
        return this.tables.get(key);
    }

    public String getName(CSVTable table) {
        for (Map.Entry<String, CSVTable> entry : tables.entrySet()) {
            if (entry.getValue() == table) {
                return entry.getKey();
            }
        }
        return null;
    }

    public List<String> loadFolder(File folder, boolean hasHeader) {
        if (!folder.isDirectory()) {
            return new ArrayList<>();
        }

        File[] files = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".csv"));
        if (files == null) {
            return new ArrayList<>();
        }

        List<String> paths = new ArrayList<>();
        for (File file : files) {
            if (!parseFile(file, hasHeader)) {
                paths.add(file.getAbsolutePath());
            }
        }
        return paths;
    }

    public List<String> loadFile(File file, boolean hasHeader) {
        if (file.getName().toLowerCase().endsWith(".zip")) {
            return parseZip(file, hasHeader);
        }

        if (parseFile(file, hasHeader)) {
            return List.of(file.getAbsolutePath());
        }
        return new ArrayList<>();
    }

    public List<String> loadUrl(URI url, boolean hasHeader) {
        URLConnection connection;
        try {
            connection = url.toURL().openConnection();

            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);

            String contentType = connection.getContentType();
            if (contentType != null && contentType.equals("application/zip")) {
                return parseZip(connection, hasHeader);
            }

            try (Reader reader = new InputStreamReader(
                    new BufferedInputStream(connection.getInputStream()), StandardCharsets.UTF_8)) {
                CSVParser parser = new CSVParser('"', hasHeader, true);

                CSVTable table = parser.parse(reader);
                this.tables.put(url.toString(), table);
                return List.of(url.toString());
            }
        } catch (Exception e) {
            Log.error(e, "Failed to load CSV from URL: %s", url);
            return new ArrayList<>();

        }
    }

    private List<String> parseZip(InputStream inputStream, Function<String, String> keyMapper, boolean hasHeader) {
        try (ZipInputStream zis = new ZipInputStream(new BufferedInputStream(inputStream))) {
            CSVParser parser = new CSVParser('"', hasHeader, true);
            List<String> paths = new ArrayList<>();
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                if (!entry.isDirectory()) {
                    InputStream entryStream = new FilterInputStream(zis) {
                        @Override
                        public void close() {
                        }
                    };
                    try {
                        CSVTable table = parser.parse(new InputStreamReader(entryStream));
                        String key = keyMapper.apply(entry.getName());
                        this.tables.put(key, table);
                        paths.add(key);
                    } catch (CSVParseException e) {
                        Log.error(e, "Invalid CSV format in ZIP entry: %s", entry.getName());
                    }
                }
                zis.closeEntry();
            }
            return paths;
        } catch (IOException e) {
            Log.error(e, "Failed to parse ZIP archive");
            return new ArrayList<>();
        }
    }

    private List<String> parseZip(File base, boolean hasHeader) {
        try {
            return parseZip(new FileInputStream(base),
                    name -> base.getAbsolutePath() + "!" + name, hasHeader);
        } catch (IOException e) {
            Log.error(e, "Failed to parse ZIP archive: %s", base.getAbsolutePath());
            return new ArrayList<>();
        }
    }

    private List<String> parseZip(URLConnection connection, boolean hasHeader) {
        try {
            return parseZip(connection.getInputStream(),
                    name -> name, hasHeader);
        } catch (IOException e) {
            Log.error(e, "Failed to parse ZIP archive from URL: %s", connection.getURL());
            return new ArrayList<>();
        }
    }

    private boolean parseFile(File file, boolean hasHeader) {
        try {
            CSVTable table = new CSVParser('"', hasHeader, true).parse(file);
            this.tables.put(file.getAbsolutePath(), table);
            return true;
        } catch (CSVParseException e) {
            Log.error(e, "Invalid CSV format in file: %s", file.getAbsolutePath());
            return false;
        }
    }

}