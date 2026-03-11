package fr.univrennes.istic.l2gen.application.gui.main;

import javax.swing.*;
import java.awt.*;
import java.util.Optional;

public final class BottomBar extends JPanel {

    private final JLabel statusLabel;
    private final JLabel nameLabel;
    private final JLabel rowsLabel;
    private final JLabel colsLabel;
    private final JLabel sumLabel;
    private final JLabel minLabel;
    private final JLabel maxLabel;
    private final JProgressBar loadingBar;

    public BottomBar() {
        setLayout(new BorderLayout(0, 0));
        setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, UIManager.getColor("Separator.foreground")));
        setPreferredSize(new Dimension(0, 26));

        statusLabel = new JLabel("Ready");
        statusLabel.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 0));
        statusLabel.setFont(statusLabel.getFont().deriveFont(Font.PLAIN, 11f));
        add(statusLabel, BorderLayout.WEST);

        JPanel centerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 3));
        centerPanel.setOpaque(false);

        nameLabel = createInfoLabel("—");
        rowsLabel = createInfoLabel("Rows: —");
        colsLabel = createInfoLabel("Cols: —");
        sumLabel = createInfoLabel("Sum: —");
        minLabel = createInfoLabel("Min: —");
        maxLabel = createInfoLabel("Max: —");

        centerPanel.add(nameLabel);
        centerPanel.add(makeSeparator());
        centerPanel.add(rowsLabel);
        centerPanel.add(makeSeparator());
        centerPanel.add(colsLabel);
        centerPanel.add(makeSeparator());
        centerPanel.add(sumLabel);
        centerPanel.add(makeSeparator());
        centerPanel.add(minLabel);
        centerPanel.add(makeSeparator());
        centerPanel.add(maxLabel);

        add(centerPanel, BorderLayout.CENTER);

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 4));
        rightPanel.setOpaque(false);

        loadingBar = new JProgressBar();
        loadingBar.setIndeterminate(false);
        loadingBar.setPreferredSize(new Dimension(100, 14));
        loadingBar.setVisible(false);
        rightPanel.add(loadingBar);

        add(rightPanel, BorderLayout.EAST);
    }

    public void setStatus(String message) {
        SwingUtilities.invokeLater(() -> statusLabel.setText(message));
    }

    public void setTableInfo(String name, int rows, int cols) {
        SwingUtilities.invokeLater(() -> {
            nameLabel.setText(name);
            rowsLabel.setText("Rows: " + rows);
            colsLabel.setText("Cols: " + cols);
        });
    }

    public void setColumnStats(Optional<Double> sum, Optional<Double> min, Optional<Double> max) {
        SwingUtilities.invokeLater(() -> {
            sumLabel.setText("Sum: " + sum.map(this::formatNumber).orElse("—"));
            minLabel.setText("Min: " + min.map(this::formatNumber).orElse("—"));
            maxLabel.setText("Max: " + max.map(this::formatNumber).orElse("—"));
        });
    }

    public void clearColumnStats() {
        SwingUtilities.invokeLater(() -> {
            sumLabel.setText("Sum: —");
            minLabel.setText("Min: —");
            maxLabel.setText("Max: —");
        });
    }

    public void setLoading(boolean isLoading) {
        SwingUtilities.invokeLater(() -> {
            loadingBar.setIndeterminate(isLoading);
            loadingBar.setVisible(isLoading);
            revalidate();
            repaint();
        });
    }

    private String formatNumber(double value) {
        if (value == Math.floor(value) && !Double.isInfinite(value)) {
            return String.valueOf((long) value);
        }
        return String.format("%.2f", value);
    }

    private JLabel createInfoLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(label.getFont().deriveFont(Font.PLAIN, 11f));
        label.setForeground(UIManager.getColor("Label.disabledForeground"));
        label.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
        return label;
    }

    private JSeparator makeSeparator() {
        JSeparator sep = new JSeparator(JSeparator.VERTICAL);
        sep.setPreferredSize(new Dimension(1, 14));
        return sep;
    }
}