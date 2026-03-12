package fr.univrennes.istic.l2gen.application.gui.panels.table;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;

import fr.univrennes.istic.l2gen.application.core.services.stats.CorrelationType;
import fr.univrennes.istic.l2gen.application.gui.GUIController;
import fr.univrennes.istic.l2gen.io.csv.model.CSVType;

import java.awt.Component;

final class TableContextMenu extends JPopupMenu {

    private final GUIController controller;
    private final TablePanel tablePanel;
    private final int columnIndex;
    private final Component parentComponent;

    public TableContextMenu(GUIController controller, TablePanel tablePanel, int columnIndex,
            Component parentComponent) {
        this.controller = controller;
        this.tablePanel = tablePanel;
        this.columnIndex = columnIndex;
        this.parentComponent = parentComponent;

        add(buildSortMenu());
        addSeparator();
        add(buildFilterMenu());
        addSeparator();
        add(buildCorrelateMenu());
        addSeparator();
        add(buildTypeMenu());

        JMenuItem renameColumnItem = new JMenuItem("Rename column");
        renameColumnItem.addActionListener(e -> {
            String newName = JOptionPane.showInputDialog(parentComponent, "New column name:",
                    tablePanel.getTable().getColumnName(columnIndex));
            if (newName != null && !newName.isBlank()) {
                tablePanel.renameColumn(columnIndex, newName);
            }
        });
        add(renameColumnItem);

        JMenuItem hideColumnItem = new JMenuItem("Hide column");
        hideColumnItem.addActionListener(e -> tablePanel.hideColumn(columnIndex));

        JMenuItem removeColumnItem = new JMenuItem("Remove column");
        removeColumnItem.addActionListener(e -> controller.onColumnRemoveRequested(columnIndex));

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

        JMenu filterByCategoryMenu = new JMenu("By category");
        JMenuItem filterCatCountItem = new JMenuItem("Count items");
        filterCatCountItem.addActionListener(e -> controller.onFilterByCategoryRequested(columnIndex, false));
        JMenuItem filterCatPercentageItem = new JMenuItem("Percentage of items");
        filterCatPercentageItem.addActionListener(e -> controller.onFilterByCategoryRequested(columnIndex, true));
        filterByCategoryMenu.add(filterCatCountItem);
        filterByCategoryMenu.add(filterCatPercentageItem);

        JMenuItem filterByValueItem = new JMenuItem("By value (equals/contains)");
        filterByValueItem.addActionListener(e -> {
            String value = JOptionPane.showInputDialog(parentComponent, "Filter rows where column equals or contains:");
            if (value != null && !value.isBlank()) {
                controller.onFilterByValueRequested(columnIndex, value.trim());
            }
        });

        JMenuItem filterTopNItem = new JMenuItem("Top N values");
        filterTopNItem.addActionListener(e -> {
            String input = JOptionPane.showInputDialog(parentComponent, "Show top N values:");
            if (input != null && !input.isBlank()) {
                try {
                    int n = Integer.parseInt(input.trim());
                    controller.onFilterTopNRequested(columnIndex, n, true);
                } catch (NumberFormatException ignored) {
                    JOptionPane.showMessageDialog(parentComponent, "Please enter a valid integer.");
                }
            }
        });

        JMenuItem filterBottomNItem = new JMenuItem("Bottom N values");
        filterBottomNItem.addActionListener(e -> {
            String input = JOptionPane.showInputDialog(parentComponent, "Show bottom N values:");
            if (input != null && !input.isBlank()) {
                try {
                    int n = Integer.parseInt(input.trim());
                    controller.onFilterTopNRequested(columnIndex, n, false);
                } catch (NumberFormatException ignored) {
                    JOptionPane.showMessageDialog(parentComponent, "Please enter a valid integer.");
                }
            }
        });

        JMenuItem filterNumericRangeItem = new JMenuItem("By numeric range");
        filterNumericRangeItem.addActionListener(e -> {
            String minInput = JOptionPane.showInputDialog(parentComponent, "Minimum value:");
            if (minInput == null) {
                return;
            }
            String maxInput = JOptionPane.showInputDialog(parentComponent, "Maximum value:");
            if (maxInput == null) {
                return;
            }
            try {
                double min = Double.parseDouble(minInput.trim());
                double max = Double.parseDouble(maxInput.trim());
                controller.onFilterByRangeRequested(columnIndex, min, max);
            } catch (NumberFormatException ignored) {
                JOptionPane.showMessageDialog(parentComponent, "Please enter valid numbers.");
            }
        });

        JMenuItem filterEmptyItem = new JMenuItem("Show empty/null only");
        filterEmptyItem.addActionListener(e -> controller.onFilterEmptyRequested(columnIndex));

        JMenuItem clearFilterItem = new JMenuItem("Clear filter");
        clearFilterItem.addActionListener(e -> controller.onFilterCleared(columnIndex));

        filterMenu.add(filterByCategoryMenu);
        filterMenu.addSeparator();
        filterMenu.add(filterByValueItem);
        filterMenu.add(filterTopNItem);
        filterMenu.add(filterBottomNItem);
        filterMenu.add(filterNumericRangeItem);
        filterMenu.add(filterEmptyItem);
        filterMenu.addSeparator();
        filterMenu.add(clearFilterItem);
        return filterMenu;
    }

    private JMenu buildCorrelateMenu() {
        JMenu correlateMenu = new JMenu("Correlate");

        JMenu pearsonMenu = new JMenu("Pearson correlation");
        JMenu spearmanMenu = new JMenu("Spearman correlation");
        JMenu kendallMenu = new JMenu("Kendall's tau correlation");

        for (int i = 0; i < tablePanel.getTable().getColumnCount(); i++) {
            if (i == columnIndex) {
                continue;
            }
            int targetColumnIndex = i;
            String columnName = tablePanel.getTable().getColumnName(i);

            JMenuItem pearsonItem = new JMenuItem(columnName);
            pearsonItem.addActionListener(
                    e -> controller.onCorrelationRequested(columnIndex, targetColumnIndex, CorrelationType.PEARSON));
            pearsonMenu.add(pearsonItem);

            JMenuItem spearmanItem = new JMenuItem(columnName);
            spearmanItem.addActionListener(
                    e -> controller.onCorrelationRequested(columnIndex, targetColumnIndex, CorrelationType.SPEARMAN));
            spearmanMenu.add(spearmanItem);

            JMenuItem kendallItem = new JMenuItem(columnName);
            kendallItem.addActionListener(
                    e -> controller.onCorrelationRequested(columnIndex, targetColumnIndex, CorrelationType.KENDALL));
            kendallMenu.add(kendallItem);
        }

        JMenuItem valueDistributionItem = new JMenuItem("Value distribution (histogram)");
        valueDistributionItem.addActionListener(e -> controller.onValueDistributionRequested(columnIndex));

        correlateMenu.add(pearsonMenu);
        correlateMenu.add(spearmanMenu);
        correlateMenu.add(kendallMenu);
        correlateMenu.add(valueDistributionItem);
        correlateMenu.addSeparator();
        return correlateMenu;
    }

    private JMenu buildTypeMenu() {
        JMenu typeMenu = new JMenu("Change type");
        for (CSVType type : CSVType.values()) {
            JMenuItem typeItem = new JMenuItem(type.name());
            typeItem.addActionListener(e -> controller.onColumnTypeChangeRequested(columnIndex, type));
            typeMenu.add(typeItem);
        }
        return typeMenu;
    }
}