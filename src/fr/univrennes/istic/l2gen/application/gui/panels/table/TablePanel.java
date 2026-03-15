package fr.univrennes.istic.l2gen.application.gui.panels.table;

import fr.univrennes.istic.l2gen.application.gui.GUIController;
import fr.univrennes.istic.l2gen.application.gui.panels.table.view.data.TableDataView;
import fr.univrennes.istic.l2gen.application.gui.panels.table.view.empty.EmptyView;
import fr.univrennes.istic.l2gen.application.gui.panels.table.view.list.TableListView;
import fr.univrennes.istic.l2gen.io.csv.model.CSVTable;

import javax.swing.JPanel;
import javax.swing.SwingWorker;
import java.awt.CardLayout;

public final class TablePanel extends JPanel {

    private static final String CARD_EMPTY = "EMPTY";
    private static final String CARD_LIST = "LIST";
    private static final String CARD_TABLE = "TABLE";

    private final GUIController controller;
    private final CardLayout cardLayout = new CardLayout();

    private final TableListView tableListView;
    private final TableDataView tableDataView;

    public TablePanel(GUIController controller) {
        this.controller = controller;
        setLayout(cardLayout);

        add(new EmptyView(controller), CARD_EMPTY);

        tableListView = new TableListView(controller);
        add(tableListView, CARD_LIST);

        tableDataView = new TableDataView(this, controller);
        add(tableDataView, CARD_TABLE);

        showEmpty();
    }

    public void hideColumn(int columnIndex) {
        tableDataView.hideColumn(columnIndex);
    }

    public void renameColumn(int columnIndex, String newName) {
        tableDataView.renameColumn(columnIndex, newName);
    }

    public void openAdvancedFilterDialog() {
        tableDataView.openAdvancedFilterDialog(controller);
    }

    public void adjustColumnWidths() {
        tableDataView.adjustColumnWidths();
    }

    public void showAllColumns() {
        int totalColumns = controller.getCurrentTable().map(CSVTable::getColumnCount).orElse(0);
        tableDataView.showAllColumns(totalColumns);
    }

    public void refresh() {
        tableListView.refresh(controller);
        if (tableListView.isEmpty()) {
            showEmpty();
        } else {
            showList();
        }
    }

    public void load(CSVTable data) {
        controller.setLoading(true);
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() {
                return null;
            }

            @Override
            protected void done() {
                tableDataView.load(data);
                showTable();
                controller.setLoading(false);
            }
        }.execute();
    }

    public void closeTable() {
        tableDataView.load(new CSVTable());
        if (tableListView.isEmpty()) {
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
}