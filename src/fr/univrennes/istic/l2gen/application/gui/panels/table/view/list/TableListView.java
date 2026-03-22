package fr.univrennes.istic.l2gen.application.gui.panels.table.view.list;

import fr.univrennes.istic.l2gen.application.core.config.Config;
import fr.univrennes.istic.l2gen.application.core.table.DataTableInfo;
import fr.univrennes.istic.l2gen.application.gui.GUIController;
import fr.univrennes.istic.l2gen.application.gui.panels.table.TablePanel;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

public final class TableListView extends JPanel {

    private final DefaultTableModel listModel;

    public TableListView(TablePanel panel, GUIController controller) {
        super(new BorderLayout());
        this.listModel = new DefaultTableModel(new String[] { "Path", "Alias", "Rows", "Columns", "Size" }, 0) {
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
                        File path = (File) listModel.getValueAt(selectedRow, 0);
                        panel.open(path);
                    }
                }
            }
        });

        add(new JScrollPane(tableList), BorderLayout.CENTER);
    }

    public void refresh() {
        listModel.setRowCount(0);
        for (DataTableInfo info : Config.getInstance().getRecentTables()) {
            listModel.addRow(new Object[] {
                    info.getSource(),
                    info.getAlias(),
                    info.getRowCount(),
                    info.getColumnCount(),
                    info.getSize()
            });
        }
    }

    public boolean isEmpty() {
        return listModel.getRowCount() == 0;
    }
}