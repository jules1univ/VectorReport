package fr.univrennes.istic.l2gen.application.gui.panels.table.view.data;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import fr.univrennes.istic.l2gen.application.gui.GUIController;
import fr.univrennes.istic.l2gen.io.csv.model.CSVTable;

import java.awt.FlowLayout;

public final class TablePagination extends JPanel {

    private static final int[] PAGE_SIZE_OPTIONS = { 500, 1000, 5000, 10000 };

    private final TableModel tableModel;
    private final GUIController controller;

    private final JLabel pageInfoLabel;
    private final JButton firstPageButton;
    private final JButton previousPageButton;
    private final JButton nextPageButton;
    private final JButton lastPageButton;
    private final JComboBox<String> pageSizeComboBox;

    public TablePagination(TableModel tableModel, GUIController controller) {
        super(new FlowLayout(FlowLayout.CENTER, 8, 4));
        this.tableModel = tableModel;
        this.controller = controller;

        firstPageButton = new JButton("first");
        firstPageButton.addActionListener(e -> {
            tableModel.goToPage(0);
            refresh();
        });

        previousPageButton = new JButton("previous");
        previousPageButton.addActionListener(e -> {
            tableModel.previousPage();
            refresh();
        });

        pageInfoLabel = new JLabel();

        nextPageButton = new JButton("next");
        nextPageButton.addActionListener(e -> {
            tableModel.nextPage();
            refresh();
        });

        lastPageButton = new JButton("last");
        lastPageButton.addActionListener(e -> {
            tableModel.goToPage(tableModel.getTotalPages() - 1);
            refresh();
        });

        String[] pageSizeLabels = new String[PAGE_SIZE_OPTIONS.length];
        for (int i = 0; i < PAGE_SIZE_OPTIONS.length; i++) {
            pageSizeLabels[i] = PAGE_SIZE_OPTIONS[i] + " rows/page";
        }

        pageSizeComboBox = new JComboBox<>(pageSizeLabels);
        pageSizeComboBox.setSelectedIndex(1);
        pageSizeComboBox.addActionListener(e -> {
            int selectedPageSize = PAGE_SIZE_OPTIONS[pageSizeComboBox.getSelectedIndex()];
            tableModel.setPageSize(selectedPageSize);
            refresh();
        });
        add(pageSizeComboBox);

        add(firstPageButton);
        add(previousPageButton);
        add(pageInfoLabel);
        add(nextPageButton);
        add(lastPageButton);

        refresh();
    }

    public void refresh() {
        int currentPage = tableModel.getPageIndex() + 1;
        int totalPages = tableModel.getTotalPages();

        pageInfoLabel.setText("Page " + currentPage + " / " + totalPages);
        firstPageButton.setEnabled(currentPage > 1);
        previousPageButton.setEnabled(currentPage > 1);
        nextPageButton.setEnabled(currentPage < totalPages);
        lastPageButton.setEnabled(currentPage < totalPages);

        boolean showCombobox = this.controller.getTable().map(CSVTable::getRowCount).orElse(0) >= PAGE_SIZE_OPTIONS[0];
        pageSizeComboBox.setVisible(showCombobox);
    }
}