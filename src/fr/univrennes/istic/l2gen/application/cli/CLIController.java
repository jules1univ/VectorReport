package fr.univrennes.istic.l2gen.application.cli;

import fr.univrennes.istic.l2gen.application.cli.commands.FilterCommand;
import fr.univrennes.istic.l2gen.application.cli.commands.HelpCommand;
import fr.univrennes.istic.l2gen.application.cli.commands.ICommand;
import fr.univrennes.istic.l2gen.application.cli.commands.ListTablesCommand;
import fr.univrennes.istic.l2gen.application.cli.commands.LoadCommand;
import fr.univrennes.istic.l2gen.application.cli.commands.ShowTableCommand;
import fr.univrennes.istic.l2gen.application.cli.util.Levenstein;
import fr.univrennes.istic.l2gen.application.cli.util.log.Log;
import fr.univrennes.istic.l2gen.application.cli.util.parser.CommandParser;
import fr.univrennes.istic.l2gen.application.cli.util.parser.ParsedCommand;
import fr.univrennes.istic.l2gen.application.core.CoreController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class CLIController extends CoreController {

    private final Map<String, ICommand> commands;
    private final List<ICommand> commandList;

    public CLIController() {
        this.commands = new HashMap<>();
        this.commandList = new ArrayList<>();

    }

    @Override
    public boolean init() {
        this.registerCommands();
        return true;
    }

    private void registerCommands() {
        this.registerCommand(new LoadCommand());

        this.registerCommand(new ShowTableCommand());
        this.registerCommand(new ListTablesCommand());
        this.registerCommand(new FilterCommand());

        this.registerCommand(new HelpCommand(commandList));
    }

    private void registerCommand(ICommand command) {
        this.commands.put(command.getName().toLowerCase(), command);
        this.commandList.add(command);
    }

    public boolean executeCommand(String input) {
        ParsedCommand parsed = CommandParser.parse(input);
        if (parsed.isEmpty()) {
            return true;
        }

        String commandName = parsed.getCommandName().toLowerCase();
        if (commandName.equals("exit")) {
            return false;
        }

        ICommand command = commands.get(commandName);
        if (command == null) {
            for (String cmdName : commands.keySet()) {
                if (Levenstein.distance(commandName, cmdName) <= 2) {
                    Log.message("Did you mean '%s'?", cmdName);
                    return true;
                }
            }

            Log.message("Unknown command: %s", commandName);
            Log.message("Type 'help' to see available commands.");
            return true;
        }

        try {
            command.execute(this, parsed.getArguments());
        } catch (Exception e) {
            Log.error(e, "Failed to execute command '%s'", commandName);
        }

        return true;
    }

    public List<ICommand> getCommands() {
        return new ArrayList<>(commandList);
    }
}
