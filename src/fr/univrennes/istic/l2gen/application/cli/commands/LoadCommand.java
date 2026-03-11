package fr.univrennes.istic.l2gen.application.cli.commands;

import fr.univrennes.istic.l2gen.application.cli.util.log.Log;
import fr.univrennes.istic.l2gen.application.core.CoreController;
import java.io.File;
import java.net.URI;

public final class LoadCommand implements ICommand {

    @Override
    public boolean execute(CoreController controller, String[] args) {
        if (args.length < 1) {
            Log.error("Missing file or folder path");
            Log.message("Usage: %s", getUsage());
            return false;
        }

        String filePath = args[0];

        boolean hasHeaders = args.length > 1 && !args[1].equalsIgnoreCase("--no-headers");

        File file = new File(filePath);
        int totalImported = 0;

        long startTime = System.currentTimeMillis();
        if (file.isFile()) {
            totalImported += controller.getLoader().loadFile(file, hasHeaders).size();
        } else if (file.isDirectory()) {
            totalImported += controller.getLoader().loadFolder(file, hasHeaders).size();
        } else {
            try {
                totalImported += controller.getLoader().loadUrl(URI.create(filePath), hasHeaders).size();
            } catch (Exception e) {
                Log.error(e, "Invalid source: %s", filePath);
                return false;
            }
        }
        long endTime = System.currentTimeMillis();
        Log.message("Loaded %d tables in %.2f seconds", totalImported, (endTime - startTime) / 1000.0);
        return true;
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
        return "load <file|folder|url> [--no-headers]";
    }
}
