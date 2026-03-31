package fr.univrennes.istic.l2gen.application.core.services;

import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.*;

import fr.univrennes.istic.l2gen.application.VectorReport;
import fr.univrennes.istic.l2gen.application.core.table.DataTable;

public final class FileService {

    public static File getAppDataDirectory() {
        String userHome = System.getProperty("user.home");
        File appDataDir = new File(userHome, ".VectorReport");
        if (!appDataDir.exists()) {
            try {
                appDataDir.mkdirs();
            } catch (Exception e) {
                if (VectorReport.DEBUG_MODE) {
                    e.printStackTrace();
                }
            }
        }
        return appDataDir;
    }

    public static File getAppDataFile(String fileName) {
        return new File(getAppDataDirectory(), fileName);
    }

    public static String getExtension(File file) {
        String name = file.getName().toLowerCase();
        int i = name.lastIndexOf('.');
        return (i > 0) ? name.substring(i + 1) : "";
    }

    public static boolean isParquetFile(File file) {
        return file.isFile() && file.canRead() && getExtension(file).equals("parquet");
    }

    public static boolean isAlreadyProcessed(File file) {
        return getAppDataFile(file.getName() + ".parquet").exists();
    }

    public static File getProcessedFile(File file) {
        return getAppDataFile(file.getName() + ".parquet");
    }

    public static List<DataTable> process(File file) {
        if (!file.exists())
            return List.of();

        try {
            if (file.isDirectory()) {
                return processDirectory(file);
            }

            if (isAlreadyProcessed(file)) {
                file = getProcessedFile(file);
            }

            if (file.isFile() && file.canRead()) {
                String ext = getExtension(file);

                switch (ext) {
                    case "zip":
                        return processZip(file);
                    case "csv":
                    case "tsv":
                    case "txt": {
                        DataTable table = DataTable.of(file);
                        if (table != null) {
                            return List.of(table);
                        } else {
                            return List.of();
                        }
                    }
                    case "parquet": {
                        DataTable table = DataTable.of(file);
                        if (table != null) {
                            return List.of(table);
                        } else {
                            return List.of();
                        }
                    }
                    default:
                        return List.of();
                }
            }

        } catch (Exception e) {
        }

        return List.of();
    }

    private static List<DataTable> processDirectory(File dir) {
        File[] files = dir.listFiles();
        if (files == null) {
            return List.of();
        }

        List<DataTable> result = new ArrayList<>();
        for (File f : files) {
            result.addAll(process(f));
        }
        return result;
    }

    private static List<DataTable> processURL(String url) {
        return List.of();
    }

    private static List<DataTable> processZip(File zipFile) throws IOException {

        Path dir = getAppDataDirectory().toPath().resolve(zipFile.getName());
        List<DataTable> result = new ArrayList<>();
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {

                File newFile = new File(dir.toFile(), entry.getName());
                if (entry.isDirectory()) {
                    newFile.mkdirs();
                    continue;
                }

                new File(newFile.getParent()).mkdirs();

                try (FileOutputStream fos = new FileOutputStream(newFile)) {
                    zis.transferTo(fos);
                }

                result.addAll(process(newFile));
            }
        }

        return result;
    }

}