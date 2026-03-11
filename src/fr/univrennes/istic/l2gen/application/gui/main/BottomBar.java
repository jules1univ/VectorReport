package fr.univrennes.istic.l2gen.application.gui.main;

import javax.swing.*;
import java.awt.*;

public final class BottomBar extends JPanel {

    private final JLabel statusLabel;

    public BottomBar() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEtchedBorder());
        setPreferredSize(new Dimension(0, 24));

        statusLabel = new JLabel(" Ready");
        add(statusLabel, BorderLayout.WEST);
    }

    public void setStatus(String message) {
        statusLabel.setText(" " + message);
    }
}