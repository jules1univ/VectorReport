package fr.univrennes.istic.l2gen.application.cli.commands;

import fr.univrennes.istic.l2gen.application.cli.util.Levenstein;
import fr.univrennes.istic.l2gen.application.cli.util.log.Log;
import fr.univrennes.istic.l2gen.application.core.CoreController;

import java.util.List;

public final class HelpCommand implements ICommand {

    private final List<ICommand> commands;

    public HelpCommand(List<ICommand> commands) {
        this.commands = commands;
    }

    @Override
    public boolean execute(CoreController controller, String[] args) {
        if (args.length > 0) {
            String cmdName = args[0];
            for (ICommand cmd : commands) {
                if (cmd.getName().equalsIgnoreCase(cmdName)) {
                    Log.message("Command: %s", cmd.getName());
                    Log.message("Description: %s", cmd.getDescription());
                    Log.message("Usage: %s", cmd.getUsage());
                    return true;
                }
            }

            for (ICommand cmd : commands) {
                int distance = Levenstein.distance(cmdName.toLowerCase(), cmd.getName().toLowerCase());
                if (distance <= 2) {
                    Log.message("Did you mean: %s?", cmd.getName());
                    return true;
                }
            }

            Log.error("Unknown command: %s", cmdName);
            return false;
        }

        Log.message("Available commands:");
        for (ICommand cmd : commands) {
            Log.message("  %-12s %s", cmd.getName(), cmd.getDescription());
        }
        Log.message("Type 'help <command>' for detailed information about a command.\n");
        return true;
    }

    @Override
    public String getName() {
        return "help";
    }

    @Override
    public String getDescription() {
        return "Display help information";
    }

    @Override
    public String getUsage() {
        return "help [command]";
    }
}
