package fr.univrennes.istic.l2gen.application.cli.commands;

import fr.univrennes.istic.l2gen.application.core.CoreController;

public interface ICommand {

    public boolean execute(CoreController controller, String[] args);

    public String getName();

    public String getDescription();

    public String getUsage();
}
