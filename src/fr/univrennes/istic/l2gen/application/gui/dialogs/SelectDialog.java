package fr.univrennes.istic.l2gen.application.gui.dialogs;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class SelectDialog extends JDialog {

    private JComboBox<String> comboBox;
    private String selectedValue = null;
    private boolean confirmed = false;

    public SelectDialog(Frame parent, String title, String message, List<String> options) {
        super(parent, title, true);

        setLayout(new BorderLayout(10, 10));
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JLabel label = new JLabel(message);
        label.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
        add(label, BorderLayout.NORTH);

        comboBox = new JComboBox<>(options.toArray(new String[0]));
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        centerPanel.add(comboBox, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);

        JButton okButton = new JButton("OK");
        JButton cancelButton = new JButton("Cancel");

        okButton.addActionListener(e -> {
            selectedValue = (String) comboBox.getSelectedItem();
            confirmed = true;
            dispose();
        });

        cancelButton.addActionListener(e -> dispose());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(cancelButton);
        buttonPanel.add(okButton);
        add(buttonPanel, BorderLayout.SOUTH);

        pack();
        setMinimumSize(new Dimension(300, 150));
        setLocationRelativeTo(parent);
    }

    public String getSelectedValue() {
        return confirmed ? selectedValue : null;
    }

    public static String show(Frame parent, String title, String message, List<String> options) {
        if (options == null || options.isEmpty()) {
            JOptionPane.showMessageDialog(parent, "No options available.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return null;
        }
        SelectDialog dialog = new SelectDialog(parent, title, message, options);
        dialog.setVisible(true);
        return dialog.getSelectedValue();
    }
}
