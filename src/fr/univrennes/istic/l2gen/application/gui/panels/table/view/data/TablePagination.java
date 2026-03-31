package fr.univrennes.istic.l2gen.application.gui.panels.table.view.data;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import fr.univrennes.istic.l2gen.application.core.lang.Lang;

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

        firstPageButton = new JButton(Lang.get("tablepagination.first"));
        firstPageButton.addActionListener(e -> {
            tableModel.goToPage(0);
            reload();
        });

        previousPageButton = new JButton(Lang.get("tablepagination.previous"));
        previousPageButton.addActionListener(e -> {
            tableModel.previousPage();
            reload();
        });

        pageInfoLabel = new JLabel();

        nextPageButton = new JButton(Lang.get("tablepagination.next"));
        nextPageButton.addActionListener(e -> {
            tableModel.nextPage();
            reload();
        });

        lastPageButton = new JButton(Lang.get("tablepagination.last"));
        lastPageButton.addActionListener(e -> {
            tableModel.goToPage(tableModel.getTotalPages() - 1);
            reload();
        });

        String[] pageSizeLabels = new String[PAGE_SIZE_OPTIONS.length];
        for (int i = 0; i < PAGE_SIZE_OPTIONS.length; i++) {
            pageSizeLabels[i] = Lang.get("tablepagination.page_size", PAGE_SIZE_OPTIONS[i]);
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

        pageInfoLabel.setText(Lang.get("tablepagination.page_info", currentPage, totalPages));
        firstPageButton.setEnabled(currentPage > 1);
        previousPageButton.setEnabled(currentPage > 1);
        nextPageButton.setEnabled(currentPage < totalPages);
        lastPageButton.setEnabled(currentPage < totalPages);

        boolean showCombobox = tableModel.getTotalRowCount() >= PAGE_SIZE_OPTIONS[0];
        pageSizeComboBox.setVisible(showCombobox);
    }
}