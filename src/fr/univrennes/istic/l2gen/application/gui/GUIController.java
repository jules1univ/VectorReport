package fr.univrennes.istic.l2gen.application.gui;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.OptionalDouble;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import com.formdev.flatlaf.util.SystemFileChooser;

import fr.univrennes.istic.l2gen.application.VectorReport;
import fr.univrennes.istic.l2gen.application.core.CoreController;
import fr.univrennes.istic.l2gen.application.core.config.Config;
import fr.univrennes.istic.l2gen.application.core.services.FileService;
import fr.univrennes.istic.l2gen.application.core.services.StatisticOp;
import fr.univrennes.istic.l2gen.application.core.services.StatisticService;
import fr.univrennes.istic.l2gen.application.core.services.TableService;
import fr.univrennes.istic.l2gen.application.core.table.DataTable;
import fr.univrennes.istic.l2gen.application.core.table.DataTableWorkerStatus;
import fr.univrennes.istic.l2gen.application.core.filter.Filter;
import fr.univrennes.istic.l2gen.application.core.lang.Lang;
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
        setStatus(Lang.get("status.ready"));

        ///// REMOVE THIS LATER !!!
        ///// SUPPRIMER CE CODE UNIQUEMENT POUR LE "PROJET DE GEN" PAS POUR APPLICATION
        File targetDir = new File(System.getProperty("user.home"), ".VectorReport");
        if (!targetDir.exists()) {
            targetDir.mkdirs();
        }
        System.out.println("Target dir: " + targetDir.getAbsolutePath());
        File ouputFile = new File(targetDir, "ValeursFoncieres-2024.txt.parquet");
        if (!ouputFile.exists()) {
            TableService.load(
                    URI.create("https://www.data.gouv.fr/api/1/datasets/r/af812b0e-a898-4226-8cc8-5a570b257326"),
                    targetDir);
        }
        mainView.getTablePanel().open(TableService.get(ouputFile));
        /////
        /////

    }

    @Override
    public void onStop() {
        setStatus(Lang.get("status.shutting_down"));
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

            setStatus(Lang.get("status.opening_table", table.getAlias()));
        });
    }

    public void onCloseTable() {
        if (currentTable != null) {
            setStatus(Lang.get("status.closing_table",
                    currentTable != null ? currentTable.getAlias() : Lang.get("error.number_na")));

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
                    Lang.get("error.exception"),
                    JOptionPane.ERROR_MESSAGE);
        });
    }

    public void onOpenFilterDialog() {
    }

    public void onOpenFileDialog() {
        SystemFileChooser fileChooser = new SystemFileChooser();
        fileChooser.setMultiSelectionEnabled(true);
        fileChooser.setFileSelectionMode(SystemFileChooser.FILES_ONLY);

        if (fileChooser.showOpenDialog(this.getMainView()) != SystemFileChooser.APPROVE_OPTION) {
            return;
        }

        File[] selectedFiles = fileChooser.getSelectedFiles();
        boolean hasZip = false;
        for (File file : selectedFiles) {
            if (FileService.getExtension(file).equalsIgnoreCase("zip")) {
                hasZip = true;
                break;
            }
        }

        final File targetDir;
        if (hasZip) {
            SystemFileChooser dirChooser = new SystemFileChooser();
            dirChooser.setMultiSelectionEnabled(false);
            dirChooser.setFileSelectionMode(SystemFileChooser.DIRECTORIES_ONLY);
            dirChooser.setDialogTitle(Lang.get("dialog.select_target_dir"));

            if (dirChooser.showOpenDialog(this.getMainView()) != SystemFileChooser.APPROVE_OPTION) {
                return;
            }
            targetDir = dirChooser.getSelectedFile();
        } else {
            targetDir = null;
        }

        setLoading(true);
        setStatus(Lang.get("status.loading_files", selectedFiles.length));

        new SwingWorker<List<DataTable>, DataTableWorkerStatus>() {
            @Override
            protected List<DataTable> doInBackground() throws Exception {
                List<DataTable> loadedTables = new ArrayList<>();
                long startTime = System.currentTimeMillis();

                for (File file : selectedFiles) {
                    if (FileService.getExtension(file).equalsIgnoreCase("zip")) {
                        loadedTables.addAll(TableService.load(file, targetDir));
                        continue;
                    }

                    List<DataTable> loadedFiles = TableService.load(file, file.getParentFile());
                    if (loadedFiles.isEmpty()) {
                        continue;
                    }

                    TableService.addRecent(file);
                    loadedTables.addAll(loadedFiles);
                }

                long elapsed = System.currentTimeMillis() - startTime;
                publish(new DataTableWorkerStatus(loadedTables.size(), selectedFiles.length, elapsed));
                return loadedTables;
            }

            @Override
            protected void process(List<DataTableWorkerStatus> chunks) {
                DataTableWorkerStatus status = chunks.get(chunks.size() - 1);
                if (status.totalCount() == 1) {
                    setStatus(Lang.get("status.loaded_file", status.loadedCount(), status.elapsed()));
                } else {
                    setStatus(Lang.get("status.loaded_files", status.loadedCount(), status.totalCount(),
                            status.elapsed()));
                }
            }

            @Override
            protected void done() {
                try {
                    List<DataTable> loadedTables = get();
                    if (loadedTables.size() == 1) {
                        mainView.getTablePanel().open(loadedTables.get(0));
                    } else {
                        getMainView().getTablePanel().refresh();
                    }
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
        } catch (Exception e) {
            onOpenExceptionDialog(e);
            return;
        }

        SystemFileChooser dirChooser = new SystemFileChooser();
        dirChooser.setMultiSelectionEnabled(false);
        dirChooser.setFileSelectionMode(SystemFileChooser.DIRECTORIES_ONLY);
        dirChooser.setDialogTitle(Lang.get("dialog.select_target_dir"));

        if (dirChooser.showOpenDialog(this.getMainView()) != SystemFileChooser.APPROVE_OPTION) {
            return;
        }
        File targetDir = dirChooser.getSelectedFile();

        setLoading(true);
        setStatus(Lang.get("status.loading_url", input));

        new SwingWorker<List<DataTable>, DataTableWorkerStatus>() {
            @Override
            protected List<DataTable> doInBackground() throws Exception {
                long startTime = System.currentTimeMillis();
                List<DataTable> loadedTables = TableService.load(uri, targetDir);
                long elapsed = System.currentTimeMillis() - startTime;

                publish(new DataTableWorkerStatus(loadedTables.size(), 1, elapsed));
                return loadedTables;
            }

            @Override
            protected void process(List<DataTableWorkerStatus> chunks) {
                DataTableWorkerStatus status = chunks.get(chunks.size() - 1);
                if (status.totalCount() == 1) {
                    setStatus(Lang.get("status.loaded_single_url", input, status.elapsed()));
                } else {
                    setStatus(Lang.get("status.loaded_url", status.loadedCount(), input, status.elapsed()));
                }
            }

            @Override
            protected void done() {
                try {
                    List<DataTable> loadedTables = get();
                    if (loadedTables.size() == 1) {
                        mainView.getTablePanel().open(loadedTables.get(0));
                    } else {
                        getMainView().getTablePanel().refresh();
                    }
                } catch (Exception e) {
                    onOpenExceptionDialog(e);
                } finally {
                    setLoading(false);
                }
            }
        }.execute();
    }

    public void onOpenDocDialog() {

    }

    public void onOpenAboutDialog() {
        StringBuilder sb = new StringBuilder();
        sb.append(Lang.get("about.description")).append("\n");
        sb.append(Lang.get("about.version")).append("\n");
        sb.append(Lang.get("about.developed_by")).append("\n");
        sb.append(" - Jules Garcia (@jules1univ)\n");
        sb.append(" - Paul Gallon (@MarcoPaulot)\n");
        sb.append(" - Elouan Barbier (@Marsu2)\n");
        sb.append(" - Noé Berthelier (@nberthelier)\n");
        sb.append(" - Briac Boitel (@bboitel)\n");
        sb.append(" - Kerem Eylem (@Keylem)\n");
        sb.append(" - Basile Guemene (@Astala-Boom)\n");
        sb.append("\n");
        sb.append(Lang.get("about.license"));

        JOptionPane.showMessageDialog(mainView, sb.toString(), Lang.get("about.title"),
                JOptionPane.INFORMATION_MESSAGE);
    }

    public void onLanguageChange(Locale locale) {
        if (locale.equals(Lang.getLocale())) {
            return;
        }

        Lang.setLocale(locale);
        Config.get().put("language", locale.toLanguageTag());
        SwingUtilities.invokeLater(() -> {

            MainView oldView = mainView;
            oldView.dispose();

            MainView newView = new MainView(this);
            setMainView(newView);
            newView.setVisible(true);
        });
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
                Lang.get("statistics.null_rate.title", currentTable.getColumnName(columnIndex)),
                Lang.get("statistics.null_rate.content", nullRateStr));
        dialog.setVisible(true);
    }

    public void onComputeCardinalityRatioRequested(int columnIndex) {
        OptionalDouble cardinalityRatioOpt = StatisticService.computeCardinalityRatio(currentTable, columnIndex);
        String cardinalityRatioStr = cardinalityRatioOpt.isPresent()
                ? String.format("%.2f%%", cardinalityRatioOpt.getAsDouble() * 100)
                : "N/A";
        StatisticsDialog dialog = new StatisticsDialog(mainView,
                Lang.get("statistics.cardinality_ratio.title", currentTable.getColumnName(columnIndex)),
                Lang.get("statistics.cardinality_ratio.content", cardinalityRatioStr));
        dialog.setVisible(true);
    }

    public void onComputeInterquartileRangeRequested(int columnIndex) {
        OptionalDouble iqrOpt = StatisticService.computeInterquartileRange(currentTable, columnIndex);
        String iqrStr = iqrOpt.isPresent() ? String.format("%.4f", iqrOpt.getAsDouble()) : "N/A";
        StatisticsDialog dialog = new StatisticsDialog(mainView,
                Lang.get("statistics.interquartile_range.title", currentTable.getColumnName(columnIndex)),
                Lang.get("statistics.interquartile_range.content", iqrStr));
        dialog.setVisible(true);
    }

    public void onComputeSkewnessRequested(int columnIndex) {
        OptionalDouble skewnessOpt = StatisticService.computeSkewness(currentTable, columnIndex);
        String skewnessStr = skewnessOpt.isPresent() ? String.format("%.4f", skewnessOpt.getAsDouble()) : "N/A";
        StatisticsDialog dialog = new StatisticsDialog(mainView,
                Lang.get("statistics.skewness.title", currentTable.getColumnName(columnIndex)),
                Lang.get("statistics.skewness.content", skewnessStr));
        dialog.setVisible(true);
    }

    public void onComputeCoefficientOfVariationRequested(int columnIndex) {
        OptionalDouble coefVarOpt = StatisticService.computeCoefficientOfVariation(currentTable, columnIndex);
        String coefVarStr = coefVarOpt.isPresent() ? String.format("%.4f", coefVarOpt.getAsDouble()) : "N/A";
        StatisticsDialog dialog = new StatisticsDialog(mainView,
                Lang.get("statistics.coefficient_of_variation.title", currentTable.getColumnName(columnIndex)),
                Lang.get("statistics.coefficient_of_variation.content", coefVarStr));
        dialog.setVisible(true);
    }

    public void onComputeCorrelationRequested(int columnIndex, int targetColumnIndex) {
        OptionalDouble correlationOpt = StatisticService.computeCorrelation(currentTable, columnIndex,
                targetColumnIndex);
        String correlationStr = correlationOpt.isPresent() ? String.format("%.4f", correlationOpt.getAsDouble())
                : "N/A";
        StatisticsDialog dialog = new StatisticsDialog(mainView,
                Lang.get("statistics.correlation.title", currentTable.getColumnName(columnIndex),
                        currentTable.getColumnName(targetColumnIndex)),
                Lang.get("statistics.correlation.content", correlationStr));
        dialog.setVisible(true);

    }
}