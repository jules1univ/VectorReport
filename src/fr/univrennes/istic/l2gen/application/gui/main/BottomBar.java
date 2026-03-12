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
    private final JLabel avgLabel;
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

        nameLabel = createInfoLabel("");
        rowsLabel = createInfoLabel("");
        colsLabel = createInfoLabel("");
        sumLabel = createInfoLabel("");
        avgLabel = createInfoLabel("");
        minLabel = createInfoLabel("");
        maxLabel = createInfoLabel("");

        nameLabel.setVisible(false);
        rowsLabel.setVisible(false);
        colsLabel.setVisible(false);
        sumLabel.setVisible(false);
        avgLabel.setVisible(false);
        minLabel.setVisible(false);
        maxLabel.setVisible(false);

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
            nameLabel.setVisible(true);
            rowsLabel.setVisible(true);
            colsLabel.setVisible(true);
        });
    }

    public void setColumnStats(Optional<String> min, Optional<String> max, Optional<String> avg, Optional<String> sum) {
        SwingUtilities.invokeLater(() -> {
            sum.ifPresentOrElse(value -> {
                sumLabel.setText("Sum: " + value);
                sumLabel.setVisible(true);
            }, () -> sumLabel.setVisible(false));
            avg.ifPresentOrElse(value -> {
                avgLabel.setText("Avg: " + value);
                avgLabel.setVisible(true);
            }, () -> avgLabel.setVisible(false));
            min.ifPresentOrElse(value -> {
                minLabel.setText("Min: " + value);
                minLabel.setVisible(true);
            }, () -> minLabel.setVisible(false));
            max.ifPresentOrElse(value -> {
                maxLabel.setText("Max: " + value);
                maxLabel.setVisible(true);
            }, () -> maxLabel.setVisible(false));
        });
    }

    public void clearColumnStats() {
        SwingUtilities.invokeLater(() -> {
            sumLabel.setVisible(false);
            avgLabel.setVisible(false);
            minLabel.setVisible(false);
            maxLabel.setVisible(false);
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