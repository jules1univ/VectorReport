package fr.univrennes.istic.l2gen.application.gui.panels;

import fr.univrennes.istic.l2gen.application.gui.GUIController;
import fr.univrennes.istic.l2gen.io.csv.model.CSVRow;
import fr.univrennes.istic.l2gen.io.csv.model.CSVTable;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.GridBagLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Optional;

public final class TablePanel extends JPanel {

    private final GUIController controller;

    private final JPanel emptyPanel;
    private final JPanel listPanel;
    private final DefaultTableModel listModel; // backing model for the table-list view
    private final JTable tableList; // shows name / rows / columns

    private final JPanel tableViewPanel;
    private final DefaultTableModel tableModel;
    private final JTable table;
    private final TableRowSorter<DefaultTableModel> rowSorter;

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

        tableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        table = new JTable(tableModel);
        table.setFillsViewportHeight(true);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        rowSorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(rowSorter);

        table.getTableHeader().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int columnIndex = table.columnAtPoint(e.getPoint());
                if (columnIndex != -1) {
                    controller.onColumnSelected(columnIndex);
                }
            }
        });

        tableViewPanel = buildTableViewPanel();
        add(tableViewPanel, CARD_TABLE);

        showEmpty();
    }

    public void refresh() {
        listModel.setRowCount(0);
        for (String name : controller.getLoader().getTablesName()) {
            CSVTable csvTable = controller.getLoader().getTable(name);
            int rows = csvTable.getRows().size();
            int cols = csvTable.getHeader()
                    .map(h -> h.getCells().size())
                    .orElse(0);
            listModel.addRow(new Object[] { name, rows, cols });
        }

        if (listModel.getRowCount() == 0) {
            showEmpty();
        } else {
            showList();
        }
    }

    public void load(CSVTable data) {
        SwingUtilities.invokeLater(() -> {
            tableModel.setRowCount(0);
            tableModel.setColumnCount(0);

            if (data.getHeader().isPresent()) {
                for (Optional<String> col : data.getHeader().get().getCells()) {
                    tableModel.addColumn(
                            col.orElse(CSVTable.DEFAULT_EMPTY_CELL).trim().toUpperCase());
                }
            }
            for (CSVRow row : data.getRows()) {
                tableModel.addRow(row.getCells().stream()
                        .map(cell -> cell.orElse(CSVTable.DEFAULT_EMPTY_CELL))
                        .toArray());
            }

            showTable();
        });
    }

    public void closeTable() {
        tableModel.setRowCount(0);
        tableModel.setColumnCount(0);
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

        JToolBar toolbar = new JToolBar();
        toolbar.setFloatable(false);

        JButton closeBtn = new JButton("Close table");
        closeBtn.addActionListener(e -> {
            controller.onTableClosed();
            closeTable();
        });
        toolbar.add(Box.createHorizontalGlue());
        toolbar.add(closeBtn);

        p.add(toolbar, BorderLayout.NORTH);
        p.add(new JScrollPane(table), BorderLayout.CENTER);

        return p;
    }
}