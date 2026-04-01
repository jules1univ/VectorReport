package fr.univrennes.istic.l2gen.application.core.services;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public final class FileService {

    public static String getExtension(File file) {
        String name = file.getName().toLowerCase();
        int i = name.lastIndexOf('.');
        return (i > 0) ? name.substring(i + 1) : "";
    }

    public static String getRemoteFileName(HttpURLConnection connection, URL url) {
        String disposition = connection.getHeaderField("Content-Disposition");

        if (disposition != null && disposition.contains("filename=")) {
            return disposition
                    .split("filename=")[1]
                    .replace("\"", "")
                    .trim();
        }

        String path = url.getPath();
        String name = path.substring(path.lastIndexOf('/') + 1);

        if (!name.contains(".")) {
            String contentType = connection.getContentType();
            if (contentType != null) {
                if (contentType.contains("csv")) {
                    return name + ".csv";
                }
                if (contentType.contains("txt")) {
                    return name + ".txt";
                }
                if (contentType.contains("parquet")) {
                    return name + ".parquet";
                }
                if (contentType.contains("zip")) {
                    return name + ".zip";
                }
            }
        }

        return name.isEmpty() ? "downloaded_file" : name;
    }

}