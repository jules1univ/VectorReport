package fr.univrennes.istic.l2gen.application.gui.main;

import javax.swing.*;
import fr.univrennes.istic.l2gen.application.gui.GUIController;

public final class TopBar extends JMenuBar {

    private final GUIController controller;

    public TopBar(GUIController controller) {
        this.controller = controller;
        this.build();
    }

    private void build() {
        JMenu file = new JMenu("File");

        JMenuItem openItem = new JMenuItem("Open");
        openItem.addActionListener(e -> controller.openFileOrFolder());
        file.add(openItem);

        JMenuItem openUrlItem = new JMenuItem("Open URL");
        openUrlItem.addActionListener(e -> controller.openUrl());
        file.add(openUrlItem);

        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(e -> System.exit(0));
        file.add(exitItem);

        // --- View Menu ---
        JMenu view = new JMenu("View");

        JMenuItem refreshItem = new JMenuItem("Refresh");
        refreshItem.addActionListener(e -> controller.refreshView());
        view.add(refreshItem);

        view.addSeparator();

        // --- Panels submenu ---
        JMenu panels = new JMenu("Panels");

        JMenuItem toggleTable = new JMenuItem("Toggle Table");
        toggleTable.addActionListener(e -> controller.getMainView().togglePanel(PanelType.TABLE));
        panels.add(toggleTable);

        JMenuItem toggleChart = new JMenuItem("Toggle Chart");
        toggleChart.addActionListener(e -> controller.getMainView().togglePanel(PanelType.CHART));
        panels.add(toggleChart);

        JMenuItem toggleSettings = new JMenuItem("Toggle Settings");
        toggleSettings.addActionListener(e -> controller.getMainView().togglePanel(PanelType.SETTINGS));
        panels.add(toggleSettings);

        panels.addSeparator();

        JMenuItem detachTable = new JMenuItem("Detach Table");
        detachTable.addActionListener(e -> controller.getMainView().detachPanel(PanelType.TABLE));
        panels.add(detachTable);

        JMenuItem detachChart = new JMenuItem("Detach Chart");
        detachChart.addActionListener(e -> controller.getMainView().detachPanel(PanelType.CHART));
        panels.add(detachChart);

        JMenuItem detachSettings = new JMenuItem("Detach Settings");
        detachSettings.addActionListener(e -> controller.getMainView().detachPanel(PanelType.SETTINGS));
        panels.add(detachSettings);

        view.add(panels);

        JMenu help = new JMenu("Help");

        JMenuItem documentation = new JMenuItem("Documentation");
        documentation.addActionListener(e -> controller.openDocumentationDialog());
        help.add(documentation);

        JMenuItem about = new JMenuItem("About");
        about.addActionListener(e -> controller.openAboutDialog());
        help.add(about);

        add(file);
        add(view);
        add(help);
    }
}