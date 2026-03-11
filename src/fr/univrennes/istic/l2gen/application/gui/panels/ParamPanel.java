package fr.univrennes.istic.l2gen.application.gui.panels;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class ParamPanel extends JFrame {

    private static final Color BG_COLOR = new Color(242, 245, 248);

    // section 1 (chart settings: colors per item / series)
    // map chartType -> (itemName -> color)
    private final Map<String, Map<String, Color>> typeColors = new LinkedHashMap<>();
    private JComboBox<String> typeSelector;
    private JComboBox<String> sliceSelector;
    private JLabel colorHexLabel;
    private JPanel colorPreview;
    private PreviewPanel previewPanel;            // small visualisation

    // section 2 (CSV title formatting + aliases)
    private JTextField rawInput;
    private JLabel formattedLabel;
    private JTextField aliasKeyField;
    private JTextField aliasValueField;
    private DefaultListModel<String> aliasModel;
    private JList<String> aliasList;

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
        Map<String, Color> pie = new LinkedHashMap<>();
        pie.put("Tranche A", Color.RED);
        pie.put("Tranche B", Color.GREEN);
        pie.put("Tranche C", Color.BLUE);
        typeColors.put("Pie", pie);

        Map<String, Color> line = new LinkedHashMap<>();
        line.put("Série 1", Color.BLUE);
        line.put("Série 2", Color.MAGENTA);
        typeColors.put("Line", line);

        Map<String, Color> bar = new LinkedHashMap<>();
        bar.put("Bar 1", Color.ORANGE);
        bar.put("Bar 2", Color.CYAN);
        typeColors.put("Bar", bar);
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
        panel.setBorder(BorderFactory.createTitledBorder("Section 1 : Paramètres des graphiques"));

        JPanel typeRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        typeRow.setBackground(BG_COLOR);
        typeRow.add(new JLabel("Type :"));
        typeSelector = new JComboBox<>(typeColors.keySet().toArray(new String[0]));
        typeSelector.addActionListener(e -> updateTypeItems());
        typeRow.add(typeSelector);
        panel.add(typeRow);

        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        row.setBackground(BG_COLOR);
        row.add(new JLabel("Item :"));
        sliceSelector = new JComboBox<>();
        sliceSelector.addActionListener(e -> updateColorDisplay());
        row.add(sliceSelector);

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
        row.add(colorHexLabel);

        // preview panel sits in same row to the right
        previewPanel = new PreviewPanel();
        previewPanel.setPreferredSize(new Dimension(100, 100));
        row.add(previewPanel);

        panel.add(row);
        updateTypeItems();
        return panel;
    }

    private JPanel createSection2() {
        JPanel panel = new JPanel();
        panel.setBackground(BG_COLOR);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createTitledBorder("Section 2 : Titres CSV et alias"));

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
        panel.add(formattedLabel);

        panel.add(Box.createVerticalStrut(20));
        JPanel aliasRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        aliasRow.setBackground(BG_COLOR);
        aliasRow.add(new JLabel("Brut :"));
        aliasKeyField = new JTextField(10);
        aliasRow.add(aliasKeyField);
        aliasRow.add(new JLabel("Alias :"));
        aliasValueField = new JTextField(10);
        aliasRow.add(aliasValueField);
        JButton addAlias = new JButton("Ajouter");
        addAlias.addActionListener(e -> addAlias());
        aliasRow.add(addAlias);
        panel.add(aliasRow);

        aliasModel = new DefaultListModel<>();
        aliasList = new JList<>(aliasModel);
        JScrollPane aliasScroll = new JScrollPane(aliasList);
        aliasScroll.setPreferredSize(new Dimension(400, 100));
        panel.add(aliasScroll);

        return panel;
    }

    private void updateTypeItems() {
        String type = (String) typeSelector.getSelectedItem();
        Map<String, Color> map = typeColors.get(type);
        sliceSelector.removeAllItems();
        if (map != null) {
            for (String item : map.keySet()) {
                sliceSelector.addItem(item);
            }
        }
        updateColorDisplay();
        updatePreview();
    }

    private void updateColorDisplay() {
        String type = (String) typeSelector.getSelectedItem();
        if (type == null) return;
        Map<String, Color> map = typeColors.get(type);
        if (map == null) return;
        String key = (String) sliceSelector.getSelectedItem();
        if (key == null) return;
        Color c = map.get(key);
        colorHexLabel.setText("Couleur : #" + Integer.toHexString(c.getRGB()).substring(2).toUpperCase());
        colorPreview.setBackground(c);
        updatePreview();
    }

    private void openColorChooser() {
        String type = (String) typeSelector.getSelectedItem();
        Map<String, Color> map = typeColors.get(type);
        String key = (String) sliceSelector.getSelectedItem();
        Color initial = map.get(key);
        Color chosen = JColorChooser.showDialog(this, "Couleur pour " + key, initial);
        if (chosen != null) {
            map.put(key, chosen);
            updateColorDisplay();
        }
    }

    private void updatePreview() {
        if (previewPanel != null) {
            previewPanel.repaint();
        }
    }

    private void formatTitle() {
        String raw = rawInput.getText();
        if (raw == null) raw = "";
        String formatted = raw.trim().replaceAll("\\s+", " ");
        formattedLabel.setText("Affichage : " + formatted);
    }

    private void addAlias() {
        String key = aliasKeyField.getText();
        String val = aliasValueField.getText();
        if (key != null && !key.isEmpty() && val != null && !val.isEmpty()) {
            aliasModel.addElement(key + " => " + val);
            aliasKeyField.setText("");
            aliasValueField.setText("");
        }
    }

    private class PreviewPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            String type = (String) typeSelector.getSelectedItem();
            if (type == null) return;
            Map<String, Color> map = typeColors.get(type);
            if (map == null) return;
            int w = getWidth();
            int h = getHeight();
            if (type.equals("Pie")) {
                int start = 0;
                int total = map.size();
                for (Color c : map.values()) {
                    g.setColor(c);
                    g.fillArc(0, 0, w, h, start, 360/total);
                    start += 360/total;
                }
            } else if (type.equals("Line")) {
                g.setColor(Color.GRAY);
                int x0 = 0, y0 = h/2;
                int dx = (w)/(map.size()-1);
                int idx = 0;
                int prevX = x0, prevY = y0;
                for (Color c : map.values()) {
                    int x = x0 + idx*dx;
                    int y = y0 - (idx%2==0?20:-20);
                    g.setColor(c);
                    g.drawLine(prevX, prevY, x, y);
                    prevX = x;
                    prevY = y;
                    idx++;
                }
            } else if (type.equals("Bar")) {
                int bx = 0;
                int bw = w/map.size();
                for (Color c : map.values()) {
                    g.setColor(c);
                    g.fillRect(bx, h/4, bw-2, h/2);
                    bx += bw;
                }
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ParamPanel().setVisible(true));
    }
}
