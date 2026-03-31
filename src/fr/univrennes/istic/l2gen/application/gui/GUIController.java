package fr.univrennes.istic.l2gen.application.gui;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.concurrent.ExecutionException;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import com.formdev.flatlaf.util.SystemFileChooser;

import fr.univrennes.istic.l2gen.application.VectorReport;
import fr.univrennes.istic.l2gen.application.core.CoreController;
import fr.univrennes.istic.l2gen.application.core.services.StatisticOp;
import fr.univrennes.istic.l2gen.application.core.services.StatisticService;
import fr.univrennes.istic.l2gen.application.core.services.TableService;
import fr.univrennes.istic.l2gen.application.core.table.DataTable;
import fr.univrennes.istic.l2gen.application.core.filter.Filter;
import fr.univrennes.istic.l2gen.application.gui.dialog.StatisticsDialog;
import fr.univrennes.istic.l2gen.application.gui.main.MainView;

public final class GUIController extends CoreController {

    private MainView mainView;
    private DataTable currentTable;

    public GUIController() {
    }

    @Override
    public void onStart() {
        this.mainView.getTablePanel().refresh();
        setStatus("Ready");
    }

    @Override
    public void onStop() {
        setStatus("Shutting down...");
        if (currentTable != null) {
            currentTable.close();
        }
    }

    public void setMainView(MainView frame) {
        mainView = frame;
    }

    public MainView getMainView() {
        return mainView;
    }

    public void setLoading(boolean isLoading) {
        mainView.getBottomBar().setLoading(isLoading);
    }

    public void setStatus(String status) {
        mainView.getBottomBar().setStatus(status);
    }

    public Optional<DataTable> getTable() {
        return Optional.ofNullable(currentTable);
    }

    public void setTable(DataTable table) {
        currentTable = table;

        SwingUtilities.invokeLater(() -> {
            mainView.getBottomBar().setTableInfo(
                    table.getAlias(),
                    (int) table.getRowCount(),
                    (int) table.getColumnCount());

            setStatus("Openning table " + table.getAlias());
        });
    }

    public void onCloseTable() {
        if (currentTable != null) {
            currentTable.close();
            currentTable = null;
        }

        mainView.getTablePanel().close();
        mainView.getBottomBar().setTableInfo("", 0, 0);
        mainView.getBottomBar().clearColumnStats();
    }

    public void onColumnSelected(int columnIndex) {
        if (currentTable != null) {
            mainView.getBottomBar().clearColumnStats();

            setLoading(true);
            new SwingWorker<List<Optional<String>>, Void>() {
                @Override
                protected List<Optional<String>> doInBackground() throws Exception {
                    Optional<String> min = StatisticService.computeBase(currentTable, columnIndex,
                            StatisticOp.MIN);
                    Optional<String> max = StatisticService.computeBase(currentTable, columnIndex,
                            StatisticOp.MAX);
                    Optional<String> avg = StatisticService.computeBase(currentTable, columnIndex,
                            StatisticOp.AVG);
                    Optional<String> sum = StatisticService.computeBase(currentTable, columnIndex,
                            StatisticOp.SUM);

                    return List.of(min, max, avg, sum);
                }

                @Override
                protected void done() {
                    try {
                        Optional<String> min = get().get(0);
                        Optional<String> max = get().get(1);
                        Optional<String> avg = get().get(2);
                        Optional<String> sum = get().get(3);

                        SwingUtilities.invokeLater(() -> mainView.getBottomBar().setColumnStats(min, max, avg, sum));

                    } catch (Exception e) {
                        onOpenExceptionDialog(e);
                    } finally {
                        setLoading(false);
                    }
                }
            }.execute();

        }
    }

    public void onColumnSortRequested(int columnIndex, boolean ascending) {
        if (currentTable != null) {
            currentTable.clearAllFilters();
            currentTable.addFilter(Filter.sort(columnIndex, ascending));
            mainView.getTablePanel().refresh();
        }
    }

    public void onFilterSearchRequested(int columnIndex, String searchTerm) {
        if (currentTable != null) {
            currentTable.addFilter(Filter.search(columnIndex, searchTerm));
            mainView.getTablePanel().refresh();
        }
    }

    public void onFilterTopNRequested(int columnIndex, int n, boolean top) {
        if (currentTable != null) {
            if (top) {
                currentTable.addFilter(Filter.topN(columnIndex, n));
            } else {
                currentTable.addFilter(Filter.bottomN(columnIndex, n));
            }
            mainView.getTablePanel().refresh();
        }
    }

    public void onFilterByRangeRequested(int columnIndex, double minValue, double maxValue) {
        if (currentTable != null) {
            currentTable.addFilter(Filter.byRange(columnIndex, minValue, maxValue));
            mainView.getTablePanel().refresh();
        }
    }

