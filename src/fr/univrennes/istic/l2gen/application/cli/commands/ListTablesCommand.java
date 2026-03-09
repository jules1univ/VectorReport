package fr.univrennes.istic.l2gen.application.cli.commands;

import java.io.File;
import java.util.List;

import fr.univrennes.istic.l2gen.application.cli.util.log.Log;
import fr.univrennes.istic.l2gen.application.core.CoreController;

public final class ListTablesCommand implements ICommand {

    @Override
    public boolean execute(CoreController controller, String[] args) {
        List<File> files = controller.getLoader().getLoadedFiles();
        if (files.isEmpty()) {
            Log.message("No tables loaded.");
            return true;
        }

        Log.message("Loaded tables:");
        for (File file : files) {
            int rows = controller.getLoader().getTable(file).rows().size();
            Log.message("  - %s (%d rows)", file.getAbsolutePath(), rows);
        }
        return true;
    }

    @Override
    public String getName() {
        return "list";
    }

    @Override
    public String getDescription() {
        return "Display a list of loaded tables";
    }

    @Override
    public String getUsage() {
        return "list";
    }

}
