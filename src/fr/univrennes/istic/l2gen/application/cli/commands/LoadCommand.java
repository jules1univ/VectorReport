package fr.univrennes.istic.l2gen.application.cli.commands;

import fr.univrennes.istic.l2gen.application.cli.util.log.Log;
import fr.univrennes.istic.l2gen.application.core.CoreController;
import fr.univrennes.istic.l2gen.io.csv.model.CSVTable;

import java.io.File;
import java.util.List;

public final class LoadCommand implements ICommand {

    @Override
    public boolean execute(CoreController controller, String[] args) {
        if (args.length < 1) {
            Log.error("Missing file or folder path");
            Log.message("Usage: %s", getUsage());
            return false;
        }

        String filePath = args[0];
        File file = new File(filePath);

        if (!file.exists()) {
            Log.error("File or folder does not exist: %s", filePath);
            return false;
        }

        Character delimiter = null;
        boolean hasHeaders = true;

        for (int i = 1; i < args.length; i++) {
            if (args[i].equals("-d") && i + 1 < args.length) {
                delimiter = args[i + 1].charAt(0);
                i++;
            } else if (args[i].equals("--no-headers")) {
                hasHeaders = false;
            }
        }

        boolean success;
        long startTime = System.currentTimeMillis();
        if (file.isFile()) {
            success = controller.getLoader().loadFile(file, delimiter, hasHeaders);
        } else if (file.isDirectory()) {
            success = controller.getLoader().loadFolder(file, delimiter, hasHeaders);
        } else {
            Log.error("Invalid file or directory: %s", filePath);
            return false;
        }
        long endTime = System.currentTimeMillis();
        Log.message("Loading completed in %d ms", (endTime - startTime));

        List<CSVTable> tables = controller.getLoader().getLastLoaded();
        if (tables.isEmpty()) {
            Log.message("No tables loaded from: %s", filePath);
            return false;
        }

        if (tables.size() == 1) {
            CSVTable table = tables.get(0);
            controller.setTable(table);

            Log.message("Successfully loaded: %s", filePath);
            Log.message("Rows: %d", table.rows().size());

            if (table.header().isPresent()) {
                Log.message("Columns: %d", table.header().get().cells().size());
            } else if (!table.rows().isEmpty()) {
                Log.message("Columns: %d", table.rows().get(0).cells().size());
            }

        } else {
            controller.setTable(tables.get(0));
            Log.message("Successfully loaded %d tables from folder: %s", tables.size(), filePath);
        }

        return success;
    }

    @Override
    public String getName() {
        return "load";
    }

    @Override
    public String getDescription() {
        return "Load a CSV file";
    }

    @Override
    public String getUsage() {
        return "load <file|folder> [-d <delimiter>] [--no-headers]";
    }
}
