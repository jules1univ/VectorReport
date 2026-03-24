package fr.univrennes.istic.l2gen.application.gui;

import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import com.formdev.flatlaf.util.SystemFileChooser;

import fr.univrennes.istic.l2gen.application.core.CoreController;
import fr.univrennes.istic.l2gen.application.core.config.Config;
import fr.univrennes.istic.l2gen.application.core.services.FileService;
import fr.univrennes.istic.l2gen.application.core.services.StaticsticAction;
import fr.univrennes.istic.l2gen.application.core.services.StatisticService;
import fr.univrennes.istic.l2gen.application.core.table.DataTable;
import fr.univrennes.istic.l2gen.application.core.filter.Filter;
import fr.univrennes.istic.l2gen.application.gui.main.MainView;

public final class GUIController extends CoreController {

    private MainView mainView;
    private DataTable currentTable;

    public GUIController() {
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

            setStatus("Loaded " + table.getAlias());
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
        setStatus("Ready");
    }

    public void onColumnSelected(int columnIndex) {
        if (currentTable != null) {
            mainView.getBottomBar().clearColumnStats();

            CompletableFuture.runAsync(() -> {
                Optional<String> min = StatisticService.computeBase(currentTable, columnIndex, StaticsticAction.MIN);
                Optional<String> max = StatisticService.computeBase(currentTable, columnIndex, StaticsticAction.MAX);
                Optional<String> avg = StatisticService.computeBase(currentTable, columnIndex, StaticsticAction.AVG);
                Optional<String> sum = StatisticService.computeBase(currentTable, columnIndex, StaticsticAction.SUM);

                SwingUtilities.invokeLater(() -> mainView.getBottomBar().setColumnStats(min, max, avg, sum));
            });
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

    public void onOpenFilterDialog() {
        JOptionPane.showMessageDialog(mainView, "Filters are not implemented yet.", "Filters",
                JOptionPane.INFORMATION_MESSAGE);
    }

    public void onOpenFileDialog() {
        SystemFileChooser chooser = new SystemFileChooser();
        chooser.setMultiSelectionEnabled(true);
        chooser.setFileSelectionMode(SystemFileChooser.FILES_ONLY);

        if (chooser.showOpenDialog(this.getMainView()) != SystemFileChooser.APPROVE_OPTION) {
            return;
        }

        File[] selectedFiles = chooser.getSelectedFiles();

        if (selectedFiles.length == 1 && FileService.isParquetFile(selectedFiles[0])) {
            try {
                DataTable table = DataTable.of(selectedFiles[0]);
                Config.getInstance().addRecentFiles(List.of(table));
                getMainView().getTablePanel().open(table);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(mainView, "Failed to load Parquet file.\n" + e.getMessage(),
                        "Load Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        Map<File, Boolean> useCachedDecisions = new HashMap<>();
        for (File file : selectedFiles) {
            if (FileService.isAlreadyProcessed(file)) {
                int option = JOptionPane.showOptionDialog(
                        mainView,
                        "A cached Parquet file already exists for " + file.getName() + ".\n"
                                + "Do you want to use the cached version or reprocess the original file?",
                        "File Already Processed",
                        JOptionPane.DEFAULT_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        new String[] { "Use Cached", "Reprocess" },
                        "Use Cached");
                useCachedDecisions.put(file, option != 1);
            }
        }

        setLoading(true);
        setStatus("Processing " + selectedFiles.length + " files...");

        new SwingWorker<List<DataTable>, Void>() {
            @Override
            protected List<DataTable> doInBackground() throws Exception {
                long startTime = System.currentTimeMillis();
                List<DataTable> processedFiles = new ArrayList<>();

                for (File file : selectedFiles) {
                    boolean useCached = useCachedDecisions.getOrDefault(file, false);

                    if (useCached) {
                        File parquetFile = FileService.getProcessedFile(file);
                        DataTable table = DataTable.of(parquetFile);
                        if (table != null) {
                            processedFiles.add(table);
                            continue;
                        }
                    } else if (FileService.isAlreadyProcessed(file)) {
                        FileService.getProcessedFile(file).delete();
                    }

                    processedFiles.addAll(FileService.process(file));
                }

                long endTime = System.currentTimeMillis();
                setStatus("Processed " + processedFiles.size() + "/" + selectedFiles.length
                        + " files in " + (endTime - startTime) + " ms");
                Config.getInstance().addRecentFiles(processedFiles);
                return processedFiles;
            }

            @Override
            protected void done() {
                try {
                    List<DataTable> processedFiles = get();
                    SwingUtilities.invokeLater(() -> {
                        if (processedFiles.size() == 1) {
                            mainView.getTablePanel().open(processedFiles.get(0));
                        } else {
                            getMainView().getTablePanel().refresh();
                        }
                    });
                } catch (InterruptedException | ExecutionException e) {
                    Throwable rootCause = e.getCause() != null ? e.getCause() : e;
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(
                                mainView,
                                rootCause.getClass().getSimpleName() + ": " + rootCause.getMessage(),
                                "Processing Error",
                                JOptionPane.ERROR_MESSAGE);
                        getMainView().getTablePanel().refresh();
                    });
                }
                setLoading(false);
            }
        }.execute();
    }

    public void onOpenUrlDialog() {
        String input = JOptionPane.showInputDialog(mainView, "Parquet URL:");
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
        new SwingWorker<File, Void>() {
            @Override
            protected File doInBackground() throws Exception {
                return downloadParquet(uri.toURL());
            }

            @Override
            protected void done() {
                try {
                    File file = get();
                    if (!mainView.getTablePanel().open(file)) {
                        JOptionPane.showMessageDialog(mainView, "Downloaded file is not a valid Parquet file.",
                                "Invalid File", JOptionPane.WARNING_MESSAGE);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(mainView, "Failed to download Parquet file.\n" + ex.getMessage(),
                            "Download Error", JOptionPane.ERROR_MESSAGE);
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

    private File downloadParquet(URL url) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setConnectTimeout(10_000);
        connection.setReadTimeout(30_000);
        connection.setInstanceFollowRedirects(true);

        File dir = Config.getInstance().getAppDataDirectory();
        File tempFile = Files.createTempFile(dir.toPath(), "parquet-", ".parquet").toFile();
        tempFile.deleteOnExit();

        try (InputStream input = connection.getInputStream();
                FileOutputStream output = new FileOutputStream(tempFile)) {
            input.transferTo(output);
        }
        return tempFile;
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
}