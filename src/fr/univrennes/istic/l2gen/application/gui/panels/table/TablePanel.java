package fr.univrennes.istic.l2gen.application.gui.panels.table;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingWorker;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import fr.univrennes.istic.l2gen.application.gui.GUIController;
import fr.univrennes.istic.l2gen.io.csv.model.CSVTable;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.GridBagLayout;
import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public final class TablePanel extends JPanel {

    private final GUIController controller;

    private final JPanel emptyPanel;
    private final JPanel listPanel;
    private final DefaultTableModel listModel;
    private final JTable tableList;

    private final JPanel tableViewPanel;
    private final TableModel tableModel;
    private final JTable table;
    private TablePagination paginationBar;

    private static final String CARD_EMPTY = "EMPTY";
    private static final String CARD_LIST = "LIST";
    private static final String CARD_TABLE = "TABLE";

    private final CardLayout cardLayout = new CardLayout();

    public TablePanel(GUIController controller) {
        this.controller = controller;
        setLayout(cardLayout);

        emptyPanel = buildEmptyPanel();
        add(emptyPanel, CARD_EMPTY);

        listModel = new DefaultTableModel(new String[] { "Name", "Rows", "Columns" }, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        tableList = new JTable(listModel);
        tableList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tableList.setFillsViewportHeight(true);
        listPanel = buildListPanel();
        add(listPanel, CARD_LIST);

        tableModel = new TableModel(this);
        table = new JTable(tableModel);
        table.setFillsViewportHeight(true);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.setRowSorter(null);
        table.getTableHeader().setReorderingAllowed(false);

        TablePanel selfPanel = this;
        Component selfComponent = this;
        table.getTableHeader().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int clickedColumnIndex = table.columnAtPoint(e.getPoint());
                if (clickedColumnIndex == -1) {
                    return;
                }

                if (SwingUtilities.isRightMouseButton(e)) {
                    TableContextMenu contextMenu = new TableContextMenu(controller, selfPanel, clickedColumnIndex,
                            selfComponent);
                    contextMenu.show(table.getTableHeader(), e.getX(), e.getY());
                } else if (SwingUtilities.isLeftMouseButton(e)) {
                    selectColumn(clickedColumnIndex);
                    controller.onColumnSelected(clickedColumnIndex);
                }
            }
        });

        tableViewPanel = buildTableViewPanel();
        add(tableViewPanel, CARD_TABLE);

        showEmpty();
    }

    private void selectColumn(int columnIndex) {
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

    void openAdvancedFilterDialog() {
        Window parentWindow = SwingUtilities.getWindowAncestor(this);
        String[] columnNames = buildColumnNamesArray();
        FilterDialog filterDialog = new FilterDialog(parentWindow, columnNames,
                controller);
        filterDialog.setVisible(true);
    }

    private String[] buildColumnNamesArray() {
        int columnCount = table.getColumnCount();
        String[] columnNames = new String[columnCount];
        for (int i = 0; i < columnCount; i++) {
            columnNames[i] = table.getColumnName(i);
        }
        return columnNames;
    }

    public JTable getTable() {
        return table;
    }

    public void hideColumn(int columnIndex) {
        TableColumnModel columnModel = table.getColumnModel();
        if (columnIndex < 0 || columnIndex >= columnModel.getColumnCount()) {
            return;
        }
        TableColumn column = columnModel.getColumn(columnIndex);
        table.removeColumn(column);
    }

    public void renameColumn(int columnIndex, String newName) {
        TableColumnModel columnModel = table.getColumnModel();
        if (columnIndex < 0 || columnIndex >= columnModel.getColumnCount()) {
            return;
        }
        TableColumn column = columnModel.getColumn(columnIndex);
        column.setHeaderValue(newName);
        table.getTableHeader().repaint();
    }

    public void refresh() {
        listModel.setRowCount(0);
        for (String name : controller.getLoader().getTablesName()) {
            CSVTable csvTable = controller.getLoader().getTable(name);
            listModel.addRow(new Object[] { name, csvTable.getRowCount(), csvTable.getColumnCount() });
        }

        if (listModel.getRowCount() == 0) {
            showEmpty();
        } else {
            showList();
        }
    }

    public void load(CSVTable data) {
        controller.setLoading(true);
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() {
                return null;
            }

            @Override
            protected void done() {
                tableModel.load(data);
                paginationBar.refresh();
                adjustColumnWidths();
                showTable();
                controller.setLoading(false);
            }
        }.execute();
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

    public void closeTable() {
        tableModel.load(new CSVTable());
        if (listModel.getRowCount() == 0) {
            showEmpty();
        } else {
            showList();
        }
    }

    private void showEmpty() {
        cardLayout.show(this, CARD_EMPTY);
    }

    private void showList() {
        cardLayout.show(this, CARD_LIST);
    }

    private void showTable() {
        cardLayout.show(this, CARD_TABLE);
    }

    private JPanel buildEmptyPanel() {
        JPanel p = new JPanel(new GridBagLayout());
        JButton loadBtn = new JButton("Load a table");
        loadBtn.addActionListener(e -> controller.openFileOrFolder());
        p.add(loadBtn);
        return p;
    }

    private JPanel buildListPanel() {
        JPanel p = new JPanel(new BorderLayout());

        tableList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = tableList.getSelectedRow();
                    if (row != -1) {
                        String name = (String) listModel.getValueAt(row, 0);
                        controller.onTableSelected(name);
                    }
                }
            }
        });

        p.add(new JScrollPane(tableList), BorderLayout.CENTER);
        return p;
    }

    private JPanel buildTableViewPanel() {
        JPanel p = new JPanel(new BorderLayout());
        paginationBar = new TablePagination(tableModel);
        p.add(new TableToolBar(controller, this, this::closeTable), BorderLayout.NORTH);
        p.add(new JScrollPane(table), BorderLayout.CENTER);
        p.add(paginationBar, BorderLayout.SOUTH);
        return p;
    }
}