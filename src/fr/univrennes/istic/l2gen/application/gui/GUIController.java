package fr.univrennes.istic.l2gen.application.gui;

import java.io.File;
import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

import com.formdev.flatlaf.util.SystemFileChooser;

import fr.univrennes.istic.l2gen.application.core.CoreController;
import fr.univrennes.istic.l2gen.application.core.services.filter.FilterCondition;
import fr.univrennes.istic.l2gen.application.core.services.stats.CorrelationType;
import fr.univrennes.istic.l2gen.application.gui.main.MainView;
import fr.univrennes.istic.l2gen.io.csv.model.CSVTable;
import fr.univrennes.istic.l2gen.io.csv.model.CSVType;

public final class GUIController extends CoreController {

    private MainView mainView;

    public GUIController() {
    }

    public void setMainView(MainView frame) {
        mainView = frame;
    }

    public MainView getMainView() {
        return mainView;
    }

    public void openFileOrFolder() {
        SystemFileChooser fileChooser = new SystemFileChooser();
        fileChooser.setMultiSelectionEnabled(true);

        if (fileChooser.showOpenDialog(mainView) != SystemFileChooser.APPROVE_OPTION) {
            return;
        }

        File[] files = fileChooser.getSelectedFiles();
        setLoading(true);
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                int totalImported = 0;
                long startGlobalTime = System.currentTimeMillis();
                for (File file : files) {
                    long startTime = System.currentTimeMillis();
                    if (file.isDirectory()) {
                        setStatus("Loading folder: " + file.getName());
                        totalImported += getLoader().loadFolder(file, true).size();
                    } else {
                        setStatus("Loading file: " + file.getName());
                        totalImported += getLoader().loadFile(file, true).size();
                    }
                    long endTime = System.currentTimeMillis();
                    setStatus(
                            String.format("Loaded %s in %.2f seconds", file.getName(), (endTime - startTime) / 1000.0));
                }
                long endGlobalTime = System.currentTimeMillis();
                setStatus(String.format("Loaded %d tables in %.2f seconds", totalImported,
                        (endGlobalTime - startGlobalTime) / 1000.0));
                return null;
            }

