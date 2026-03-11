package fr.univrennes.istic.l2gen.application.gui.panels;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class ParamPanel extends JFrame {

    private static final Color BG_COLOR = new Color(242, 245, 248);
    // colors used for layout background only
    private static final Color ACCENT_COLOR = new Color(99, 102, 241);

    // section 1 (pie slice colors)
    private final Map<String, Color> sliceColors = new LinkedHashMap<>();
    private JComboBox<String> sliceSelector;
    private JLabel colorHexLabel;
    private JPanel colorPreview;

    // section 2 (CSV title formatting)
    private JTextField rawInput;
    private JLabel formattedLabel;

    public ParamPanel() {
        super("Paramétrage");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG_COLOR);
        initData();
        initUI();
    }

    private void initData() {
        sliceColors.put("Tranche A", Color.RED);
        sliceColors.put("Tranche B", Color.GREEN);
        sliceColors.put("Tranche C", Color.BLUE);
    }

    private void initUI() {
        JPanel main = new JPanel();
        main.setLayout(new BoxLayout(main, BoxLayout.Y_AXIS));
        main.setBackground(BG_COLOR);
        main.setBorder(new EmptyBorder(20, 20, 20, 20));

        main.add(createSection1());
        main.add(Box.createVerticalStrut(30));
        main.add(createSection2());

        setContentPane(main);
    }

    private JPanel createSection1() {
        JPanel panel = new JPanel();
        panel.setBackground(BG_COLOR);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createTitledBorder("Section 1 : Couleurs du camembert"));

        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        row.setBackground(BG_COLOR);
        row.add(new JLabel("Morceau :"));
        sliceSelector = new JComboBox<>(sliceColors.keySet().toArray(new String[0]));
        sliceSelector.addActionListener(e -> updateColorDisplay());
        row.add(sliceSelector);

        // simple square showing color
        colorPreview = new JPanel();
        colorPreview.setPreferredSize(new Dimension(20, 20));
        colorPreview.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        colorPreview.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        colorPreview.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                openColorChooser();
            }
        });
        row.add(colorPreview);

        colorHexLabel = new JLabel("Couleur : ");
        colorHexLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        row.add(colorHexLabel);

        panel.add(row);
        updateColorDisplay();
        return panel;
    }

    private JPanel createSection2() {
        JPanel panel = new JPanel();
        panel.setBackground(BG_COLOR);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createTitledBorder("Section 2 : Titres CSV"));

        JPanel row1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        row1.setBackground(BG_COLOR);
        row1.add(new JLabel("Valeur brute :"));
        rawInput = new JTextField(30);
        row1.add(rawInput);
        panel.add(row1);

        JPanel row2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        row2.setBackground(BG_COLOR);
        JButton formatButton = new JButton("Mettre en forme");
        formatButton.addActionListener(e -> formatTitle());
        row2.add(formatButton);
        panel.add(row2);

        formattedLabel = new JLabel("Affichage :");
        formattedLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        panel.add(formattedLabel);

        return panel;
    }

    private void updateColorDisplay() {
        String key = (String) sliceSelector.getSelectedItem();
        Color c = sliceColors.get(key);
        colorHexLabel.setText("Couleur : #" + Integer.toHexString(c.getRGB()).substring(2).toUpperCase());
        colorPreview.setBackground(c);
    }

    private void openColorChooser() {
        String key = (String) sliceSelector.getSelectedItem();
        Color initial = sliceColors.get(key);
        Color chosen = JColorChooser.showDialog(this, "Couleur pour " + key, initial);
        if (chosen != null) {
            sliceColors.put(key, chosen);
            updateColorDisplay();
        }
    }

    private void formatTitle() {
        String raw = rawInput.getText();
        if (raw == null) raw = "";
        String formatted = raw.trim().replaceAll("\\s+", " ");
        formattedLabel.setText("Affichage : " + formatted);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ParamPanel().setVisible(true));
    }
}
