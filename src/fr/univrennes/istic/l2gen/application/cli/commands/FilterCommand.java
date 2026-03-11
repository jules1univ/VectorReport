package fr.univrennes.istic.l2gen.application.cli.commands;

import java.util.List;
import java.util.Optional;

import fr.univrennes.istic.l2gen.application.cli.util.log.Log;
import fr.univrennes.istic.l2gen.application.core.CoreController;
import fr.univrennes.istic.l2gen.application.core.filter.IFilter;
import fr.univrennes.istic.l2gen.application.core.filter.column.ColumnFilter;
import fr.univrennes.istic.l2gen.application.core.filter.comparaison.ComparisonFilter;
import fr.univrennes.istic.l2gen.application.core.filter.comparaison.ComparisonOperator;
import fr.univrennes.istic.l2gen.application.core.filter.range.RangeFilter;
import fr.univrennes.istic.l2gen.application.core.filter.type.TypeFilter;
import fr.univrennes.istic.l2gen.io.csv.model.CSVSubtype;
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
            case "sort" -> {
                return sortTable(controller, args);
            }
            case "cleanup" -> {
                return cleanupTable(controller, args);
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
            Log.message("Available filter types: column, range, compare, type");
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
            case "compare" -> {
                return addCompareFilter(controller, args);
            }
            case "type" -> {
                return addTypeFilter(controller, args);
            }
            default -> {
                Log.error("Unknown filter type: %s", filterType);
                Log.message("Available filter types: column, range, compare, type");
                return false;
            }
        }
    }

    private boolean addTypeFilter(CoreController controller, String[] args) {
        if (args.length < 4) {
            Log.error("Missing arguments for type filter");
            Log.message("Usage: filter add type <col_index|col_name> <type>");
            Log.message("Types: string, numeric, integer, floating, url, email, boolean, date, empty");
            return false;
        }

        try {
            int colIndex = this.getColumnIndex(controller.getTable(), args[2]);
            String typeStr = args[3].toLowerCase();
            CSVSubtype valueType = switch (typeStr) {
                case "string" -> CSVSubtype.STRING;
                case "numeric", "number" -> CSVSubtype.FLOATING;
                case "integer" -> CSVSubtype.INTEGER;
                case "floating" -> CSVSubtype.FLOATING;
                case "url" -> CSVSubtype.URL;
                case "email" -> CSVSubtype.EMAIL;
                case "boolean" -> CSVSubtype.BOOLEAN;
                case "date" -> CSVSubtype.DATE;
                case "empty" -> CSVSubtype.EMPTY;
                default -> throw new IllegalArgumentException("Unknown type: " + typeStr);
            };

            IFilter filter = new TypeFilter(colIndex, valueType);
            controller.getFilter().add(filter);

            Log.message("Added type filter: column[%d] is %s", colIndex, valueType.name().toLowerCase());
            return true;
        } catch (NumberFormatException e) {
            Log.error(e, "Invalid column index format");
            return false;
        } catch (IllegalArgumentException e) {
            Log.error(e.getMessage());
            return false;
        }
    }

    private boolean addColumnFilter(CoreController controller, String[] args) {
        if (args.length < 4) {
            Log.error("Missing arguments for column filter");
            Log.message("Usage: filter add column <col_index> <value> [exact]");
            return false;
        }

        try {
            int colIndex = this.getColumnIndex(controller.getTable(), args[2]);
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
            int colIndex = this.getColumnIndex(controller.getTable(), args[2]);
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

    private boolean addCompareFilter(CoreController controller, String[] args) {
        if (args.length < 4) {
            Log.error("Missing arguments for compare filter");
            Log.message("Usage: filter add compare <col_index> <operator> [value] [numeric|text]");
            Log.message("Operators: =, !=, >, >=, <, <=, contains, startsWith, endsWith, empty, notEmpty");
            return false;
        }

        try {
            int colIndex = this.getColumnIndex(controller.getTable(), args[2]);
            ComparisonOperator operator = ComparisonOperator.parse(args[3]);

            String value = null;
            boolean numeric = false;

            if (operator != ComparisonOperator.EMPTY && operator != ComparisonOperator.NOT_EMPTY) {
                if (args.length < 5) {
                    Log.error("Missing comparison value");
                    return false;
                }
                value = args[4];
            }

            if (args.length > 5) {
                numeric = args[5].equalsIgnoreCase("numeric") || args[5].equalsIgnoreCase("number");
            }

            IFilter filter = new ComparisonFilter(colIndex, operator, value, numeric, true);
            controller.getFilter().add(filter);

            Log.message("Added comparison filter: column[%d] %s %s", colIndex, operator,
                    value == null ? "" : ("'" + value + "'"));
            return true;
        } catch (NumberFormatException e) {
            Log.error(e, "Invalid column index format");
            return false;
        } catch (IllegalArgumentException e) {
            Log.error(e.getMessage());
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
        if (controller.getTable() == null) {
            Log.error("No table selected. Load a table first.");
            return false;
        }

        CSVTable filtered = controller.getFilter().apply(controller.getTable());
        controller.setTable(filtered);
        Log.message("Filters applied");
        Log.message("Rows after filtering: %d", filtered.getRows().size());
        return true;
    }

    private boolean sortTable(CoreController controller, String[] args) {
        if (controller.getTable() == null) {
            Log.error("No table selected. Load a table first.");
            return false;
        }
        if (args.length < 3) {
            Log.error("Missing arguments for sorting");
            Log.message("Usage: filter sort <col_index> <asc|desc> [numeric|text]");
            return false;
        }

        try {
            int colIndex = this.getColumnIndex(controller.getTable(), args[1]);
            boolean ascending = !args[2].equalsIgnoreCase("desc");
            boolean numeric = args.length > 3
                    && (args[3].equalsIgnoreCase("numeric") || args[3].equalsIgnoreCase("number"));

            CSVTable sorted = controller.getFilter().sortByColumn(controller.getTable(), colIndex, ascending, numeric);
            controller.setTable(sorted);
            Log.message("Table sorted by column[%d] (%s, %s)", colIndex, ascending ? "asc" : "desc",
                    numeric ? "numeric" : "text");
            return true;
        } catch (NumberFormatException e) {
            Log.error(e, "Invalid column index format");
            return false;
        }
    }

    private boolean cleanupTable(CoreController controller, String[] args) {
        if (controller.getTable() == null) {
            Log.error("No table selected. Load a table first.");
            return false;
        }
        if (args.length < 2) {
            Log.error("Missing cleanup target");
            Log.message("Usage: filter cleanup <rows|columns|all>");
            return false;
        }

        CSVTable table = controller.getTable();
        CSVTable result;
        switch (args[1].toLowerCase()) {
            case "rows" -> result = controller.getFilter().removeEmptyRows(table);
            case "columns", "cols" -> result = controller.getFilter().removeEmptyColumns(table);
            case "all" ->
                result = controller.getFilter().removeEmptyColumns(controller.getFilter().removeEmptyRows(table));
            default -> {
                Log.error("Unknown cleanup target: %s", args[1]);
                Log.message("Usage: filter cleanup <rows|columns|all>");
                return false;
            }
        }

        controller.setTable(result);
        Log.message("Cleanup applied. Rows: %d", result.getRows().size());
        return true;
    }

    private int getColumnIndex(CSVTable table, String colIdentifier) {
        try {
            return Integer.parseInt(colIdentifier);
        } catch (NumberFormatException e) {
            if (table.getHeader().isPresent()) {
                int index = table.getHeader().get().getCells().indexOf(Optional.of(colIdentifier));
                if (index == -1) {
                    throw new IllegalArgumentException("Column not found: " + colIdentifier);
                }
                return index;
            } else {
                throw new IllegalArgumentException("Table has no header, column must be specified by index");
            }
        }
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

        usage.append("add column <col_index|col_name> <value> [exact] - Add a column filter\n");
        usage.append("add range <col_index|col_name> <min> <max> - Add a range filter\n");
        usage.append("add compare <col_index|col_name> <operator> [value] [numeric|text] - Add comparison filter\n");
        usage.append(
                "add type <col_index|col_name> <type> - Add a type filter (string|numeric|integer|floating|url|email|boolean|date|empty|notEmpty)\n");
        usage.append("sort <col_index|col_name> <asc|desc> [numeric|text] - Sort current table\n");

        usage.append("cleanup <rows|columns|all> - Remove empty rows/columns\n");
        usage.append("apply - Apply active filters to current table\n");
        usage.append("list - List all active filters\n");
        usage.append("clear - Clear all filters\n");
        return usage.toString();
    }
}
