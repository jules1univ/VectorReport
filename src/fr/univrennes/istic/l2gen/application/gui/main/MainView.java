package fr.univrennes.istic.l2gen.application.gui.main;

import javax.swing.*;

import fr.univrennes.istic.l2gen.application.gui.GUIController;
import fr.univrennes.istic.l2gen.application.gui.panels.TablePanel;

import java.awt.*;

public final class MainView extends JFrame {

    private final GUIController controller;

    private final TablePanel tablePanel;
    private final JScrollPane panelY;
    private final JScrollPane panelZ;

    private final TopBar topBar;
    private final BottomBar bottomBar;

    public MainView(GUIController controller) {
        this.controller = controller;

        this.tablePanel = new TablePanel();
        this.panelY = new JScrollPane();
        this.panelZ = new JScrollPane();

        this.topBar = new TopBar(controller);
        this.bottomBar = new BottomBar();
        build();
    }

    private void build() {
        setTitle("VectorReport");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 620);
        setMinimumSize(new Dimension(700, 450));
        setLocationRelativeTo(null);
        setJMenuBar(topBar);

        add(bottomBar, BorderLayout.SOUTH);

        JSplitPane topSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, panelY, panelZ);
        topSplit.setResizeWeight(0.5);

        JSplitPane mainSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, topSplit, tablePanel);
        mainSplit.setResizeWeight(0.7);

        add(mainSplit, BorderLayout.CENTER);

        SwingUtilities.invokeLater(() -> {
            topSplit.setDividerLocation(0.5);
            mainSplit.setDividerLocation(0.7);
        });
    }

    public TablePanel getTablePanel() {
        return tablePanel;
    }

    public JScrollPane getPanelY() {
        return panelY;
    }

    public JScrollPane getPanelZ() {
        return panelZ;
    }

    public TopBar getTopBar() {
        return topBar;
    }

    public BottomBar getBottomBar() {
        return bottomBar;
    }
}