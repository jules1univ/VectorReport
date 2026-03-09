package fr.univrennes.istic.l2gen.application;

import java.lang.management.ManagementFactory;

import fr.univrennes.istic.l2gen.application.cli.CLIApp;

public class VectorReport {

    public static boolean DEBUG_MODE = ManagementFactory.getRuntimeMXBean().getInputArguments().toString()
            .indexOf("-agentlib:jdwp") > 0;

    public static void main(String[] args) throws Exception {
        new CLIApp().start();
    }
}
