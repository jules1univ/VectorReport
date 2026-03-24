package fr.univrennes.istic.l2gen.application.gui.panels.table.view.data;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;

import fr.univrennes.istic.l2gen.application.gui.GUIController;

public final class TableColumnContextMenu extends JPopupMenu {

    private final GUIController controller;
    private final TableDataView tableView;
    private final int columnIndex;

    public TableColumnContextMenu(TableDataView tableView, GUIController controller,
            int columnIndex) {
        this.controller = controller;
        this.tableView = tableView;
        this.columnIndex = columnIndex;

        add(buildSortMenu());
        addSeparator();
        add(buildFilterMenu());
        addSeparator();
        // add(buildCorrelateMenu());

        JMenuItem renameColumnItem = new JMenuItem("Rename column");
        renameColumnItem.addActionListener(e -> {
            String newName = JOptionPane.showInputDialog(tableView, "New column name:",
                    tableView.getTable().getColumnName(columnIndex));
            if (newName != null && !newName.isBlank()) {
                tableView.renameColumn(columnIndex, newName);
            }
        });
        add(renameColumnItem);

        JMenuItem hideColumnItem = new JMenuItem("Hide column");
        hideColumnItem.addActionListener(e -> tableView.hideColumn(columnIndex));

        add(hideColumnItem);
    }

    private JMenu buildSortMenu() {
        JMenu sortMenu = new JMenu("Sort");

        JMenuItem sortAscendingItem = new JMenuItem("Ascending");
        sortAscendingItem.addActionListener(e -> controller.onColumnSortRequested(columnIndex, true));

        JMenuItem sortDescendingItem = new JMenuItem("Descending");
        sortDescendingItem.addActionListener(e -> controller.onColumnSortRequested(columnIndex, false));

        sortMenu.add(sortAscendingItem);
        sortMenu.add(sortDescendingItem);
        return sortMenu;
    }

    private JMenu buildFilterMenu() {
        JMenu filterMenu = new JMenu("Filter");

        JMenuItem filterTopNItem = new JMenuItem("Top N values");
        filterTopNItem.addActionListener(e -> {
            String input = JOptionPane.showInputDialog(tableView, "Show top N values:");
            if (input != null && !input.isBlank()) {
                try {
                    int n = Integer.parseInt(input.trim());
                    controller.onFilterTopNRequested(columnIndex, n, true);
                } catch (NumberFormatException ignored) {
                    JOptionPane.showMessageDialog(tableView, "Please enter a valid integer.");
                }
            }
        });

        JMenuItem filterBottomNItem = new JMenuItem("Bottom N values");
        filterBottomNItem.addActionListener(e -> {
            String input = JOptionPane.showInputDialog(tableView, "Show bottom N values:");
            if (input != null && !input.isBlank()) {
                try {
                    int n = Integer.parseInt(input.trim());
                    controller.onFilterTopNRequested(columnIndex, n, false);
                } catch (NumberFormatException ignored) {
                    JOptionPane.showMessageDialog(tableView, "Please enter a valid integer.");
                }
            }
        });

        JMenuItem filterNumericRangeItem = new JMenuItem("By numeric range");
        filterNumericRangeItem.addActionListener(e -> {
            String minInput = JOptionPane.showInputDialog(tableView, "Minimum value:");
            if (minInput == null) {
                return;
            }
            String maxInput = JOptionPane.showInputDialog(tableView, "Maximum value:");
            if (maxInput == null) {
                return;
            }
            try {
                double min = Double.parseDouble(minInput.trim());
                double max = Double.parseDouble(maxInput.trim());
                controller.onFilterByRangeRequested(columnIndex, min, max);
            } catch (NumberFormatException ignored) {
                JOptionPane.showMessageDialog(tableView, "Please enter valid numbers.");
            }
        });

        JMenuItem filterEmptyItem = new JMenuItem("Show empty values only");
        filterEmptyItem.addActionListener(e -> controller.onFilterEmptyRequested(columnIndex, true));

        JMenuItem filterNonEmptyItem = new JMenuItem("Show non-empty values only");
        filterNonEmptyItem.addActionListener(e -> controller.onFilterEmptyRequested(columnIndex, false));

        JMenuItem clearFilterItem = new JMenuItem("Clear filters");
        clearFilterItem.addActionListener(e -> controller.onFilterCleared(columnIndex));

        filterMenu.add(filterTopNItem);
        filterMenu.add(filterBottomNItem);
        filterMenu.addSeparator();
        filterMenu.add(filterNumericRangeItem);
        filterMenu.addSeparator();
        filterMenu.add(filterEmptyItem);
        filterMenu.add(filterNonEmptyItem);
        filterMenu.addSeparator();
        filterMenu.add(clearFilterItem);
        return filterMenu;
    }

}