package fr.univrennes.istic.l2gen.application.gui.dialog;

import javax.swing.*;

import fr.univrennes.istic.l2gen.application.core.lang.Lang;

import java.awt.*;
import java.awt.event.ActionListener;

public class StatisticsDialog extends JDialog {

    private final String content;
    private boolean addedToNotebook = false;

    public StatisticsDialog(Frame parentFrame, String title, String content) {
        super(parentFrame, title, true);
        this.content = content;
        build();
        pack();
        setLocationRelativeTo(parentFrame);
    }

    private void build() {
        JPanel rootPanel = new JPanel(new BorderLayout());
        setContentPane(rootPanel);

        JTextArea textArea = new JTextArea(content);
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setCursor(Cursor.getDefaultCursor());
        textArea.setFocusable(false);

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(400, 300));
        rootPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 5));

        JButton closeButton = new JButton(Lang.get("button.close"));
        JButton addToNotebookButton = new JButton(Lang.get("button.add_to_notebook"));

        closeButton.addActionListener(buildCloseAction());
        addToNotebookButton.addActionListener(buildAddToNotebookAction());

        buttonPanel.add(closeButton);
        buttonPanel.add(Box.createHorizontalStrut(4));
        buttonPanel.add(addToNotebookButton);

        rootPanel.add(buttonPanel, BorderLayout.SOUTH);

        getRootPane().setDefaultButton(addToNotebookButton);
    }

    private ActionListener buildAddToNotebookAction() {
        return event -> addedToNotebook = true;
    }

    private ActionListener buildCloseAction() {
        return event -> dispose();
    }

    public boolean isAddedToNotebook() {
        return addedToNotebook;
    }
}