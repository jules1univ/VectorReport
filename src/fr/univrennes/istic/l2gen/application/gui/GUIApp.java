package fr.univrennes.istic.l2gen.application.gui;

import fr.univrennes.istic.l2gen.application.core.CoreApp;
import fr.univrennes.istic.l2gen.application.core.lang.Lang;
import fr.univrennes.istic.l2gen.application.core.services.TableService;
import fr.univrennes.istic.l2gen.application.gui.main.MainView;

import java.time.LocalTime;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;
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
            TableService.load();

            MainView view = new MainView(controller);
            view.setVisible(true);

            this.controller.setMainView(view);
            this.controller.onStart();
        });

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            this.controller.onStop();
            TableService.save();
        }));
    }
}