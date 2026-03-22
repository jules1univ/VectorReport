package fr.univrennes.istic.l2gen.application.core.services;

import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.*;

import fr.univrennes.istic.l2gen.application.core.config.Config;
import fr.univrennes.istic.l2gen.application.core.table.DataTableInfo;

public final class FileService {

    public static boolean isParquetFile(File file) {
        return file.isFile() && file.canRead() && getExtension(file).equals("parquet");
    }

    public static boolean isAlreadyProcessed(File file) {
        return Config.getInstance().getAppDataFile(file.getName() + ".parquet").exists();
    }

    public static File getProcessedFile(File file) {
        return Config.getInstance().getAppDataFile(file.getName() + ".parquet");
    }

    public static List<DataTableInfo> process(File file) {
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
                        DataTableInfo info = convertToParquet(file);
                        if (info != null) {
                            return List.of(info);
                        } else {
                            return List.of();
                        }
                    }
                    case "parquet": {
                        DataTableInfo info = DataTableInfo.of(file);
                        if (info != null) {
                            return List.of(info);
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

    private static List<DataTableInfo> processDirectory(File dir) {
        File[] files = dir.listFiles();
        if (files == null) {
            return List.of();
        }

        List<DataTableInfo> result = new ArrayList<>();
        for (File f : files) {
            result.addAll(process(f));
        }
        return result;
    }

    private static List<DataTableInfo> processZip(File zipFile) throws IOException {

        Path dir = Config.getInstance().getAppDataDirectory().toPath().resolve(zipFile.getName());
        List<DataTableInfo> result = new ArrayList<>();
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

    private static DataTableInfo convertToParquet(File file) {
        try {
            File output = Config.getInstance().getAppDataFile(file.getName() + ".parquet");
            return ConverterService.convert(file, output);
        } catch (Exception e) {
            return null;
        }
    }

    private static String getExtension(File file) {
        String name = file.getName().toLowerCase();
        int i = name.lastIndexOf('.');
        return (i > 0) ? name.substring(i + 1) : "";
    }
}