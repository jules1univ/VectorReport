package fr.univrennes.istic.l2gen.application.gui.main;

import javax.swing.*;

import fr.univrennes.istic.l2gen.application.core.lang.Lang;

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

        statusLabel = new JLabel("");
        statusLabel.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 0));
        statusLabel.setFont(statusLabel.getFont().deriveFont(Font.PLAIN, 11f));
        add(statusLabel, BorderLayout.WEST);

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

        JPanel labelsRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        labelsRow.setOpaque(false);

        labelsRow.add(nameLabel);
        labelsRow.add(makeSeparator());
        labelsRow.add(rowsLabel);
        labelsRow.add(makeSeparator());
        labelsRow.add(colsLabel);
        labelsRow.add(makeSeparator());
        labelsRow.add(sumLabel);
        labelsRow.add(makeSeparator());
        labelsRow.add(avgLabel);
        labelsRow.add(makeSeparator());
        labelsRow.add(minLabel);
        labelsRow.add(makeSeparator());
        labelsRow.add(maxLabel);

        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false);
        centerPanel.add(labelsRow, new GridBagConstraints());

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
            rowsLabel.setText(Lang.get("status.rows_count", rows));
            colsLabel.setText(Lang.get("status.cols_count", cols));

            nameLabel.setVisible(true);
            rowsLabel.setVisible(true);
            colsLabel.setVisible(true);
        });
    }

    public void setColumnStats(Optional<String> min, Optional<String> max, Optional<String> avg, Optional<String> sum) {
        SwingUtilities.invokeLater(() -> {
            sum.ifPresentOrElse(value -> {
                sumLabel.setText(Lang.get("status.sum", value));
                sumLabel.setVisible(true);
            }, () -> sumLabel.setVisible(false));
            avg.ifPresentOrElse(value -> {
                avgLabel.setText(Lang.get("status.avg", value));
                avgLabel.setVisible(true);
            }, () -> avgLabel.setVisible(false));
            min.ifPresentOrElse(value -> {
                minLabel.setText(Lang.get("status.min", value));
                minLabel.setVisible(true);
            }, () -> minLabel.setVisible(false));
            max.ifPresentOrElse(value -> {
                maxLabel.setText(Lang.get("status.max", value));
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