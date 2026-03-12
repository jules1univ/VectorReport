package fr.univrennes.istic.l2gen.application.gui.main;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import fr.univrennes.istic.l2gen.application.gui.GUIController;
import fr.univrennes.istic.l2gen.application.gui.panels.ChartPanel;
import fr.univrennes.istic.l2gen.application.gui.panels.ChartSettingPanel;
import fr.univrennes.istic.l2gen.application.gui.panels.table.TablePanel;

public final class MainView extends JFrame {

    private final GUIController controller;

    private final TablePanel tablePanel;
    private final ChartPanel chartPanel;
    private final ChartSettingPanel chartSettingPanel;

    private final TopBar topBar;
    private final BottomBar bottomBar;

    private JSplitPane topSplit;
    private JSplitPane mainSplit;

    private boolean tableVisible = true;
    private boolean chartVisible = true;
    private boolean settingsVisible = true;

    public MainView(GUIController controller) {
        this.controller = controller;

        this.tablePanel = new TablePanel(controller);
        this.chartPanel = new ChartPanel(controller);
        this.chartSettingPanel = new ChartSettingPanel(controller);

        this.topBar = new TopBar(controller);
        this.bottomBar = new BottomBar();

        build();
    }

    private void build() {
        setTitle("VectorReport");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1280, 800);
        setMinimumSize(new Dimension(700, 450));
        setLocationRelativeTo(null);
        setJMenuBar(topBar);

        this.tablePanel.setBorder(BorderFactory.createLineBorder(UIManager.getColor("Separator.foreground")));
        this.chartPanel.setBorder(BorderFactory.createLineBorder(UIManager.getColor("Separator.foreground")));
        this.chartSettingPanel.setBorder(BorderFactory.createLineBorder(UIManager.getColor("Separator.foreground")));

        add(bottomBar, BorderLayout.SOUTH);
        buildLayout();
    }

    private void buildLayout() {
        BorderLayout layout = (BorderLayout) getContentPane().getLayout();
        Component center = layout.getLayoutComponent(BorderLayout.CENTER);
        if (center != null)
            remove(center);

        topSplit = null;
        mainSplit = null;

        List<Component> topComponents = new ArrayList<>();
        if (chartVisible)
            topComponents.add(chartPanel);
        if (settingsVisible)
            topComponents.add(chartSettingPanel);

        Component centerComponent;

        if (topComponents.isEmpty() && !tableVisible) {
            centerComponent = new JPanel();
        } else if (topComponents.isEmpty()) {
            centerComponent = tablePanel;
        } else if (!tableVisible) {
            centerComponent = buildTopArea(topComponents);
        } else {
            Component top = buildTopArea(topComponents);
            mainSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, top, tablePanel);
            mainSplit.setResizeWeight(0.6);
            centerComponent = mainSplit;
        }

        add(centerComponent, BorderLayout.CENTER);
        revalidate();
        repaint();

        SwingUtilities.invokeLater(() -> {
            if (topSplit != null) {
                topSplit.setDividerLocation(0.7);
            }
            if (mainSplit != null) {
                mainSplit.setDividerLocation(0.6);
            }
        });
    }

    private Component buildTopArea(List<Component> components) {
        if (components.size() == 1) {
            return components.get(0);
        }
        topSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, components.get(0), components.get(1));
        topSplit.setResizeWeight(0.7);
        return topSplit;
    }

    public void togglePanel(PanelType type) {
        switch (type) {
            case TABLE -> tableVisible = !tableVisible;
            case CHART -> chartVisible = !chartVisible;
            case SETTINGS -> settingsVisible = !settingsVisible;
        }
        buildLayout();
    }

    public void detachPanel(PanelType type) {
        JComponent panel = switch (type) {
            case TABLE -> tablePanel;
            case CHART -> chartPanel;
            case SETTINGS -> chartSettingPanel;
        };

        String title = switch (type) {
            case TABLE -> "Table";
            case CHART -> "Chart";
            case SETTINGS -> "Settings";
        };

        switch (type) {
            case TABLE -> tableVisible = false;
            case CHART -> chartVisible = false;
            case SETTINGS -> settingsVisible = false;
        }
        buildLayout();

        JDialog dialog = new JDialog(this, title, false);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);
        dialog.add(panel, BorderLayout.CENTER);
        dialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                dialog.remove(panel);
                switch (type) {
                    case TABLE -> tableVisible = true;
                    case CHART -> chartVisible = true;
                    case SETTINGS -> settingsVisible = true;
                }
                buildLayout();
            }
        });
        dialog.setVisible(true);
    }

    public TablePanel getTablePanel() {
        return tablePanel;
    }

    public ChartPanel getChartPanel() {
        return chartPanel;
    }

    public ChartSettingPanel getChartSettingPanel() {
        return chartSettingPanel;
    }

    public TopBar getTopBar() {
        return topBar;
    }

    public BottomBar getBottomBar() {
        return bottomBar;
    }
}