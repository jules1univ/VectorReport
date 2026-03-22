package fr.univrennes.istic.l2gen.application.gui.panels.table.view.data;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import java.awt.FlowLayout;

public final class TablePagination extends JPanel {

    private static final int[] PAGE_SIZE_OPTIONS = { 500, 1000, 5000, 10000 };

    private final TableModel tableModel;

    private final JLabel pageInfoLabel;
    private final JButton firstPageButton;
    private final JButton previousPageButton;
    private final JButton nextPageButton;
    private final JButton lastPageButton;
    private final JComboBox<String> pageSizeComboBox;

    public TablePagination(TableModel tableModel) {
        super(new FlowLayout(FlowLayout.CENTER, 8, 4));
        this.tableModel = tableModel;

        firstPageButton = new JButton("first");
        firstPageButton.addActionListener(e -> {
            tableModel.goToPage(0);
            reload();
        });

        previousPageButton = new JButton("previous");
        previousPageButton.addActionListener(e -> {
            tableModel.previousPage();
            reload();
        });

        pageInfoLabel = new JLabel();

        nextPageButton = new JButton("next");
        nextPageButton.addActionListener(e -> {
            tableModel.nextPage();
            reload();
        });

        lastPageButton = new JButton("last");
        lastPageButton.addActionListener(e -> {
            tableModel.goToPage(tableModel.getTotalPages() - 1);
            reload();
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
            reload();
        });
        add(pageSizeComboBox);

        add(firstPageButton);
        add(previousPageButton);
        add(pageInfoLabel);
        add(nextPageButton);
        add(lastPageButton);

        reload();
    }

    public void reload() {
        int currentPage = tableModel.getPageIndex() + 1;
        int totalPages = tableModel.getTotalPages();

        pageInfoLabel.setText("Page " + currentPage + " / " + totalPages);
        firstPageButton.setEnabled(currentPage > 1);
        previousPageButton.setEnabled(currentPage > 1);
        nextPageButton.setEnabled(currentPage < totalPages);
        lastPageButton.setEnabled(currentPage < totalPages);

        boolean showCombobox = tableModel.getTotalRowCount() >= PAGE_SIZE_OPTIONS[0];
        pageSizeComboBox.setVisible(showCombobox);
    }
}