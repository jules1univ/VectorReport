package fr.univrennes.istic.l2gen.application.cli.commands;

import java.util.Optional;

import fr.univrennes.istic.l2gen.application.cli.util.log.Log;
import fr.univrennes.istic.l2gen.application.cli.util.log.LogLevel;
import fr.univrennes.istic.l2gen.application.core.CoreController;
import fr.univrennes.istic.l2gen.io.csv.model.CSVTable;

public final class ShowTableCommand implements ICommand {

    @Override
    public boolean execute(CoreController controller, String[] args) {
        if (args.length < 1) {
            Log.error("Missing show action");
            Log.message("Usage: %s", getUsage());
            return false;
        }

        if (controller.getTable() == null) {
            Log.error("No table selected.");
            return false;
        }

        String action = args[0].toLowerCase();
        switch (action) {
            case "header" -> {
                return showHeader(controller);
            }
            case "table" -> {
                return showTable(controller);
            }
            case "summary" -> {
                return showSummary(controller);
            }
            case "range" -> {
                return showRange(controller, args);
            }
            case "column" -> {
                return showColumn(controller, args);
            }
            case "row" -> {
                return showRow(controller, args);
            }
            default -> {
                Log.error("Unknown show action: %s", action);
                Log.message("Usage: %s", getUsage());
                return false;
            }
        }
    }

    private boolean showHeader(CoreController controller) {
        if (controller.getTable().getHeader().isEmpty()) {
            Log.message("No header available for the current table.");
            return true;
        }
        for (int i = 0; i < controller.getTable().getHeader().get().getCells().size(); i++) {
            Optional<String> cell = controller.getTable().getHeader().get().getCell(i);
            Log.message("[%d]: %s", i, cell.orElse(CSVTable.DEFAULT_EMPTY_CELL));
        }
        return true;
    }

    private boolean showTable(CoreController controller) {
        CSVTable table = controller.getTable();
        if (table.getRows().size() > 25) {
            Log.log(LogLevel.WARNING, "Table is too large to display fully (%d rows). Showing first 25 rows instead.",
                    table.getRows().size());
            Log.message(table.rangeToString(0, 25));
        } else {
            Log.message(table.toString());
        }
        return true;
    }

    private boolean showSummary(CoreController controller) {
        CSVTable table = controller.getTable();
        Log.message("Table Summary:");
        Log.message("Total Rows: %d", table.getRows().size());

        int totalCols = 0;
        if (table.getHeader().isPresent()) {
            totalCols = table.getHeader().get().getCells().size();
        } else if (!table.getRows().isEmpty()) {
            totalCols = table.getRows().get(0).getCells().size();
        }
        Log.message("Total Columns: %d", totalCols);
        // TODO: Add more summary information like column types, sample values, etc.
        return true;
    }

    private boolean showRange(CoreController controller, String[] args) {
        if (args.length < 3) {
            Log.error("Missing arguments for range show");
            Log.message("Usage: show range <offset> <limit>");
            return false;
        }
        try {
            int[] range = this.parseRangeArgs(args, 1);
            Log.message(controller.getTable().rangeToString(range[0], range[1]));
            return true;
        } catch (NumberFormatException e) {
            Log.error(e, "Invalid offset or limit value");
            return false;
        }
    }

    private boolean showColumn(CoreController controller, String[] args) {
        if (args.length < 2) {
            Log.error("Missing arguments for column show");
            Log.message("Usage: show column <col_index>");
            return false;
        }
        try {
            int colIndex = Integer.parseInt(args[1]);
            int[] range = this.parseRangeArgs(args, 2);
            Log.message(controller.getTable().columnToString(colIndex, range[0], range[1]));
            return true;
        } catch (NumberFormatException e) {
            Log.error(e, "Invalid column index value");
            return false;
        }
    }

    private boolean showRow(CoreController controller, String[] args) {
        if (args.length < 2) {
            Log.error("Missing arguments for row show");
            Log.message("Usage: show row <row_index>");
            return false;
        }

        try {
            int rowIndex = Integer.parseInt(args[1]);
            int[] range = this.parseRangeArgs(args, 2);
            Log.message(controller.getTable().rowToString(rowIndex, range[0], range[1]));
            return true;
        } catch (NumberFormatException e) {
            Log.error(e, "Invalid row index value");
            return false;
        }
    }

    private int[] parseRangeArgs(String[] args, int index) {
        int offset = 0;
        int limit = 25;

        try {
            offset = Integer.parseInt(args[index]);
        } catch (NumberFormatException e) {
            Log.error(e, "Invalid offset value");
        }

        try {
            limit = Integer.parseInt(args[index + 1]);
        } catch (NumberFormatException e) {
            Log.error(e, "Invalid limit value");
        }

        return new int[] { offset, limit };
    }

    @Override
    public String getName() {
        return "show";
    }

    @Override
    public String getDescription() {
        return "Display the current table with applied filters";
    }

    @Override
    public String getUsage() {
        StringBuilder usage = new StringBuilder();
        usage.append("show <action> [options]\n");
        usage.append("Actions:\n");
        usage.append("header - Show the table header if available\n");
        usage.append("table - Show the table with applied filters\n");
        usage.append("summary - Show a summary of the table (row count, column count, etc.)\n");
        usage.append("range <offset> <limit> - Show rows where a numeric column is within the specified range\n");
        usage.append("column <col_index> [offset] [limit] - Show a specific column\n");
        usage.append("row <row_index> [offset] [limit] - Show a specific row\n");
        return usage.toString();
    }

}
