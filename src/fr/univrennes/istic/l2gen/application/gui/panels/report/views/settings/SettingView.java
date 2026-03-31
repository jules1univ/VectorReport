package fr.univrennes.istic.l2gen.application.gui.panels.report.views.settings;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

import fr.univrennes.istic.l2gen.application.gui.GUIController;

public class SettingView extends JPanel {
    private final GUIController controller;

    public SettingView(GUIController controller) {
        this.controller = controller;
        this.build();
    }

    private void build() {
        setLayout(new BorderLayout());

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));

        content.add(buildSection("Chart", new JPanel[][] {
                row("Type", dropdown(new String[] { "Pie", "Bar", "Columns" })),
                row("Title", textField("My Chart")),
                row("Width", spinner(100, 4000, 800, 10)),
                row("Height", spinner(100, 4000, 600, 10)),
                row("Show Legend", checkbox(true)),
        }));

        content.add(buildSection("Data", new JPanel[][] {
                row("Table filters", dropdown(new String[] { "include", "exclude" })),
                row("Aggregate", dropdown(new String[] { "Sum", "Average", "Count", "Max", "Min" })),
                row("Normalize", checkbox(false)),
        }));

        String[] fonts = List.of(GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts())
                .stream()
                .map(Font::getFontName)
                .toArray(String[]::new);

        content.add(buildSection("Appearance", new JPanel[][] {
                row("Fill Color", colorPicker(new Color(0x4C86C8))),
                row("Stroke Color", colorPicker(Color.DARK_GRAY)),
                row("Opacity", slider(0, 100, 90)),
                row("Stroke Width", spinner(1, 20, 2, 1)),
                row("Font", dropdown(fonts)),
                row("Font Size", spinner(6, 72, 12, 1)),
        }));

        content.add(buildSection("Axes", new JPanel[][] {
                row("Show X Axis", checkbox(true)),
                row("Show Y Axis", checkbox(true)),
                row("X Label", textField("X Axis")),
                row("Y Label", textField("Y Axis")),
                row("Tick Count", spinner(1, 50, 5, 1)),
                row("Scale", dropdown(new String[] { "Linear", "Logarithmic", "Sqrt" })),
        }));

        content.add(Box.createVerticalGlue());

        JScrollPane scroll = new JScrollPane(content);
        scroll.setBorder(null);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        add(scroll, BorderLayout.CENTER);
    }

    private JPanel buildSection(String title, JPanel[][] rows) {
        JPanel section = new JPanel(new BorderLayout());

        JPanel body = new JPanel(new GridBagLayout());
        body.setBorder(BorderFactory.createEmptyBorder(2, 0, 2, 0));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(1, 2, 1, 2);
        gbc.gridy = 0;

        for (JPanel[] row : rows) {
            gbc.gridx = 0;
            gbc.weightx = 0.4;
            body.add(row[0], gbc);
            gbc.gridx = 1;
            gbc.weightx = 0.6;
            body.add(row[1], gbc);
            gbc.gridy++;
        }

        Icon expandedIcon = UIManager.getIcon("Tree.expandedIcon");
        Icon collapsedIcon = UIManager.getIcon("Tree.collapsedIcon");

        JButton header = new JButton(title, expandedIcon);
        header.setHorizontalAlignment(SwingConstants.LEFT);
        header.setFocusPainted(true);
        header.setBorderPainted(true);
        header.setContentAreaFilled(true);

        header.addActionListener(e -> {
            boolean visible = !body.isVisible();
            body.setVisible(visible);
            header.setIcon(visible ? expandedIcon : collapsedIcon);
        });

        section.add(header, BorderLayout.NORTH);
        section.add(body, BorderLayout.CENTER);
        section.setBorder(BorderFactory.createEmptyBorder(0, 0, 4, 0));
        return section;
    }

    private JPanel[] row(String label, JComponent control) {
        JPanel lp = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        lp.add(new JLabel(label));

        JPanel cp = new JPanel(new BorderLayout());
        cp.add(control, BorderLayout.CENTER);

        return new JPanel[] { lp, cp };
    }

    private JTextField textField(String def) {
        return new JTextField(def);
    }

    private JComboBox<String> dropdown(String[] options) {
        return new JComboBox<>(options);
    }

    private JSpinner spinner(int min, int max, int value, int step) {
        return new JSpinner(new SpinnerNumberModel(value, min, max, step));
    }

    private JCheckBox checkbox(boolean selected) {
        JCheckBox box = new JCheckBox();
        box.setSelected(selected);
        return box;
    }

    private JSlider slider(int min, int max, int value) {
        JSlider s = new JSlider(min, max, value);
        s.setPaintTicks(false);
        s.setPaintLabels(false);
        return s;
    }

    private JPanel colorPicker(Color initial) {
        JPanel wrapper = new JPanel(new BorderLayout(4, 0));
        JPanel swatch = new JPanel();
        swatch.setBackground(initial);
        swatch.setPreferredSize(new Dimension(24, 0));
        JButton btn = new JButton("Pick...");
        btn.addActionListener(e -> {
            Color chosen = JColorChooser.showDialog(this, "Choose color", swatch.getBackground());
            if (chosen != null)
                swatch.setBackground(chosen);
        });
        wrapper.add(swatch, BorderLayout.WEST);
        wrapper.add(btn, BorderLayout.CENTER);
        return wrapper;
    }

}
