package fr.univrennes.istic.l2gen.application.gui.panels.table.view.data;

import java.util.function.Function;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;

import fr.univrennes.istic.l2gen.application.core.lang.Lang;
import fr.univrennes.istic.l2gen.application.core.table.DataTable;
import fr.univrennes.istic.l2gen.application.gui.GUIController;

public final class TableColumnContextMenu extends JPopupMenu {

    private final GUIController controller;
    private final TableDataView tableView;
    private final DataTable table;
    private final int columnIndex;

    public TableColumnContextMenu(TableDataView tableView, GUIController controller,
            int columnIndex) {
        this.controller = controller;
        this.tableView = tableView;
        this.columnIndex = columnIndex;
        this.table = tableView.getTableModel().getTable().get();

        add(buildSortMenu());
        addSeparator();
        add(buildFilterMenu());
        addSeparator();
        add(buildStatsMenu());
        addSeparator();
        JMenuItem renameColumnItem = new JMenuItem(Lang.get("tablecolumnmenu.rename"));
        renameColumnItem.addActionListener(e -> {
            String newName = JOptionPane.showInputDialog(tableView, Lang.get("tablecolumnmenu.enter_new_name"),
                    tableView.getTableView().getColumnName(columnIndex));
            if (newName != null && !newName.isBlank()) {
                tableView.renameColumn(columnIndex, newName);
            }
        });
        add(renameColumnItem);

        JMenuItem hideColumnItem = new JMenuItem(Lang.get("tablecolumnmenu.hide"));
        hideColumnItem.addActionListener(e -> tableView.hideColumn(columnIndex));

        add(hideColumnItem);
    }

    private JMenu buildSortMenu() {
        JMenu sortMenu = new JMenu(Lang.get("tablecolumnmenu.sort"));

        JMenuItem sortAscendingItem = new JMenuItem(Lang.get("tablecolumnmenu.sort.ascending"));
        sortAscendingItem.addActionListener(e -> controller.onColumnSortRequested(columnIndex, true));

        JMenuItem sortDescendingItem = new JMenuItem(Lang.get("tablecolumnmenu.sort.descending"));
        sortDescendingItem.addActionListener(e -> controller.onColumnSortRequested(columnIndex, false));

        sortMenu.add(sortAscendingItem);
        sortMenu.add(sortDescendingItem);
        return sortMenu;
    }

