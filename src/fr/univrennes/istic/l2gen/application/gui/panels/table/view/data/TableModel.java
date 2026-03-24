package fr.univrennes.istic.l2gen.application.gui.panels.table.view.data;

import fr.univrennes.istic.l2gen.application.core.table.DataTable;

import java.lang.foreign.Linker.Option;
import java.util.Optional;

import javax.swing.table.AbstractTableModel;

public final class TableModel extends AbstractTableModel {

    private static final int DEFAULT_PAGE_SIZE = 1000;

    private final TableDataView tableView;

    private DataTable table;
    private int pageSize;
    private int pageIndex;

    public TableModel(TableDataView tableView) {
        this.tableView = tableView;
        this.pageSize = DEFAULT_PAGE_SIZE;
        this.pageIndex = 0;
    }

    public void open(DataTable dataTable) {
        this.table = dataTable;
        this.pageIndex = 0;
        prefetchCurrentPage();
        fireTableStructureChanged();
    }

    public void close() {
        this.table = null;
        this.pageIndex = 0;
        fireTableStructureChanged();
    }

    public Optional<DataTable> getTable() {
        return Optional.ofNullable(table);
    }

    public void goToPage(int newPageIndex) {
        if (table == null) {
            return;
        }
        int totalPages = getTotalPages();
        if (newPageIndex < 0 || newPageIndex >= totalPages) {
            return;
        }
        this.pageIndex = newPageIndex;
        prefetchCurrentPage();
        fireTableDataChanged();
        tableView.refresh();
    }

    public void nextPage() {
        goToPage(pageIndex + 1);
    }

    public void previousPage() {
        goToPage(pageIndex - 1);
    }

    public int getPageIndex() {
        return pageIndex;
    }

    public int getTotalPages() {
        if (table == null) {
            return 0;
        }
        return (int) Math.ceil((double) table.getRowCount() / pageSize);
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int newPageSize) {
        if (newPageSize <= 0) {
            return;
        }
        this.pageSize = newPageSize;
        this.pageIndex = 0;
        prefetchCurrentPage();
        fireTableDataChanged();
    }

    public int getTotalRowCount() {
        if (table == null) {
            return 0;
        }
        return (int) table.getRowCount();
    }

    private int getPageStartRow() {
        return pageIndex * pageSize;
    }

    private int getPageEndRow() {
        if (table == null) {
            return 0;
        }
        return Math.min(getPageStartRow() + pageSize, (int) table.getRowCount());
    }

    private void prefetchCurrentPage() {
        if (table == null) {
            return;
        }
        table.prefetch(getPageStartRow(), getPageEndRow());
    }

    @Override
    public int getRowCount() {
        if (table == null) {
            return 0;
        }
        return getPageEndRow() - getPageStartRow();
    }

    @Override
    public int getColumnCount() {
        if (table == null) {
            return 0;
        }
        return (int) table.getColumnCount();
    }

    @Override
    public String getColumnName(int columnIndex) {
        if (table == null) {
            return "";
        }
        return table.getColumnName(columnIndex);
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (table == null) {
            return null;
        }
        return table.getValueAt(getPageStartRow() + rowIndex, columnIndex);
    }

    @Override
    public boolean isCellEditable(int row, int col) {
        return false;
    }
}