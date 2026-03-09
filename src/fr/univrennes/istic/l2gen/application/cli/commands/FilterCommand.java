package fr.univrennes.istic.l2gen.application.cli.commands;

import java.util.List;

import fr.univrennes.istic.l2gen.application.cli.util.log.Log;
import fr.univrennes.istic.l2gen.application.core.CoreController;
import fr.univrennes.istic.l2gen.application.core.filter.ColumnFilter;
import fr.univrennes.istic.l2gen.application.core.filter.IFilter;
import fr.univrennes.istic.l2gen.application.core.filter.RangeFilter;
import fr.univrennes.istic.l2gen.io.csv.model.CSVTable;

public final class FilterCommand implements ICommand {

    @Override
    public boolean execute(CoreController controller, String[] args) {
        if (args.length < 1) {
            Log.error("Missing filter action");
            Log.message("Usage: %s", getUsage());
            return false;
        }

        String action = args[0].toLowerCase();
        switch (action) {
            case "add" -> {
                return addFilter(controller, args);
            }
            case "list" -> {
                return listFilters(controller);
            }
            case "clear" -> {
                return clearFilters(controller);
            }
            case "apply" -> {
                return applyFilters(controller);
            }
            default -> {
                Log.error("Unknown filter action: %s", action);
                Log.message("Usage: %s", getUsage());
                return false;
            }
        }
    }

    private boolean addFilter(CoreController controller, String[] args) {
        if (args.length < 2) {
            Log.error("Missing filter type");
            Log.message("Available filter types: column, range");
            return false;
        }

        String filterType = args[1].toLowerCase();
        switch (filterType) {
            case "column" -> {
                return addColumnFilter(controller, args);
            }
            case "range" -> {
                return addRangeFilter(controller, args);
            }
            default -> {
                Log.error("Unknown filter type: %s", filterType);
                Log.message("Available filter types: column, range");
                return false;
            }
        }
    }

    private boolean addColumnFilter(CoreController controller, String[] args) {
        if (args.length < 4) {
            Log.error("Missing arguments for column filter");
            Log.message("Usage: filter add column <col_index> <value> [exact]");
            return false;
        }

        try {
            int colIndex = Integer.parseInt(args[2]);
            String value = args[3];
            boolean exact = args.length > 4 && args[4].equalsIgnoreCase("exact");

            IFilter filter = new ColumnFilter(colIndex, value, exact);
            controller.getFilter().add(filter);

            Log.message("Added column filter: column[%d] %s '%s'", colIndex, (exact ? "==" : "contains"), value);
            return true;
        } catch (NumberFormatException e) {
            Log.error(e, "Invalid column index format");
            return false;
        }
    }

    private boolean addRangeFilter(CoreController controller, String[] args) {
        if (args.length < 5) {
            Log.error("Missing arguments for range filter");
            Log.message("Usage: filter add range <col_index> <min> <max>");
            Log.message("Use 'null' for unbounded min or max");
            return false;
        }

        try {
            int colIndex = Integer.parseInt(args[2]);
            Double min = args[3].equalsIgnoreCase("null") ? null : Double.parseDouble(args[3]);
            Double max = args[4].equalsIgnoreCase("null") ? null : Double.parseDouble(args[4]);

            IFilter filter = new RangeFilter(colIndex, min, max);
            controller.getFilter().add(filter);
            Log.message("Added range filter: column[%d] in [%s, %s]", colIndex, (min != null ? min : "-infinity"),
                    (max != null ? max : "+infinity"));
            return true;
        } catch (NumberFormatException e) {
            Log.error("Error: Invalid number format");
            return false;
        }
    }

    private boolean listFilters(CoreController controller) {
        List<IFilter> filters = controller.getFilter().getAll();

        if (filters.isEmpty()) {
            Log.message("No active filters");
        } else {
            Log.message("Active filters (" + filters.size() + "):");
            for (int i = 0; i < filters.size(); i++) {
                IFilter filter = filters.get(i);
                Log.message("  %d. %s", (i + 1), filter);
            }
        }
        return true;
    }

    private boolean clearFilters(CoreController controller) {
        controller.getFilter().clear();
        Log.message("All filters cleared");
        return true;
    }

    private boolean applyFilters(CoreController controller) {
        CSVTable filtered = controller.getFilter().apply(controller.getTable());
        Log.message("Filters applied");
        Log.message("Rows after filtering: %d", filtered.rows().size());
        return true;
    }

    @Override
    public String getName() {
        return "filter";
    }

    @Override
    public String getDescription() {
        return "Manage filters on CSV data";
    }

    @Override
    public String getUsage() {
        StringBuilder usage = new StringBuilder();
        usage.append("filter <action> [args...]\n");
        usage.append("Actions:\n");
        usage.append("add column <col_index> <value> [exact]\n");
        usage.append("add range <col_index> <min> <max>\n");
        usage.append("list\n");
        usage.append("clear\n");
        usage.append("apply");
        return usage.toString();
    }
}
