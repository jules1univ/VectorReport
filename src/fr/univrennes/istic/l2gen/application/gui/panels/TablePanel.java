package fr.univrennes.istic.l2gen.application.gui.panels;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import fr.univrennes.istic.l2gen.io.csv.model.CSVRow;
import fr.univrennes.istic.l2gen.io.csv.model.CSVTable;

import java.util.Optional;

public final class TablePanel extends JPanel {

    private final JTable table;
    private final DefaultTableModel tableModel;

    public TablePanel() {
        setLayout(new java.awt.BorderLayout());

        tableModel = new DefaultTableModel();
        table = new JTable(tableModel);
        table.setFillsViewportHeight(true);

        add(new JScrollPane(table));
    }

    public void load(CSVTable data) {
        SwingUtilities.invokeLater(() -> {
            tableModel.setRowCount(0);
            tableModel.setColumnCount(0);

            if (data.header().isPresent()) {
                for (Optional<String> col : data.header().get().cells()) {
                    tableModel.addColumn(col.orElse(CSVTable.DEFAULT_EMPTY_CELL).trim().toUpperCase());
                }
            }
            for (CSVRow row : data.rows()) {
                tableModel.addRow(row.cells().stream()
                        .map(cell -> cell.orElse(CSVTable.DEFAULT_EMPTY_CELL))
                        .toArray());
            }
        });
    }
}