    public void onFilterEmptyRequested(int columnIndex, boolean showEmpty) {
        if (currentTable != null) {
            if (showEmpty) {
                currentTable.addFilter(Filter.showEmpty(columnIndex));
            } else {
                currentTable.addFilter(Filter.hideEmpty(columnIndex));
            }
            mainView.getTablePanel().refresh();
        }
    }

    public void onFilterCleared(int columnIndex) {
        if (currentTable != null) {
            currentTable.clearFilters(columnIndex);
            mainView.getTablePanel().refresh();
        }
    }

    public void onFilterReset() {
        if (currentTable != null) {
            currentTable.clearAllFilters();
            mainView.getTablePanel().refresh();
        }
    }

    public void onOpenExceptionDialog(Exception e) {
        if (VectorReport.DEBUG_MODE) {
            e.printStackTrace();
        }

        Throwable rootCause = e.getCause() != null ? e.getCause() : e;
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(
                    mainView,
                    rootCause.getClass().getSimpleName() + ": " + rootCause.getMessage(),
                    "Processing Error",
                    JOptionPane.ERROR_MESSAGE);
        });
    }

    public void onOpenFilterDialog() {
    }

    public void onOpenFileDialog() {
        SystemFileChooser chooser = new SystemFileChooser();
        chooser.setMultiSelectionEnabled(true);
        chooser.setFileSelectionMode(SystemFileChooser.FILES_ONLY);

        if (chooser.showOpenDialog(this.getMainView()) != SystemFileChooser.APPROVE_OPTION) {
            return;
        }

        File[] files = chooser.getSelectedFiles();

        setLoading(true);
        setStatus("Loading " + files.length + " files...");

        new SwingWorker<List<DataTable>, Void>() {
            @Override
            protected List<DataTable> doInBackground() throws Exception {
                List<DataTable> loaded = new ArrayList<>();

                long startTime = System.currentTimeMillis();
                for (File file : files) {
                    List<DataTable> loadedFiles = TableService.load(file);
                    if (loadedFiles.isEmpty()) {
                        continue;
                    }

                    TableService.addRecent(file);
                    loaded.addAll(loadedFiles);
                }
                long endTime = System.currentTimeMillis();

                setStatus("Loaded " + loaded.size() + "/" + files.length + " files in " + (endTime - startTime)
                        + " ms");
                return loaded;
            }

            @Override
            protected void done() {
                try {
                    List<DataTable> loaded = get();
                    SwingUtilities.invokeLater(() -> {
                        if (loaded.size() == 1) {
                            mainView.getTablePanel().open(loaded.get(0));
                        } else {
                            getMainView().getTablePanel().refresh();
                        }
                    });
                } catch (Exception e) {
                    onOpenExceptionDialog(e);
                } finally {
                    setLoading(false);
                }
            }
        }.execute();
    }

    public void onOpenUrlDialog() {
        String input = JOptionPane.showInputDialog(mainView, "URL:");
        if (input == null || input.isBlank()) {
            return;
        }

        URI uri;
        try {
            uri = URI.create(input.trim());
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(mainView, "Invalid URL.", "Invalid URL",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        setLoading(true);
        setStatus("Loading " + input + " ...");
        new SwingWorker<List<DataTable>, Void>() {
            @Override
            protected List<DataTable> doInBackground() throws Exception {

                long startTime = System.currentTimeMillis();
                List<DataTable> loaded = TableService.load(uri);
                long endTime = System.currentTimeMillis();

                setStatus("Loaded " + loaded.size() + " files in " + (endTime - startTime)
                        + " ms");
                return loaded;
            }

            @Override
            protected void done() {
                try {
                    List<DataTable> loaded = get();
                    SwingUtilities.invokeLater(() -> {
                        if (loaded.size() == 1) {
                            mainView.getTablePanel().open(loaded.get(0));
                        } else {
                            getMainView().getTablePanel().refresh();
                        }
                    });
                } catch (Exception e) {
                    onOpenExceptionDialog(e);
                } finally {
                    setLoading(false);
                }
            }
        }.execute();
    }

    public void onOpenDocDialog() {
        try {
            if (!Desktop.isDesktopSupported()) {
                JOptionPane.showMessageDialog(mainView, "Documentation is not available on this system.",
                        "Documentation", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            File docs = new File("docs/DOCUMENTATION.md");
            if (!docs.exists()) {
                JOptionPane.showMessageDialog(mainView, "Documentation file not found.", "Documentation",
                        JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            Desktop.getDesktop().open(docs);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(mainView, "Unable to open documentation.", "Documentation",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public void onOpenAboutDialog() {
        StringBuilder sb = new StringBuilder();
        sb.append("VectorReport - Generate SVG charts from CSV files.\n");
        sb.append("Version: 1.0.0\n");
        sb.append("Developed by:\n");
        sb.append(" - Jules Garcia (@jules1univ)\n");
        sb.append(" - Paul Gallon (@MarcoPaulot)\n");
        sb.append(" - Elouan Barbier (@Marsu2)\n");
        sb.append(" - Noé Berthelier (@nberthelier)\n");
        sb.append(" - Briac Boitel (@bboitel)\n");
        sb.append(" - Kerem Eylem (@Keylem)\n");
        sb.append(" - Basile Guemene (@Astala-Boom)\n");
        sb.append("\n");
        sb.append("This software is licensed under the MIT License.\n");

        JOptionPane.showMessageDialog(mainView, sb.toString(), "About VectorReport", JOptionPane.INFORMATION_MESSAGE);
    }

    public void onComputeSummaryRequested(int columnIndex) {
        String summary = StatisticService.computeSummary(currentTable, columnIndex);
        StatisticsDialog dialog = new StatisticsDialog(mainView,
                "Summary for " + currentTable.getColumnName(columnIndex), summary);
        dialog.setVisible(true);
    }

    public void onComputeNullRateRequested(int columnIndex) {
        OptionalDouble nullRateOpt = StatisticService.computeNullRate(currentTable, columnIndex);
        String nullRateStr = nullRateOpt.isPresent() ? String.format("%.2f%%", nullRateOpt.getAsDouble() * 100) : "N/A";
        StatisticsDialog dialog = new StatisticsDialog(mainView,
                "Null Rate for " + currentTable.getColumnName(columnIndex), "Null Rate: " + nullRateStr);
        dialog.setVisible(true);
    }

    public void onComputeCardinalityRatioRequested(int columnIndex) {
        OptionalDouble cardinalityRatioOpt = StatisticService.computeCardinalityRatio(currentTable, columnIndex);
        String cardinalityRatioStr = cardinalityRatioOpt.isPresent()
                ? String.format("%.2f%%", cardinalityRatioOpt.getAsDouble() * 100)
                : "N/A";
        StatisticsDialog dialog = new StatisticsDialog(mainView,
                "Cardinality Ratio for " + currentTable.getColumnName(columnIndex),
                "Cardinality Ratio: " + cardinalityRatioStr);
        dialog.setVisible(true);
    }

    public void onComputeInterquartileRangeRequested(int columnIndex) {
        OptionalDouble iqrOpt = StatisticService.computeInterquartileRange(currentTable, columnIndex);
        String iqrStr = iqrOpt.isPresent() ? String.format("%.4f", iqrOpt.getAsDouble()) : "N/A";
        StatisticsDialog dialog = new StatisticsDialog(mainView,
                "Interquartile Range for " + currentTable.getColumnName(columnIndex),
                "Interquartile Range: " + iqrStr);
        dialog.setVisible(true);
    }

    public void onComputeSkewnessRequested(int columnIndex) {
        OptionalDouble skewnessOpt = StatisticService.computeSkewness(currentTable, columnIndex);
        String skewnessStr = skewnessOpt.isPresent() ? String.format("%.4f", skewnessOpt.getAsDouble()) : "N/A";
        StatisticsDialog dialog = new StatisticsDialog(mainView,
                "Skewness for " + currentTable.getColumnName(columnIndex),
                "Skewness: " + skewnessStr);
        dialog.setVisible(true);
    }

    public void onComputeCoefficientOfVariationRequested(int columnIndex) {
        OptionalDouble coefVarOpt = StatisticService.computeCoefficientOfVariation(currentTable, columnIndex);
        String coefVarStr = coefVarOpt.isPresent() ? String.format("%.4f", coefVarOpt.getAsDouble()) : "N/A";
        StatisticsDialog dialog = new StatisticsDialog(mainView,
                "Coefficient of Variation for " + currentTable.getColumnName(columnIndex),
                "Coefficient of Variation: " + coefVarStr);
        dialog.setVisible(true);
    }

    public void onComputeCorrelationRequested(int columnIndex, int targetColumnIndex) {
        OptionalDouble correlationOpt = StatisticService.computeCorrelation(currentTable, columnIndex,
                targetColumnIndex);
        String correlationStr = correlationOpt.isPresent() ? String.format("%.4f", correlationOpt.getAsDouble())
                : "N/A";
        StatisticsDialog dialog = new StatisticsDialog(mainView,
                "Correlation between " + currentTable.getColumnName(columnIndex) + " and "
                        + currentTable.getColumnName(targetColumnIndex),
                "Correlation: " + correlationStr);
        dialog.setVisible(true);

    }
}