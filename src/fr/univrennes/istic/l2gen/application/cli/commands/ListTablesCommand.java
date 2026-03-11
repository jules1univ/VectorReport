package fr.univrennes.istic.l2gen.application.cli.commands;

import java.util.List;

import fr.univrennes.istic.l2gen.application.cli.util.log.Log;
import fr.univrennes.istic.l2gen.application.core.CoreController;

public final class ListTablesCommand implements ICommand {

    @Override
    public boolean execute(CoreController controller, String[] args) {
        List<String> names = controller.getLoader().getTablesName();
        if (names.isEmpty()) {
            Log.message("No tables loaded.");
            return true;
        }

        Log.message("Loaded tables:");
        for (String name : names) {
            int rows = controller.getLoader().getTable(name).getRows().size();
            Log.message("  - %s (%d rows)", name, rows);
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
