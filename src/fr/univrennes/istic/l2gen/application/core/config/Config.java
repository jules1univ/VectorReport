package fr.univrennes.istic.l2gen.application.core.config;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import fr.univrennes.istic.l2gen.application.VectorReport;
import fr.univrennes.istic.l2gen.application.core.table.DataTable;

public final class Config {
    private static Config instance;
    private List<DataTable> recentTables = new ArrayList<>();

    public static Config getInstance() {
        if (instance == null) {
            instance = new Config();
        }
        return instance;
    }

    private Config() {
    }

    public File getAppDataDirectory() {
        String userHome = System.getProperty("user.home");
        File appDataDir = new File(userHome, ".VectorReport");
        if (!appDataDir.exists()) {
            appDataDir.mkdirs();
        }
        return appDataDir;
    }

    public File getAppDataFile(String fileName) {
        return new File(getAppDataDirectory(), fileName);
    }

    public List<DataTable> getRecentTables() {
        return recentTables;
    }

    public void addRecentFiles(List<DataTable> files) {
        this.recentTables.addAll(files);
    }

    public boolean init() {
        // TODO: load the config from json file if it exists, otherwise create a new one
        // with default values and save it
        // getAppDataFile("config.json");

        if (VectorReport.DEBUG_MODE) {
        }

        return true;
    }

    public void save() {
    }
}
