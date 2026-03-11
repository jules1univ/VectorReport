package fr.univrennes.istic.l2gen.application.gui.panels;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class FilterPanel extends JPanel {
    private JCheckBox filter1CheckBox;
    private JCheckBox filter2CheckBox;
    private JButton resetButton;
    private ArrayList<String> header = new ArrayList<>();

    public FilterPanel() {

        this.header.add("pimpon");
        this.header.add("pilou");
        this.header.add("azerty");
        this.header.add("grougrou");

        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("Filtres"));

        JPanel filtersPanel = new JPanel();
        filtersPanel.setLayout(new BoxLayout(filtersPanel, BoxLayout.Y_AXIS));

        filter1CheckBox = new JCheckBox("Filtre 1");
        filter2CheckBox = new JCheckBox("Filtre 2");
        filtersPanel.add(filter1CheckBox);
        filtersPanel.add(filter2CheckBox);

        resetButton = new JButton("Réinitialiser");
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(resetButton);

        add(filtersPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
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

    public static void main(String[] args) {
        FilterPanel fp = new FilterPanel();
    }
}
