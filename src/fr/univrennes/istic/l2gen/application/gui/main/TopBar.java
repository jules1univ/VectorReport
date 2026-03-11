package fr.univrennes.istic.l2gen.application.gui.main;

import javax.swing.*;

import fr.univrennes.istic.l2gen.application.gui.GUIController;

public final class TopBar extends JMenuBar {

    private GUIController controller;

    public TopBar(GUIController controller) {
        this.build();
    }

    private void build() {
        JMenu file = new JMenu("File");
        JMenuItem openItem = new JMenuItem("Open");
        openItem.addActionListener(e -> controller.openFileOrFolder());
        file.add(openItem);

        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(e -> System.exit(0)); // TODO: better exit handling
        file.add(exitItem);

        JMenu view = new JMenu("View");
        JMenuItem refreshItem = new JMenuItem("Refresh");
        refreshItem.addActionListener(e -> controller.refreshView());
        view.add(refreshItem);

        JMenu layout = new JMenu("Layout");
        JMenuItem tableAndFilterView = new JMenuItem("Table & Filters");
        tableAndFilterView.addActionListener(e -> controller.setCurrentView(LayoutType.TABLE_AND_FILTERS));
        layout.add(tableAndFilterView);

        JMenuItem chartAndSetting = new JMenuItem("Charts & Settings");
        chartAndSetting.addActionListener(e -> controller.setCurrentView(LayoutType.CHARTS_AND_SETTINGS));
        layout.add(chartAndSetting);

        JMenuItem tableAndMultiple = new JMenuItem("Table & Multiple");
        tableAndMultiple.addActionListener(e -> controller.setCurrentView(LayoutType.TABLE_AND_MULTIPLE));
        layout.add(tableAndMultiple);

        JMenuItem resetLayout = new JMenuItem("Reset");
        resetLayout.addActionListener(e -> controller.setCurrentView(null));
        layout.add(resetLayout);
        view.add(layout);

        JMenu help = new JMenu("Help");

        JMenuItem documentation = new JMenuItem("Documentation");
        documentation.addActionListener(e -> controller.openDocumentation());
        help.add(documentation);

        JMenuItem about = new JMenuItem("About");
        about.addActionListener(e -> controller.openAbout());
        help.add(about);

        add(file);
        add(view);
        add(help);
    }
}