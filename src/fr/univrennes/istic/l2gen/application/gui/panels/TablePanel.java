package fr.univrennes.istic.l2gen.application.gui.panels;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.*;

import fr.univrennes.istic.l2gen.io.csv.model.CSVTable;
import fr.univrennes.istic.l2gen.io.csv.parser.CSVParseException;
import fr.univrennes.istic.l2gen.io.csv.parser.CSVParser;

import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

public class TablePanel extends JFrame {

    private JTable table;
    private DefaultTableModel model;

    private final Color BG_COLOR = new Color(242, 245, 248);
    private final Color SIDEBAR_COLOR = Color.WHITE;
    private final Color ACCENT_COLOR = new Color(99, 102, 241);
    private final Color TEXT_COLOR = new Color(30, 41, 59);

    public TablePanel() {
        setTitle("CSV Explorer Pro");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG_COLOR);

        model = new DefaultTableModel();
        table = new JTable(model);
        applyExtremeStyle();

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(BG_COLOR);
        headerPanel.setBorder(new EmptyBorder(20, 25, 20, 25));

        JLabel title = new JLabel("Mes Données CSV");
        title.setFont(new Font("Inter", Font.BOLD, 24));
        title.setForeground(TEXT_COLOR);

        JButton btnOpen = new JButton("Importer un fichier");
        styleModernButton(btnOpen);
        btnOpen.addActionListener(e -> chargerFichier());

        headerPanel.add(title, BorderLayout.WEST);
        headerPanel.add(btnOpen, BorderLayout.EAST);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240), 1));
        scrollPane.getViewport().setBackground(Color.WHITE);

        JPanel tableContainer = new JPanel(new BorderLayout());
        tableContainer.setBackground(BG_COLOR);
        tableContainer.setBorder(new EmptyBorder(0, 25, 25, 25));
        tableContainer.add(scrollPane);

        setLayout(new BorderLayout());
        add(headerPanel, BorderLayout.NORTH);
        add(tableContainer, BorderLayout.CENTER);
    }

    private void applyExtremeStyle() {

        table.setShowGrid(true);
        table.setIntercellSpacing(new Dimension(1, 1));
        table.setFillsViewportHeight(true);
        table.getTableHeader().setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, new Color(200, 200, 200)));

        table.setGridColor(new Color(230, 234, 238));

        table.setRowHeight(45);
        table.setSelectionBackground(new Color(241, 245, 255));
        table.setSelectionForeground(ACCENT_COLOR);
        table.setFont(new Font("SansSerif", Font.PLAIN, 14));

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean s, boolean f, int r, int c) {
                super.getTableCellRendererComponent(t, v, s, f, r, c);
                setBorder(noFocusBorder);
                if (!s)
                    setBackground(Color.WHITE);
                return this;
            }
        });

        JTableHeader header = table.getTableHeader();
        header.setBorder(BorderFactory.createLineBorder(new Color(230, 234, 238)));
    }

    private void styleModernButton(JButton btn) {
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setBackground(ACCENT_COLOR);
        btn.setForeground(Color.WHITE);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setFont(new Font("SansSerif", Font.BOLD, 13));
        btn.setPreferredSize(new Dimension(160, 40));

        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ACCENT_COLOR, 1),
                BorderFactory.createEmptyBorder(5, 15, 5, 15)));
    }

    private void chargerFichier() {
        JFileChooser jfc = new JFileChooser();
        if (jfc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = jfc.getSelectedFile();

            model.setRowCount(0);
            model.setColumnCount(0);

            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String line;
                boolean first = true;

                while ((line = br.readLine()) != null) {
                    String delimiter = ",";
                    if (line.contains("\t"))
                        delimiter = "\t";
                    else if (line.contains(";"))
                        delimiter = ";";

                    String[] data = line.split(delimiter);

                    if (first) {
                        // Création des colonnes
                        for (String col : data) {

                            model.addColumn(col.trim().toUpperCase());
                        }
                        first = false;
                    } else {
                        // Ajout de la ligne
                        model.addRow(data);
                    }
                }

                // Forcer l'affichage du quadrillage après le chargement
                table.setShowGrid(true);
                table.setGridColor(new Color(220, 220, 220));

            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Erreur de lecture : " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TablePanel().setVisible(true));
    }
}