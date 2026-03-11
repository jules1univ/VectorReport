package fr.univrennes.istic.l2gen.application.gui;

import java.io.File;
import java.net.URI;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import com.formdev.flatlaf.util.SystemFileChooser;

import fr.univrennes.istic.l2gen.application.core.CoreController;
import fr.univrennes.istic.l2gen.application.core.services.CoreServices;
import fr.univrennes.istic.l2gen.application.gui.dialogs.SelectDialog;
import fr.univrennes.istic.l2gen.application.gui.main.LayoutType;
import fr.univrennes.istic.l2gen.application.gui.main.MainView;
import fr.univrennes.istic.l2gen.io.csv.model.CSVTable;

public final class GUIController extends CoreController {

    private MainView frame;

    public GUIController() {
        this(CoreServices.defaultServices());
    }

    public GUIController(CoreServices services) {
        super(services);
    }

    @Override
    public boolean init() {
        return true;
    }

    public void setFrame(MainView frame) {
        this.frame = frame;
    }

    public void openFileOrFolder() {
        SystemFileChooser fileChooser = new SystemFileChooser();
        // fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        fileChooser.setMultiSelectionEnabled(true);

        if (fileChooser.showOpenDialog(frame) != SystemFileChooser.APPROVE_OPTION) {
            return;
        }

        File file = fileChooser.getSelectedFile();

        boolean success;
        long startTime = System.currentTimeMillis();
        if (file.isDirectory()) {
            this.setStatus("Loading folder: " + file.getName());
            success = this.getLoader().loadFolder(file, null, true);
        } else {
            this.setStatus("Loading file: " + file.getName());
            success = this.getLoader().loadFile(file, null, true);
        }
        long endTime = System.currentTimeMillis();
        this.setStatus(String.format("Loaded in %.2f seconds", (endTime - startTime) / 1000.0));

        if (!success) {
            JOptionPane.showMessageDialog(frame, "Failed to load the selected file/folder.", "Error",
                    JOptionPane.ERROR_MESSAGE);
        } else {
            this.selectCurrentTable();
        }

    }

    public void openUrl() {
        String url = JOptionPane.showInputDialog(frame, "URL", "Load from url",
                JOptionPane.PLAIN_MESSAGE);
        if (url != null && !url.trim().isEmpty()) {
            try {
                URI uri = URI.create(url);
                this.setStatus("Loading from URL: " + uri);

                long startTime = System.currentTimeMillis();
                boolean success = this.getLoader().loadUrl(uri, null, true);
                long endTime = System.currentTimeMillis();

                if (!success) {
                    JOptionPane.showMessageDialog(frame, "Failed to load from URL.", "Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
                this.setStatus(String.format("Loaded from URL in %.2f seconds", (endTime - startTime) / 1000.0));
                this.selectCurrentTable();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(frame, "Failed to load from URL: " + e.getMessage(), "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void selectCurrentTable() {
        List<String> names = this.getLoader().getLoadedFileNames();
        if (names.size() == 0) {
            JOptionPane.showMessageDialog(frame, "No tables loaded.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        if (names.size() == 1) {
            CSVTable table = this.getLoader().getTable();
            this.setTable(table);
            return;
        }

        String selectedName = SelectDialog.show(
                frame,
                "Select Table",
                "Select a table to load:",
                names);

        if (selectedName == null)
            return;

        CSVTable table = this.getLoader().getTableByName(selectedName);
        this.setTable(table);
    }

    public void refreshView() {

    }

    public void onColumnSelected(int colIndex) {

    }

    public void setCurrentView(LayoutType layout) {

    }

    public void setStatus(String status) {
        frame.getBottomBar().setStatus(status);
    }

    public void openDocumentation() {

    }

    public void openAbout() {

    }

    @Override
    public void setTable(CSVTable table) {
        super.setTable(table);
        frame.getTablePanel().load(table);
    }
}