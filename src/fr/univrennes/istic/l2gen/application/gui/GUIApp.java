package fr.univrennes.istic.l2gen.application.gui;

import fr.univrennes.istic.l2gen.application.VectorReport;
import fr.univrennes.istic.l2gen.application.core.CoreApp;

import javax.swing.*;

public final class GUIApp extends CoreApp<GUIController> {

    public GUIApp() {
        super(new GUIController());
    }

    public GUIApp(GUIController controller) {
        super(controller);
    }

    @Override
    public void start() {
        if (!this.controller.init()) {
            JOptionPane.showMessageDialog(null,
                    "Application failed to initialize.",
                    "Startup Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        SwingUtilities.invokeLater(() -> {
            GUIView view = new GUIView(controller);
            view.setVisible(true);
        });
    }
}