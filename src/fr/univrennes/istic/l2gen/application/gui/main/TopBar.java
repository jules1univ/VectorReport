package fr.univrennes.istic.l2gen.application.gui.main;

import javax.swing.*;

public final class TopBar extends JMenuBar {

    public TopBar() {
        this.build();
    }

    private void build() {
        JMenu file = new JMenu("File");
        file.add(new JMenuItem("Open"));
        file.add(new JMenuItem("Exit"));

        JMenu view = new JMenu("View");
        view.add(new JMenuItem("Refresh"));

        add(file);
        add(view);
    }
}