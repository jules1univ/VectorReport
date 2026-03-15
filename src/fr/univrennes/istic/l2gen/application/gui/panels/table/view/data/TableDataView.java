package fr.univrennes.istic.l2gen.application.gui.panels.table.view.data;

import fr.univrennes.istic.l2gen.application.gui.GUIController;
import fr.univrennes.istic.l2gen.application.gui.panels.table.TablePanel;
import fr.univrennes.istic.l2gen.application.gui.panels.table.filter.FilterDialog;
import fr.univrennes.istic.l2gen.io.csv.model.CSVTable;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public final class TableDataView extends JPanel {

    private final TableModel tableModel;
    private final JTable table;
    private final TablePagination paginationBar;

    public TableDataView(TablePanel tablePanel, GUIController controller) {
        super(new BorderLayout());

        tableModel = new TableModel(this);
        table = new JTable(tableModel);
        table.setFillsViewportHeight(true);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.setRowSorter(null);
        table.getTableHeader().setReorderingAllowed(false);

        TableDataView selfView = this;

        table.getTableHeader().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int clickedColumnIndex = table.columnAtPoint(e.getPoint());
                if (clickedColumnIndex == -1) {
                    return;
                }
                if (SwingUtilities.isRightMouseButton(e)) {
                    TableColumnContextMenu contextMenu = new TableColumnContextMenu(selfView, controller,
                            clickedColumnIndex);
                    contextMenu.show(table.getTableHeader(), e.getX(), e.getY());
                } else if (SwingUtilities.isLeftMouseButton(e)) {
                    selectColumn(clickedColumnIndex);
                    controller.onColumnSelected(clickedColumnIndex);
                }
            }
        });

        paginationBar = new TablePagination(tableModel, controller);

        add(new TableToolBar(controller, tablePanel, tablePanel::closeTable), BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(paginationBar, BorderLayout.SOUTH);
    }

    public void load(CSVTable data) {
        tableModel.load(data);
        paginationBar.refresh();
        adjustColumnWidths();
    }

    public JTable getTable() {
        return table;
    }

    public TableModel getTableModel() {
        return tableModel;
    }

    public void hideColumn(int columnIndex) {
        TableColumnModel columnModel = table.getColumnModel();
        if (columnIndex < 0 || columnIndex >= columnModel.getColumnCount()) {
            return;
        }
        table.removeColumn(columnModel.getColumn(columnIndex));
    }

    public void renameColumn(int columnIndex, String newName) {
        TableColumnModel columnModel = table.getColumnModel();
        if (columnIndex < 0 || columnIndex >= columnModel.getColumnCount()) {
            return;
        }
        columnModel.getColumn(columnIndex).setHeaderValue(newName);
        table.getTableHeader().repaint();
    }

    public void showAllColumns(int totalColumnCount) {
        TableColumnModel columnModel = table.getColumnModel();
        for (int modelIndex = 0; modelIndex < totalColumnCount; modelIndex++) {
            boolean alreadyVisible = false;
            for (int viewIndex = 0; viewIndex < columnModel.getColumnCount(); viewIndex++) {
                if (columnModel.getColumn(viewIndex).getModelIndex() == modelIndex) {
                    alreadyVisible = true;
                    break;
                }
            }
            if (!alreadyVisible) {
                TableColumn column = new TableColumn(modelIndex);
                column.setHeaderValue(tableModel.getColumnName(modelIndex));
                table.addColumn(column);
            }
        }
        adjustColumnWidths();
    }

    public void openAdvancedFilterDialog(GUIController controller) {
        Window parentWindow = SwingUtilities.getWindowAncestor(this);
        int columnCount = table.getColumnCount();
        String[] columnNames = new String[columnCount];
        for (int i = 0; i < columnCount; i++) {
            columnNames[i] = table.getColumnName(i);
        }
        FilterDialog filterDialog = new FilterDialog(parentWindow, columnNames, controller);
        filterDialog.setVisible(true);
    }

    public void selectColumn(int columnIndex) {
        TableColumnModel columnModel = table.getColumnModel();
        if (columnIndex < 0 || columnIndex >= columnModel.getColumnCount()) {
            return;
        }
        table.setColumnSelectionAllowed(true);
        table.setRowSelectionAllowed(false);
        table.clearSelection();
        table.setColumnSelectionInterval(columnIndex, columnIndex);
        table.selectAll();
        table.setColumnSelectionInterval(columnIndex, columnIndex);
    }

    public void adjustColumnWidths() {
        int sampleRowCount = Math.min(table.getRowCount(), 50);
        for (int columnIndex = 0; columnIndex < table.getColumnCount(); columnIndex++) {
            int maxWidth = 0;
            TableColumn tableColumn = table.getColumnModel().getColumn(columnIndex);

            TableCellRenderer headerRenderer = table.getTableHeader().getDefaultRenderer();
            Component headerComponent = headerRenderer.getTableCellRendererComponent(
                    table, tableColumn.getHeaderValue(), false, false, -1, columnIndex);
            maxWidth = Math.max(maxWidth, headerComponent.getPreferredSize().width);

            for (int rowIndex = 0; rowIndex < sampleRowCount; rowIndex++) {
                TableCellRenderer cellRenderer = table.getCellRenderer(rowIndex, columnIndex);
                Component cellComponent = cellRenderer.getTableCellRendererComponent(
                        table, table.getValueAt(rowIndex, columnIndex), false, false, rowIndex, columnIndex);
                maxWidth = Math.max(maxWidth, cellComponent.getPreferredSize().width);
            }

            tableColumn.setPreferredWidth(maxWidth + 16);
        }
    }
}