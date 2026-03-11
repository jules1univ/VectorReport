package fr.univrennes.istic.l2gen.application.gui;

import javax.swing.*;

import fr.univrennes.istic.l2gen.application.gui.panels.TablePanel;

import java.awt.*;

public final class GUIView extends JFrame {

    private final GUIController controller;
    private final TablePanel tablePanel;
    private final JScrollPane panelY;
    private final JScrollPane panelZ;

    public GUIView(GUIController controller) {
        this.controller = controller;
        this.tablePanel = new TablePanel();
        this.panelY = new JScrollPane();
        this.panelZ = new JScrollPane();
        build();
    }

    private void build() {
        setTitle("VectorReport");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 620);
        setMinimumSize(new Dimension(700, 450));
        setLocationRelativeTo(null);

        JSplitPane topSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, panelY, tablePanel);
        topSplit.setResizeWeight(0.5);

        JSplitPane mainSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, topSplit, panelZ);
        mainSplit.setResizeWeight(0.7);

        setContentPane(mainSplit);

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
}