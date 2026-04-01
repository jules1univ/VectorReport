package fr.univrennes.istic.l2gen.application.gui.panels.report;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import fr.univrennes.istic.l2gen.application.gui.GUIController;
import fr.univrennes.istic.l2gen.application.gui.panels.report.views.notebook.NoteBook;
import fr.univrennes.istic.l2gen.application.gui.panels.report.views.settings.SettingView;

public class ReportPanel extends JPanel {
    private final GUIController controller;

    private SettingView settingView;
    private NoteBook noteBook;

    private JSplitPane mainSplit;

    public ReportPanel(GUIController controller) {
        this.controller = controller;
        this.build();
    }

    private void build() {
        this.settingView = new SettingView(this.controller);
        this.noteBook = new NoteBook(this.controller);

        this.mainSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, this.noteBook, this.settingView);

        this.noteBook.setBorder(BorderFactory.createLineBorder(UIManager.getColor("Separator.foreground")));
        this.settingView.setBorder(BorderFactory.createLineBorder(UIManager.getColor("Separator.foreground")));

        SwingUtilities.invokeLater(() -> this.mainSplit.setDividerLocation(0.7));

        setLayout(new BorderLayout());
        add(mainSplit, BorderLayout.CENTER);
    }
}
