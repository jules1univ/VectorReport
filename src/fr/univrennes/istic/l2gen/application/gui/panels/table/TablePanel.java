package fr.univrennes.istic.l2gen.application.gui.panels.table;

import fr.univrennes.istic.l2gen.application.VectorReport;
import fr.univrennes.istic.l2gen.application.core.services.FileService;
import fr.univrennes.istic.l2gen.application.core.table.DataTable;
import fr.univrennes.istic.l2gen.application.gui.GUIController;
import fr.univrennes.istic.l2gen.application.gui.panels.table.view.data.TableDataView;
import fr.univrennes.istic.l2gen.application.gui.panels.table.view.empty.EmptyView;
import fr.univrennes.istic.l2gen.application.gui.panels.table.view.list.TableListView;

import javax.swing.JPanel;
import javax.swing.SwingWorker;
import java.awt.CardLayout;
import java.io.File;
import java.io.IOException;
import java.util.List;

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
        controller.setLoading(true);
        new SwingWorker<DataTable, Void>() {
            @Override
            protected DataTable doInBackground() {
                if (FileService.isAlreadyProcessed(file)) {
                    try {
                        return DataTable.of(file);
                    } catch (IOException e) {
                        if (VectorReport.DEBUG_MODE) {
                            e.printStackTrace();
                        }
                        return null;
                    }
                }

                List<DataTable> tables = FileService.process(file);
                return tables.isEmpty() ? null : tables.get(0);
            }

            @Override
            protected void done() {
                try {
                    DataTable table = (DataTable) get();
                    if (table != null) {
                        open(table);
                    }
                } catch (Exception e) {
                    if (VectorReport.DEBUG_MODE) {
                        e.printStackTrace();
                    }
                } finally {
                    controller.setLoading(false);
                }
            }
        }.execute();
        return true;
    }

    public boolean open(DataTable table) {
        controller.setTable(table);
        tableDataView.open(table);
        cardLayout.show(TablePanel.this, TableViewState.TABLE.name());
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