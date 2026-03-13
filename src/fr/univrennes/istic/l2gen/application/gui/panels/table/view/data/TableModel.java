package fr.univrennes.istic.l2gen.application.gui.panels.table.view.data;

import fr.univrennes.istic.l2gen.application.gui.panels.table.TablePanel;
import fr.univrennes.istic.l2gen.io.csv.model.CSVTable;

import javax.swing.table.AbstractTableModel;

public final class TableModel extends AbstractTableModel {

    private static final int DEFAULT_PAGE_SIZE = 1000;

    private final TablePanel tablePanel;

    private CSVTable source;
    private String[] columnNames;

    private int pageIndex;
    private int pageSize;
    private int columnCount;

    public TableModel(TablePanel tablePanel) {
        this.pageSize = DEFAULT_PAGE_SIZE;
        this.pageIndex = 0;
        this.tablePanel = tablePanel;
    }

    public void load(CSVTable data) {
        this.source = data;
        this.pageIndex = 0;
        this.columnCount = data.getColumnCount();
        this.columnNames = buildColumnNames(data);
        fireTableStructureChanged();
    }

    public void goToPage(int newPageIndex) {
        int totalPages = getTotalPages();
        if (newPageIndex < 0 || newPageIndex >= totalPages) {
            return;
        }
        this.pageIndex = newPageIndex;
        fireTableDataChanged();
        tablePanel.adjustColumnWidths();
    }

    public void nextPage() {
        goToPage(pageIndex + 1);
    }

    public void previousPage() {
        goToPage(pageIndex - 1);
    }

    int getPageIndex() {
        return pageIndex;
    }

    int getTotalPages() {
        if (source == null || source.getRowCount() == 0) {
            return 1;
        }
        return (int) Math.ceil((double) source.getRowCount() / pageSize);
    }

    int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int newPageSize) {
        this.pageSize = newPageSize;
        this.pageIndex = 0;
        fireTableDataChanged();
    }

    int getTotalRowCount() {
        return source == null ? 0 : source.getRowCount();
    }

    private int getPageStartRow() {
        return pageIndex * pageSize;
    }

    private int getPageEndRow() {
        if (source == null) {
            return 0;
        }
        return Math.min(getPageStartRow() + pageSize, source.getRowCount());
    }

    private static String[] buildColumnNames(CSVTable data) {
        int colCount = data.getColumnCount();
        String[] names = new String[colCount];
        if (data.getHeader().isPresent()) {
            String[] headerCells = data.getHeader().get().getCells();
            for (int i = 0; i < colCount; i++) {
                String cell = (i < headerCells.length) ? headerCells[i] : null;
                names[i] = (cell == null) ? "Column " + (i + 1) : cell;
            }
        } else {
            for (int i = 0; i < colCount; i++) {
                names[i] = "Column " + (i + 1);
            }
        }
        return names;
    }

    @Override
    public int getRowCount() {
        return getPageEndRow() - getPageStartRow();
    }

    @Override
    public int getColumnCount() {
        return columnCount;
    }

    @Override
    public String getColumnName(int columnIndex) {
        if (columnNames == null || columnIndex >= columnNames.length) {
            return "Column " + (columnIndex + 1);
        }
        return columnNames[columnIndex];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (source == null) {
            return null;
        }
        int absoluteRowIndex = getPageStartRow() + rowIndex;
        return source.getColumn(columnIndex).getRawCell(absoluteRowIndex);
    }

    @Override
    public boolean isCellEditable(int row, int col) {
        return false;
    }
}