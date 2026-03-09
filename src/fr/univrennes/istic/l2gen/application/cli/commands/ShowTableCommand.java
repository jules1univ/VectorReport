package fr.univrennes.istic.l2gen.application.cli.commands;

import java.util.List;

import fr.univrennes.istic.l2gen.application.cli.util.log.Log;
import fr.univrennes.istic.l2gen.application.core.CoreController;
import fr.univrennes.istic.l2gen.io.csv.model.CSVRow;
import fr.univrennes.istic.l2gen.io.csv.model.CSVTable;

public final class ShowTableCommand implements ICommand {

    @Override
    public boolean execute(CoreController controller, String[] args) {
        String filename = null;
        int limit = 10;
        int offset = 0;

        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            if (arg.equals("--limit") && i + 1 < args.length) {
                try {
                    limit = Integer.parseInt(args[++i]);
                } catch (NumberFormatException e) {
                    Log.error(e, "Invalid limit value '%s'", args[i]);
                    return false;
                }
            } else if (arg.equals("--offset") && i + 1 < args.length) {
                try {
                    offset = Integer.parseInt(args[++i]);
                } catch (NumberFormatException e) {
                    Log.error(e, "Invalid offset value '%s'", args[i]);
                    return false;
                }
            } else if (!arg.startsWith("--")) {
                filename = arg;
            } else {
                Log.error("Unknown argument '%s'", arg);
                return false;
            }
        }

        if (filename == null) {
            Log.error("Filename is required");
            return false;
        }

        CSVTable table = controller.getLoader().getTableByName(filename);
        if (table == null) {
            Log.error("Table '%s' not found", filename);
            return false;
        }

        CSVRow header = table.header();
        List<CSVRow> rows = table.rows();

        int maxWidth = header.values().stream()
                .mapToInt(String::length)
                .max()
                .orElse(20);

        StringBuilder sb = new StringBuilder();
        sb.append("| ");
        for (String col : header.values()) {
            sb.append(String.format("%-20s", col)).append(" | ");
        }
        sb.append("\n");
        sb.append("|");

        for (int i = 0; i < header.values().size(); i++) {
            sb.append("-".repeat(maxWidth + 2)).append("|");
        }
        sb.append("\n");
        for (int i = offset; i < Math.min(rows.size(), offset + limit); i++) {
            CSVRow row = rows.get(i);
            sb.append("| ");
            for (String cell : row.values()) {
                sb.append(String.format("%-20s", cell)).append(" | ");
            }
            sb.append("\n");
        }
        Log.message(sb.toString());
        return true;
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
        return "show [filename] [--limit N] [--offset N]";
    }

}
