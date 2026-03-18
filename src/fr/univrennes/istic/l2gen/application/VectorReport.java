package fr.univrennes.istic.l2gen.application;

import java.lang.management.ManagementFactory;

import fr.univrennes.istic.l2gen.application.core.CoreApp;
import fr.univrennes.istic.l2gen.application.gui.GUIApp;

public class VectorReport {

    public static boolean DEBUG_MODE = ManagementFactory.getRuntimeMXBean().getInputArguments().toString()
            .indexOf("-agentlib:jdwp") > 0;

    public static void main(String[] args) throws Exception {
        CoreApp<?> app = new GUIApp();
        app.start();
    }
}
