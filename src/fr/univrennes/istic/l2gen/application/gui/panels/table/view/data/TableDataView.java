package fr.univrennes.istic.l2gen.application.gui.panels.table.view.data;

import fr.univrennes.istic.l2gen.application.core.table.DataTable;
import fr.univrennes.istic.l2gen.application.core.table.DataType;
import fr.univrennes.istic.l2gen.application.gui.GUIController;
import fr.univrennes.istic.l2gen.application.gui.panels.table.TablePanel;

import javax.swing.Icon;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import com.formdev.flatlaf.extras.FlatSVGIcon;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public final class TableDataView extends JPanel {

    private final TableModel tableModel;
    private final JTable tableView;
    private final TablePagination paginationBar;

    public TableDataView(TablePanel tablePanel, GUIController controller) {
        super(new BorderLayout());

        tableModel = new TableModel(this);
        tableView = new JTable(tableModel);
        tableView.setFillsViewportHeight(true);
        tableView.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tableView.setRowSorter(null);
        tableView.getTableHeader().setReorderingAllowed(false);

        TableDataView selfView = this;

        TableDataViewHeader headerRenderer = new TableDataViewHeader(tableView.getTableHeader().getDefaultRenderer());
        tableView.getTableHeader().setDefaultRenderer(headerRenderer);
        tableView.getTableHeader().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int clickedColumnIndex = tableView.columnAtPoint(e.getPoint());
                if (clickedColumnIndex == -1) {
                    return;
                }
                if (SwingUtilities.isRightMouseButton(e)) {
                    TableColumnContextMenu contextMenu = new TableColumnContextMenu(selfView, controller,
                            clickedColumnIndex);
                    contextMenu.show(tableView.getTableHeader(), e.getX(), e.getY());
                } else if (SwingUtilities.isLeftMouseButton(e)) {
                    selectColumn(clickedColumnIndex);
                    controller.onColumnSelected(clickedColumnIndex);
                }
            }
        });

        paginationBar = new TablePagination(tableModel);

        add(new TableToolBar(controller, tablePanel), BorderLayout.NORTH);
        add(new JScrollPane(tableView), BorderLayout.CENTER);
        add(paginationBar, BorderLayout.SOUTH);
    }

    public void open(DataTable table) {
        tableModel.open(table);
        paginationBar.reload();
        adjustColumnWidths();
    }

    public void close() {
        tableModel.close();
        paginationBar.reload();
    }

    public void refresh() {
        tableModel.fireTableDataChanged();
        paginationBar.reload();
        updateHeaderIcons();
        adjustColumnWidths();
    }

    public JTable getTableView() {
        return tableView;
    }

    public TableModel getTableModel() {
        return tableModel;
    }

    public void hideColumn(int columnIndex) {
        TableColumnModel columnModel = tableView.getColumnModel();
        if (columnIndex < 0 || columnIndex >= columnModel.getColumnCount()) {
            return;
        }
        tableView.removeColumn(columnModel.getColumn(columnIndex));

        updateHeaderIcons();
    }

    public void renameColumn(int columnIndex, String newName) {
        TableColumnModel columnModel = tableView.getColumnModel();
        if (columnIndex < 0 || columnIndex >= columnModel.getColumnCount()) {
            return;
        }
        columnModel.getColumn(columnIndex).setHeaderValue(newName);
        tableView.getTableHeader().repaint();
    }

    public void showAllColumns() {
        TableColumnModel columnModel = tableView.getColumnModel();
        while (columnModel.getColumnCount() > 0) {
            columnModel.removeColumn(columnModel.getColumn(0));
        }
        tableView.createDefaultColumnsFromModel();

        updateHeaderIcons();
        adjustColumnWidths();
    }

    public void hideEmptyColumns() {
        DataTable table = tableModel.getTable().orElse(null);
        if (table == null) {
            return;
        }

        int viewIndex = 0;
        int tableIndex = 0;
        while (tableIndex < table.getColumnCount() && viewIndex < tableView.getColumnCount()) {
            if (table.getColumnType(tableIndex) == DataType.EMPTY) {
                tableView.removeColumn(tableView.getColumnModel().getColumn(viewIndex));
                tableIndex++;
                continue;
            }

            System.out.println("Column " + table.getColumnName(tableIndex) + " is " + table.getColumnType(tableIndex));

            viewIndex++;
            tableIndex++;
        }

        updateHeaderIcons();
    }

    public void selectColumn(int columnIndex) {
        TableColumnModel columnModel = tableView.getColumnModel();
        if (columnIndex < 0 || columnIndex >= columnModel.getColumnCount()) {
            return;
        }
        tableView.setColumnSelectionAllowed(true);
        tableView.setRowSelectionAllowed(false);
        tableView.clearSelection();
        tableView.setColumnSelectionInterval(columnIndex, columnIndex);
        tableView.selectAll();
        tableView.setColumnSelectionInterval(columnIndex, columnIndex);
    }

    private void updateHeaderIcons() {
        TableDataViewHeader headerRenderer = (TableDataViewHeader) tableView.getTableHeader().getDefaultRenderer();
        DataTable table = tableModel.getTable().orElse(null);
        if (table == null) {
            return;
        }

        Icon filterIcon = new FlatSVGIcon("icons/filter.svg", 14, 14);
        for (int columnIndex = 0; columnIndex < table.getColumnCount(); columnIndex++) {
            boolean hasFilter = table.getFilter(columnIndex).size() > 0;
            if (hasFilter) {
                headerRenderer.setIcon(columnIndex, filterIcon);
            } else {
                headerRenderer.clearIcon(columnIndex);
            }
        }
        tableView.getTableHeader().repaint();
    }

    private void adjustColumnWidths() {
        int sampleRowCount = Math.min(tableView.getRowCount(), 50);
        for (int columnIndex = 0; columnIndex < tableView.getColumnCount(); columnIndex++) {
            int maxWidth = 0;
            TableColumn tableColumn = tableView.getColumnModel().getColumn(columnIndex);

            TableCellRenderer headerRenderer = tableView.getTableHeader().getDefaultRenderer();
            Component headerComponent = headerRenderer.getTableCellRendererComponent(
                    tableView, tableColumn.getHeaderValue(), false, false, -1, columnIndex);
            maxWidth = Math.max(maxWidth, headerComponent.getPreferredSize().width);

            for (int rowIndex = 0; rowIndex < sampleRowCount; rowIndex++) {
                TableCellRenderer cellRenderer = tableView.getCellRenderer(rowIndex, columnIndex);
                Component cellComponent = cellRenderer.getTableCellRendererComponent(
                        tableView, tableView.getValueAt(rowIndex, columnIndex), false, false, rowIndex, columnIndex);
                maxWidth = Math.max(maxWidth, cellComponent.getPreferredSize().width);
            }

            tableColumn.setPreferredWidth(maxWidth + 16);
        }
    }
}