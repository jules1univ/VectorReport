package fr.univrennes.istic.l2gen.application.gui.panels;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class FilterPanel extends JPanel {

    // Bouton de réinitialisation globale
    private JButton resetButton;

    // Menu déroulant pour choisir la colonne à trier
    private JComboBox<String> comboBox;

    // Association nom de colonne -> type de données (int, double, String,
    // boolean...)
    private Map<String, String> headerType = new HashMap<>();

    // Boutons radio pour choisir l'ordre de tri
    private JRadioButton croissantRadioButton;
    private JRadioButton decroissantRadioButton;
    private JButton resetOrderButton;
    private ButtonGroup orderGroup;

    // Association nom de colonne -> champs de filtre générés dynamiquement
    private Map<String, JComponent[]> filterFields = new HashMap<>();

    /**
     * Génère un panneau de filtre adapté au type de la colonne.
     * - int/double/date : deux champs min/max
     * - Autres (String, boolean...) : un champ de recherche avec placeholder
     */
    public JPanel filterType(String nameColumn, String type) {
        JPanel rowPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        rowPanel.add(new JLabel(nameColumn + " : "));

        if (type.equalsIgnoreCase("int") || type.equalsIgnoreCase("double") || type.equalsIgnoreCase("date")) {
            // Champs min et max pour les types numériques/date
            JTextField minField = new JTextField(5);
            JTextField maxField = new JTextField(5);
            rowPanel.add(new JLabel("Valeur minimale"));
            rowPanel.add(minField);
            rowPanel.add(new JLabel("Valeur maximale"));
            rowPanel.add(maxField);
            filterFields.put(nameColumn, new JComponent[] { minField, maxField });
        } else {
            // Champ de recherche pour les autres types
            JTextField searchField = new JTextField("rechercher...", 10);
            rowPanel.add(searchField);
            filterFields.put(nameColumn, new JComponent[] { searchField });
        }

        return rowPanel;
    }

    public FilterPanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("Filtres"));

        // Panneau central pour les filtres par colonne
        JPanel filtersPanel = new JPanel();
        filtersPanel.setLayout(new BoxLayout(filtersPanel, BoxLayout.Y_AXIS));

        // Panneau nord pour le tri (comboBox + boutons radio)
        JPanel orderPanels = new JPanel();
        orderPanels.setLayout(new BoxLayout(orderPanels, BoxLayout.Y_AXIS));

        // Données fictives : à remplacer par getColumnsType() plus tard
        headerType.put("pimon", "int");
        headerType.put("pilou", "String");
        headerType.put("azerty", "double");
        headerType.put("grougrou", "boolean");

        // Menu déroulant des colonnes disponibles
        comboBox = new JComboBox<>(headerType.keySet().toArray(new String[0]));
        orderPanels.add(comboBox);

        // Boutons radio pour l'ordre de tri
        croissantRadioButton = new JRadioButton("Croissant");
        decroissantRadioButton = new JRadioButton("Décroissant");
        resetOrderButton = new JButton("Supprimer ordre");

        orderGroup = new ButtonGroup();
        orderGroup.add(croissantRadioButton);
        orderGroup.add(decroissantRadioButton);
        orderPanels.add(croissantRadioButton);
        orderPanels.add(decroissantRadioButton);
        orderPanels.add(resetOrderButton);

        // Panneau de filtre dynamique pour une colonne (en dur pour l'instant)
        JPanel dynamicFilterPanel = new JPanel();
        dynamicFilterPanel.setLayout(new BoxLayout(dynamicFilterPanel, BoxLayout.Y_AXIS));
        dynamicFilterPanel.setBorder(BorderFactory.createTitledBorder("Filtre par colonne"));
        dynamicFilterPanel.add(filterType("pimon", "Int"));
        filtersPanel.add(dynamicFilterPanel);

        // Bouton de réinitialisation globale
        resetButton = new JButton("Réinitialiser");
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(resetButton);

        add(filtersPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        add(orderPanels, BorderLayout.NORTH);

        // Listener : affiche la colonne et l'ordre sélectionnés
        comboBox.addActionListener(e -> {
            String selectedCol = (String) comboBox.getSelectedItem();
            String order = croissantRadioButton.isSelected() ? "Croissant"
                    : decroissantRadioButton.isSelected() ? "Décroissant" : "";
            System.out.println("Colonne : " + selectedCol + ", Ordre : " + order);
        });

        croissantRadioButton.addActionListener(e -> {
            String selectedCol = (String) comboBox.getSelectedItem();
            System.out.println("Colonne : " + selectedCol + ", Ordre : Croissant");
        });

        decroissantRadioButton.addActionListener(e -> {
            String selectedCol = (String) comboBox.getSelectedItem();
            System.out.println("Colonne : " + selectedCol + ", Ordre : Décroissant");
        });

        // Listener : réinitialise l'ordre de tri
        resetOrderButton.addActionListener(e -> {
            orderGroup.clearSelection();
            System.out.println("Ordre réinitialisé");
        });

        // Listener : réinitialise tous les filtres
        resetButton.addActionListener(e -> {
            orderGroup.clearSelection();
            comboBox.setSelectedIndex(0);
            System.out.println("Filtres réinitialisés");
        });
    }

    // --- Getters ---

    public JButton getResetButton() {
        return resetButton;
    }

    public JRadioButton getCroissantRadioButton() {
        return croissantRadioButton;
    }

    public JRadioButton getDecroissantRadioButton() {
        return decroissantRadioButton;
    }

    public ButtonGroup getOrderGroup() {
        return orderGroup;
    }

    public Map<String, JComponent[]> getFilterFields() {
        return filterFields;
    }

    // --- Main de test ---

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("FilterPanel");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(350, 300);
            frame.setLocationRelativeTo(null);
            frame.setContentPane(new FilterPanel());
            frame.setVisible(true);
        });
    }

}
