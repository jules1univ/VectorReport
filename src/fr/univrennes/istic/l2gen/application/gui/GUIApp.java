package fr.univrennes.istic.l2gen.application.gui;

import fr.univrennes.istic.l2gen.application.core.CoreApp;
import fr.univrennes.istic.l2gen.application.gui.main.MainView;

import java.time.LocalTime;

import javax.swing.*;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;

public final class GUIApp extends CoreApp<GUIController> {

    public GUIApp() {
        super(new GUIController());
    }

    public GUIApp(GUIController controller) {
        super(controller);
    }

    @Override
    public void start() {

        if (!FlatLightLaf.setup()) {
            JOptionPane.showMessageDialog(null,
                    "Application failed to initialize.",
                    "Startup Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        int hour = LocalTime.now().getHour();
        if (hour >= 18 || hour < 6) {
            try {
                UIManager.setLookAndFeel(new FlatDarkLaf());
            } catch (Exception e) {
                return;
            }
        }

        SwingUtilities.invokeLater(() -> {
            MainView view = new MainView(controller);
            view.setVisible(true);

            this.controller.setMainView(view);
        });
    }
}