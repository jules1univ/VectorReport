package fr.univrennes.istic.l2gen.application.core;

public abstract class CoreApp<Controller extends CoreController> {

    protected final Controller controller;

    public CoreApp(Controller controller) {
        this.controller = controller;
    }

    public CoreController getController() {
        return controller;
    }

    public abstract void start();
}
