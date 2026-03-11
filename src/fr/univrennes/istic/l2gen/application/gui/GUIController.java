package fr.univrennes.istic.l2gen.application.gui;

import java.io.File;
import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

import com.formdev.flatlaf.util.SystemFileChooser;

import fr.univrennes.istic.l2gen.application.core.CoreController;
import fr.univrennes.istic.l2gen.application.gui.main.LayoutType;
import fr.univrennes.istic.l2gen.application.gui.main.MainView;
import fr.univrennes.istic.l2gen.io.csv.model.CSVColumn;
import fr.univrennes.istic.l2gen.io.csv.model.CSVTable;
import fr.univrennes.istic.l2gen.io.csv.model.CSVType;

public final class GUIController extends CoreController {

    private MainView mainView;

    public GUIController() {
    }

    @Override
    public boolean init() {
        return true;
    }

    public void setMainView(MainView frame) {
        this.mainView = frame;
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
        this.setLoading(true);
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

                this.setLoading(true);
                this.setStatus("Loading from URL: " + uri);

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

    public void refreshView() {

    }

    public void setCurrentView(LayoutType layout) {

    }

    public void setStatus(String status) {
        mainView.getBottomBar().setStatus(status);
    }

    public void setLoading(boolean isLoading) {
        mainView.getBottomBar().setLoading(isLoading);
    }

    public void openDocumentationDialog() {

    }

    public void openAboutDialog() {

    }

    public void onColumnSelected(int colIndex) {
        CSVTable table = this.getTable();
        if (table == null) {
            return;
        }
        CSVType columnType = table.getColumnType(colIndex);

        switch (columnType) {
            case STRING: {
                CSVColumn<String> column = table.getTypedColumn(colIndex, String::valueOf, String.class);
                double minLength = column.getCells().stream()
                        .filter(Optional::isPresent)
                        .mapToInt(opt -> opt.get().length())
                        .min()
                        .orElse(0);
                double maxLength = column.getCells().stream()
                        .filter(Optional::isPresent)
                        .mapToInt(opt -> opt.get().length())
                        .max()
                        .orElse(0);
                this.mainView.getBottomBar().setColumnStats(
                        Optional.of(Double.valueOf(minLength)),
                        Optional.of(Double.valueOf(maxLength)),
                        Optional.empty());
            }
                break;
            case DATE: {
                CSVColumn<LocalDate> column = table.getTypedColumn(colIndex, LocalDate::parse, LocalDate.class);
                double minTime = column.getCells().stream()
                        .filter(Optional::isPresent)
                        .mapToLong(opt -> opt.get().toEpochDay())
                        .min()
                        .orElse(0);
                double maxTime = column.getCells().stream()
                        .filter(Optional::isPresent)
                        .mapToLong(opt -> opt.get().toEpochDay())
                        .max()
                        .orElse(0);
                this.mainView.getBottomBar().setColumnStats(
                        Optional.of(minTime),
                        Optional.of(maxTime),
                        Optional.empty());
            }
                break;
            case NUMERIC: {
                CSVColumn<Double> column = table.getTypedColumn(colIndex, Double::valueOf, Double.class);
                double sum = column.getCells().stream()
                        .filter(Optional::isPresent)
                        .mapToDouble(opt -> opt.get())
                        .sum();
                double min = column.getCells().stream()
                        .filter(Optional::isPresent)
                        .mapToDouble(opt -> opt.get())
                        .min()
                        .orElse(0);
                double max = column.getCells().stream()
                        .filter(Optional::isPresent)
                        .mapToDouble(opt -> opt.get())
                        .max()
                        .orElse(0);
                this.mainView.getBottomBar().setColumnStats(
                        Optional.of(sum),
                        Optional.of(min),
                        Optional.of(max));
            }
                break;
            default:
                this.mainView.getBottomBar().clearColumnStats();
                break;
        }

    }

    public void onTableLoad() {
        List<String> names = this.getLoader().getTablesName();
        if (names.size() == 0) {
            JOptionPane.showMessageDialog(mainView, "No tables loaded.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        if (names.size() == 1) {
            CSVTable table = this.getLoader().getTable(names.get(0));
            this.setTable(table);
            return;
        }

        mainView.getTablePanel().refresh();
    }

    public void onTableSelected(String tableName) {
        CSVTable table = this.getLoader().getTable(tableName);
        this.setTable(table);
    }

    public void onTableClosed() {
        mainView.getTablePanel().closeTable();
        mainView.getTablePanel().refresh();
    }

    @Override
    public void setTable(CSVTable table) {
        super.setTable(table);
        mainView.getTablePanel().load(table);

        String tableName = this.getLoader().getName(table);
        mainView.getBottomBar().setTableInfo(tableName, table.getRowCount(), table.getColumnCount());
        mainView.getBottomBar().clearColumnStats();
    }
}