            @Override
            protected void done() {
                setLoading(false);
                onTableLoad();
            }

        }.execute();
    }

    public void openUrl() {
        String url = JOptionPane.showInputDialog(mainView, "URL", "Load from url",
                JOptionPane.PLAIN_MESSAGE);
        if (url != null && !url.trim().isEmpty()) {
            try {
                URI uri = URI.create(url);

                setLoading(true);
                setStatus("Loading from URL: " + uri);

                new SwingWorker<Void, Void>() {
                    @Override
                    protected Void doInBackground() throws Exception {
                        long startTime = System.currentTimeMillis();
                        int tablesLoaded = getLoader().loadUrl(uri, true).size();
                        long endTime = System.currentTimeMillis();

                        setStatus(String.format("Loaded %d tables from URL in %.2f seconds", tablesLoaded,
                                (endTime - startTime) / 1000.0));
                        return null;
                    }

                    @Override
                    protected void done() {
                        setLoading(false);
                        onTableLoad();
                    }

                }.execute();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(mainView, "Failed to load from URL: " + e.getMessage(), "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void openDocumentationDialog() {

    }

    public void openAboutDialog() {

    }

    public void onFilterByCategoryRequested(int columnIndex, boolean percentage) {
        if (getTable().isEmpty()) {
            return;
        }
        CSVTable table = getTable().get();

        setLoading(true);

        new SwingWorker<CSVTable, Void>() {
            @Override
            protected CSVTable doInBackground() throws Exception {
                return getFilter().filterByCategory(table, columnIndex, percentage);
            }

            @Override
            protected void done() {
                try {
                    setTable(get());
                } catch (InterruptedException | ExecutionException exception) {
                    Thread.currentThread().interrupt();
                } finally {
                    setLoading(false);
                }
            }
        }.execute();
    }

    public void onMultiColumnCorrelationRequested(int columnIndex, List<Integer> targetColumnIndices) {
        if (getTable().isEmpty()) {
            return;
        }
        CSVTable table = getTable().get();
    }

    public void onCorrelationRequested(int columnIndex, int targetColIndex, CorrelationType type) {
        if (getTable().isEmpty()) {
            return;
        }
        CSVTable table = getTable().get();
        setLoading(true);
        new SwingWorker<Double, Void>() {
            @Override
            protected Double doInBackground() throws Exception {
                return getStats().getCorrelation(table, columnIndex, targetColIndex, type);
            }

            @Override
            protected void done() {
                setLoading(false);

                try {
                    double correlation = get();
                    String message = String.format("Between %s and %s: %.4f",
                            table.getColumnName(columnIndex).orElse("(unknown)").toLowerCase(),
                            table.getColumnName(targetColIndex).orElse("(unknown)").toLowerCase(),
                            correlation);

                    JOptionPane.showMessageDialog(mainView, message,
                            String.format("Correlation (%s)", type.name().toLowerCase()),
                            JOptionPane.INFORMATION_MESSAGE);
                } catch (InterruptedException | ExecutionException exception) {
                    Thread.currentThread().interrupt();
                } finally {
                }
            }
        }.execute();
    }

    public void onValueDistributionRequested(int columnIndex) {

    }

    public void onFilterTopNRequested(int columnIndex, int n, boolean top) {
    }

    public void onFilterByRangeRequested(int columnIndex, double min, double max) {
    }

    public void onFilterEmptyRequested(int columnIndex, boolean showEmpty) {
    }

    public void onTableSaveRequested() {
    }

    public void onFilterRequested(List<FilterCondition> conditions) {
    }

    public void onFilterCleared(int columnIndex) {
    }

    public void onColumnRemoveRequested(int columnIndex) {
        if (getTable().isEmpty()) {
            return;
        }
        CSVTable table = getTable().get();
        CSVTable copy = new CSVTable(table);
        copy.removeColumn(columnIndex);
        setTable(copy);
    }

    public void onColumnTypeChangeRequested(int columnIndex, CSVType newType) {
        if (getTable().isEmpty()) {
            return;
        }
        CSVTable table = getTable().get();
        table.getColumn(columnIndex).setType(newType);
    }

    public void onColumnSortRequested(int columnIndex, boolean ascending) {
        if (getTable().isEmpty()) {
            return;
        }
        CSVTable table = getTable().get();

        setLoading(true);
        new SwingWorker<CSVTable, Void>() {
            @Override
            protected CSVTable doInBackground() {
                return getFilter().sortByColumn(table, columnIndex, ascending);
            }

            @Override
            protected void done() {
                try {
                    setTable(get());
                } catch (InterruptedException | ExecutionException exception) {
                    Thread.currentThread().interrupt();
                } finally {
                    setLoading(false);
                }
            }
        }.execute();
    }

    public void onColumnSelected(int columnIndex) {
        if (getTable().isEmpty()) {
            return;
        }
        CSVTable table = getTable().get();
        setLoading(true);
        new SwingWorker<Void, Void>() {
            private Optional<String> min;
            private Optional<String> max;
            private Optional<String> avg;
            private Optional<String> sum;

            @Override
            protected Void doInBackground() {
                min = getStats().getColumnMin(table, columnIndex);
                max = getStats().getColumnMax(table, columnIndex);
                avg = getStats().getColumnAvg(table, columnIndex);
                sum = getStats().getColumnSum(table, columnIndex);
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                    mainView.getBottomBar().setColumnStats(min, max, avg, sum);
                } catch (InterruptedException | ExecutionException exception) {
                    Thread.currentThread().interrupt();
                } finally {
                    setLoading(false);
                }
            }
        }.execute();
    }

    public void onTableLoad() {
        List<String> names = getLoader().getTablesName();
        if (names.size() == 0) {
            JOptionPane.showMessageDialog(mainView, "No tables loaded.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        if (names.size() == 1) {
            CSVTable table = getLoader().getTable(names.get(0));
            setTable(table);
            return;
        }

        mainView.getTablePanel().refresh();
    }

    public void onTableSelected(String tableName) {
        CSVTable table = getLoader().getTable(tableName);
        setTable(table);
    }

    public void onTableClosed() {
        mainView.getTablePanel().closeTable();
        mainView.getTablePanel().refresh();
    }

    public void setStatus(String status) {
        mainView.getBottomBar().setStatus(status);
    }

    public void setLoading(boolean isLoading) {
        mainView.getBottomBar().setLoading(isLoading);
    }

    @Override
    public void setTable(CSVTable table) {
        super.setTable(table);
        mainView.getTablePanel().load(table);

        String tableName = getLoader().getName(table);
        mainView.getBottomBar().setTableInfo(tableName, table.getRowCount(), table.getColumnCount());
        mainView.getBottomBar().clearColumnStats();
    }
}