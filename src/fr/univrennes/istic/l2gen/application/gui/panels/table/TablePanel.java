package fr.univrennes.istic.l2gen.application.gui.panels.table;

import fr.univrennes.istic.l2gen.application.gui.GUIController;
import fr.univrennes.istic.l2gen.application.gui.panels.table.view.data.TableDataView;
import fr.univrennes.istic.l2gen.application.gui.panels.table.view.empty.EmptyView;
import fr.univrennes.istic.l2gen.application.gui.panels.table.view.list.TableListView;

import javax.swing.JPanel;
import javax.swing.SwingWorker;
import java.awt.CardLayout;
import java.io.File;

public final class TablePanel extends JPanel {
    private final GUIController controller;
    private final CardLayout cardLayout = new CardLayout();

    private final TableListView tableListView;
    private final TableDataView tableDataView;

    public TablePanel(GUIController controller) {
        this.controller = controller;
        setLayout(cardLayout);

        add(new EmptyView(controller), TableViewState.EMPTY.name());

        tableListView = new TableListView(this, controller);
        add(tableListView, TableViewState.LIST.name());

        tableDataView = new TableDataView(this, controller);
        add(tableDataView, TableViewState.TABLE.name());

        cardLayout.show(this, TableViewState.EMPTY.name());
    }

    public TableDataView getTable() {
        return tableDataView;
    }

    public boolean open(File file) {
        if (!file.isFile() || !file.canRead() || !file.getName().toLowerCase().endsWith(".parquet")) {
            return false;
        }

        controller.setLoading(true);
        new SwingWorker<Void, Void>() {

            @Override
            protected Void doInBackground() {
                controller.onOpenParquetTable(file);
                return null;
            }

            @Override
            protected void done() {
                cardLayout.show(TablePanel.this, TableViewState.TABLE.name());
                controller.setLoading(false);
            }
        }.execute();
        return true;
    }

    public void close() {
        tableDataView.close();
        if (tableListView.isEmpty()) {
            cardLayout.show(this, TableViewState.EMPTY.name());
        } else {
            cardLayout.show(this, TableViewState.LIST.name());
        }
    }

    public void refresh() {
        tableListView.refresh();
        if (tableListView.isEmpty()) {
            cardLayout.show(this, TableViewState.EMPTY.name());
        } else {
            cardLayout.show(this, TableViewState.LIST.name());
        }
    }

}