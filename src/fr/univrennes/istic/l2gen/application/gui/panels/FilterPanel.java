package fr.univrennes.istic.l2gen.application.gui.panels;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class FilterPanel extends JPanel {
    private JCheckBox filter1CheckBox;
    private JCheckBox filter2CheckBox;
    private JButton resetButton;
    private JComboBox comboBox;
    private Map<String, String> headerType = new HashMap<>(); // TODO creer getColumnsType() -> nom,type
    private JRadioButton croissantRadioButton;
    private JRadioButton decroissantRadioButton;
    private JButton resetOrderButton;
    private ButtonGroup orderGroup;
    private Map<String, JComponent[]> filterFields = new HashMap<>(); // colonne -> champs de filtre

    public JPanel filterType(String nameColumn, String type) {
        JPanel rowPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        rowPanel.add(new JLabel(nameColumn + " : "));

        if (type.equalsIgnoreCase("int") || type.equalsIgnoreCase("double") || type.equalsIgnoreCase("date")) {
            JTextField minField = new JTextField(5);
            JTextField maxField = new JTextField(5);
            minField.setToolTipText("min");
            maxField.setToolTipText("max");
            rowPanel.add(new JLabel("min"));
            rowPanel.add(minField);
            rowPanel.add(new JLabel("max"));
            rowPanel.add(maxField);
            filterFields.put(nameColumn, new JComponent[] { minField, maxField });
        }
        if (type.equalsIgnoreCase("string")) {
            JTextField searchField = new JTextField("rechercher...");
            rowPanel.add(searchField);
            filterFields.put(nameColumn, new JComponent[] { searchField });
        } else {
        }

        return rowPanel;
    }

    public FilterPanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("Filtres"));
        JPanel filtersPanel = new JPanel();
        filtersPanel.setLayout(new BoxLayout(filtersPanel, BoxLayout.Y_AXIS));
        JPanel orderPanels = new JPanel();
        orderPanels.setLayout(new BoxLayout(orderPanels, BoxLayout.Y_AXIS));

        // Exemple fictif d'association nom de colonne -> type
        headerType.put("pimon", "int");
        headerType.put("pilou", "String");
        headerType.put("azerty", "double");
        headerType.put("grougrou", "boolean");

        // Menu déroulant
        comboBox = new JComboBox<>(headerType.keySet().toArray());
        orderPanels.add(comboBox);
        // Ajout des boutons radio pour l'ordre
        croissantRadioButton = new JRadioButton("Croissant");
        decroissantRadioButton = new JRadioButton("Décroissant");
        resetOrderButton = new JButton("Supprimer ordre");

        orderGroup = new ButtonGroup();
        orderGroup.add(croissantRadioButton);
        orderGroup.add(decroissantRadioButton);
        orderPanels.add(croissantRadioButton);
        orderPanels.add(decroissantRadioButton);
        orderPanels.add(resetOrderButton);

        // Panneau de filtres dynamiques selon le type
        JPanel dynamicFilterPanel = new JPanel();
        dynamicFilterPanel.setLayout(new BoxLayout(dynamicFilterPanel, BoxLayout.Y_AXIS));
        dynamicFilterPanel.setBorder(BorderFactory.createTitledBorder("Filtres par colonne"));

        dynamicFilterPanel.add(filterType("pimon", "String"));

        filtersPanel.add(dynamicFilterPanel);

        // Bouton reset
        resetButton = new JButton("Réinitialiser");
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(resetButton);

        add(filtersPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        add(orderPanels, BorderLayout.NORTH);

        // Action Listener pour le comboBox et les boutons radio
        comboBox.addActionListener(e -> {
            String selectedCol = (String) comboBox.getSelectedItem();
            String order = croissantRadioButton.isSelected() ? "Croissant"
                    : decroissantRadioButton.isSelected() ? "Décroissant" : "";
            System.out.println("Colonne sélectionnée : " + selectedCol + ", Ordre : " + order);
        });
        croissantRadioButton.addActionListener(e -> {
            String selectedCol = (String) comboBox.getSelectedItem();
            System.out.println("Colonne sélectionnée : " + selectedCol + ", Ordre : Croissant");
        });
        decroissantRadioButton.addActionListener(e -> {
            String selectedCol = (String) comboBox.getSelectedItem();
            System.out.println("Colonne sélectionnée : " + selectedCol + ", Ordre : Décroissant");
        });

        resetOrderButton.addActionListener(e -> {
            // Désélectionner les boutons radio
            orderGroup.clearSelection();
            System.out.println("reset ordre croissant/décroissant");
        });
    }

    // Getters pour accéder aux composants si besoin
    public JCheckBox getFilter1CheckBox() {
        return filter1CheckBox;
    }

    public JCheckBox getFilter2CheckBox() {
        return filter2CheckBox;
    }

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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("FilterPanel");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(300, 250);
            frame.setLocationRelativeTo(null);
            frame.setContentPane(new FilterPanel());
            frame.setVisible(true);
        });
    }

}
