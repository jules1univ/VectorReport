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
        openItem.addActionListener(e -> controller.onOpenFileDialog());
        file.add(openItem);

        JMenuItem openUrlItem = new JMenuItem("Open URL");
        openUrlItem.addActionListener(e -> controller.onOpenUrlDialog());
        file.add(openUrlItem);

        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(e -> System.exit(0));
        file.add(exitItem);

        JMenu view = new JMenu("View");

        JMenu panels = new JMenu("Panels");
        panels.addSeparator();

        view.add(panels);

        JMenu help = new JMenu("Help");

        JMenuItem documentation = new JMenuItem("Documentation");
        documentation.addActionListener(e -> controller.onOpenDocDialog());
        help.add(documentation);

        JMenuItem about = new JMenuItem("About");
        about.addActionListener(e -> controller.onOpenAboutDialog());
        help.add(about);

        add(file);
        add(view);
        add(help);
    }
}