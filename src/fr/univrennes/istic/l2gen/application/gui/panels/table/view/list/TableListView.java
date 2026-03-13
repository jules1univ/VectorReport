package fr.univrennes.istic.l2gen.application.gui.panels.table.view.list;

import fr.univrennes.istic.l2gen.application.gui.GUIController;
import fr.univrennes.istic.l2gen.io.csv.model.CSVTable;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public final class TableListView extends JPanel {

    private final DefaultTableModel listModel;

    public TableListView(GUIController controller) {
        super(new BorderLayout());

        listModel = new DefaultTableModel(new String[] { "Name", "Rows", "Columns" }, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        JTable tableList = new JTable(listModel);
        tableList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tableList.setFillsViewportHeight(true);

        tableList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int selectedRow = tableList.getSelectedRow();
                    if (selectedRow != -1) {
                        String tableName = (String) listModel.getValueAt(selectedRow, 0);
                        controller.onTableSelected(tableName);
                    }
                }
            }
        });

        add(new JScrollPane(tableList), BorderLayout.CENTER);
    }

    void refresh(GUIController controller) {
        listModel.setRowCount(0);
        for (String tableName : controller.getLoader().getTablesName()) {
            CSVTable csvTable = controller.getLoader().getTable(tableName);
            listModel.addRow(new Object[] { tableName, csvTable.getRowCount(), csvTable.getColumnCount() });
        }
    }

    boolean isEmpty() {
        return listModel.getRowCount() == 0;
    }
}