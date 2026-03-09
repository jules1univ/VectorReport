package fr.univrennes.istic.l2gen.application.cli;

import java.util.Optional;

import fr.univrennes.istic.l2gen.application.VectorReport;
import fr.univrennes.istic.l2gen.application.cli.util.log.Log;
import fr.univrennes.istic.l2gen.application.core.CoreApp;

public final class CLIApp extends CoreApp<CLIController> {

    private boolean running;

    public CLIApp() {
        super(new CLIController());
        this.running = false;
    }

    @Override
    public void start() {
        if (this.controller.init()) {
            Log.message("Application failed to init.");
        }

        this.running = true;
        this.printWelcome();

        while (running) {
            Optional<String> optInput = Log.input("vec>");
            if (optInput.isEmpty()) {
                continue;
            }
            Log.message("");
            if (!controller.executeCommand(optInput.get())) {
                running = false;
            }
        }
    }

    private void printWelcome() {
        Log.message("\n\n");
        Log.message("+---------------------------------------------------+");
        Log.message("|                   VectorReport CLI                |");
        Log.message("+---------------------------------------------------+");
        Log.message("\n");

        Log.message("Welcome! Type 'help' to see available commands.");
        Log.message("Type 'exit' to close the application.");

        if (VectorReport.DEBUG_MODE) {
            Log.message("[DEBUG MODE IS ENABLED]");
        }
    }

}
