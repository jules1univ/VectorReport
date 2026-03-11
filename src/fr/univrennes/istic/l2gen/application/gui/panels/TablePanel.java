package fr.univrennes.istic.l2gen.application.gui.panels;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import fr.univrennes.istic.l2gen.application.gui.GUIController;
import fr.univrennes.istic.l2gen.io.csv.model.CSVRow;
import fr.univrennes.istic.l2gen.io.csv.model.CSVTable;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Optional;

public final class TablePanel extends JPanel {

    private final JTable table;
    private final DefaultTableModel tableModel;

    public TablePanel(GUIController controller) {
        setLayout(new BorderLayout());

        tableModel = new DefaultTableModel();
        table = new JTable(tableModel);
        table.setFillsViewportHeight(true);

        table.getTableHeader().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int columnIndex = table.columnAtPoint(e.getPoint());
                if (columnIndex != -1) {
                    controller.onColumnSelected(columnIndex);
                }
            }
        });

        add(new JScrollPane(table));
    }

    public void load(CSVTable data) {
        SwingUtilities.invokeLater(() -> {
            tableModel.setRowCount(0);
            tableModel.setColumnCount(0);

            if (data.header().isPresent()) {
                for (Optional<String> col : data.header().get().getCells()) {
                    tableModel.addColumn(col.orElse(CSVTable.DEFAULT_EMPTY_CELL).trim().toUpperCase());
                }
            }
            for (CSVRow row : data.rows()) {
                tableModel.addRow(row.getCells().stream()
                        .map(cell -> cell.orElse(CSVTable.DEFAULT_EMPTY_CELL))
                        .toArray());
            }
        });
    }
}