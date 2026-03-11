package fr.univrennes.istic.l2gen.application.gui;

import javax.swing.*;
import java.awt.*;

public final class GUIView extends JFrame {

    private final GUIController controller;

    public GUIView(GUIController controller) {
        this.controller = controller;
        this.build();
    }

    private void build() {
        setTitle("VectorReport");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 620);
        setMinimumSize(new Dimension(700, 450));
        setLocationRelativeTo(null);

        //
        // setContentPane(root);
    }

}