    private JMenu buildFilterMenu() {
        JMenu filterMenu = new JMenu(Lang.get("tablecolumnmenu.filter"));

        JMenuItem filterTopNItem = new JMenuItem(Lang.get("tablecolumnmenu.filter.topn"));
        filterTopNItem.addActionListener(e -> {
            String input = JOptionPane.showInputDialog(tableView, Lang.get("tablecolumnmenu.filter.enter_topn"));
            if (input != null && !input.isBlank()) {
                try {
                    int n = Integer.parseInt(input.trim());
                    controller.onFilterTopNRequested(columnIndex, n, true);
                } catch (NumberFormatException ignored) {
                    JOptionPane.showMessageDialog(tableView, Lang.get("error.invalid_number"));
                }
            }
        });

        JMenuItem filterBottomNItem = new JMenuItem(Lang.get("tablecolumnmenu.filter.bottomn"));
        filterBottomNItem.addActionListener(e -> {
            String input = JOptionPane.showInputDialog(tableView, Lang.get("tablecolumnmenu.filter.enter_bottomn"));
            if (input != null && !input.isBlank()) {
                try {
                    int n = Integer.parseInt(input.trim());
                    controller.onFilterTopNRequested(columnIndex, n, false);
                } catch (NumberFormatException ignored) {
                    JOptionPane.showMessageDialog(tableView, Lang.get("error.invalid_number"));
                }
            }
        });

        JMenuItem filterNumericRangeItem = new JMenuItem(Lang.get("tablecolumnmenu.filter.numeric_range"));
        filterNumericRangeItem.addActionListener(e -> {
            String minInput = JOptionPane.showInputDialog(tableView, Lang.get("tablecolumnmenu.filter.enter_min"));
            if (minInput == null) {
                return;
            }
            String maxInput = JOptionPane.showInputDialog(tableView, Lang.get("tablecolumnmenu.filter.enter_max"));
            if (maxInput == null) {
                return;
            }
            try {
                double min = Double.parseDouble(minInput.trim());
                double max = Double.parseDouble(maxInput.trim());
                controller.onFilterByRangeRequested(columnIndex, min, max);
            } catch (NumberFormatException ignored) {
                JOptionPane.showMessageDialog(tableView, Lang.get("error.invalid_number"));
            }
        });

        JMenuItem filterEmptyItem = new JMenuItem(Lang.get("tablecolumnmenu.filter.empty"));
        filterEmptyItem.addActionListener(e -> controller.onFilterEmptyRequested(columnIndex, true));

        JMenuItem filterNonEmptyItem = new JMenuItem(Lang.get("tablecolumnmenu.filter.non_empty"));
        filterNonEmptyItem.addActionListener(e -> controller.onFilterEmptyRequested(columnIndex, false));

        JMenuItem clearFilterItem = new JMenuItem(Lang.get("tablecolumnmenu.filter.clear"));
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

    private JMenu buildStatsMenu() {

        JMenu stats = new JMenu(Lang.get("tablecolumnmenu.stats"));
        JMenuItem summaryItem = new JMenuItem(Lang.get("tablecolumnmenu.stats.summary"));
        summaryItem.addActionListener(e -> controller.onComputeSummaryRequested(columnIndex));
        stats.add(summaryItem);

        JMenuItem nullRateItem = new JMenuItem(Lang.get("tablecolumnmenu.stats.null_rate"));
        nullRateItem.addActionListener(e -> controller.onComputeNullRateRequested(columnIndex));
        stats.add(nullRateItem);

        JMenuItem cardinalityRatioItem = new JMenuItem(Lang.get("tablecolumnmenu.stats.cardinality_ratio"));
        cardinalityRatioItem.addActionListener(e -> controller.onComputeCardinalityRatioRequested(columnIndex));
        stats.add(cardinalityRatioItem);

        if (this.table.getColumnType(columnIndex).isNumeric()) {
            JMenuItem interquartileRangeItem = new JMenuItem(Lang.get("tablecolumnmenu.stats.interquartile_range"));
            interquartileRangeItem.addActionListener(e -> controller.onComputeInterquartileRangeRequested(columnIndex));
            stats.add(interquartileRangeItem);

            JMenuItem skewnessItem = new JMenuItem(Lang.get("tablecolumnmenu.stats.skewness"));
            skewnessItem.addActionListener(e -> controller.onComputeSkewnessRequested(columnIndex));
            stats.add(skewnessItem);

            JMenuItem coefVariationItem = new JMenuItem(Lang.get("tablecolumnmenu.stats.coef_variation"));
            coefVariationItem.addActionListener(e -> controller.onComputeCoefficientOfVariationRequested(columnIndex));
            stats.add(coefVariationItem);

            JMenu correlationItem = new JMenu(Lang.get("tablecolumnmenu.stats.correlation"));
            this.columnSelector(correlationItem,
                    i -> i != columnIndex && table.getColumnType(i).isNumeric(),
                    targetIndex -> {
                        controller.onComputeCorrelationRequested(columnIndex, targetIndex);
                        return null;
                    });

            stats.add(correlationItem);
        }

        return stats;
    }

    private void columnSelector(JMenu menu, Function<Integer, Boolean> condition, Function<Integer, Void> action) {
        for (int i = 0; i < table.getColumnCount(); i++) {
            if (!condition.apply(i)) {
                continue;
            }
            JMenuItem colItem = new JMenuItem(table.getColumnName(i));
            int colIndex = i;
            colItem.addActionListener(e -> action.apply(colIndex));
            menu.add(colItem);
        }
    }
}