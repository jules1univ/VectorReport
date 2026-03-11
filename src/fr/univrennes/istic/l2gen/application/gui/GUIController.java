package fr.univrennes.istic.l2gen.application.gui;

import fr.univrennes.istic.l2gen.application.core.CoreController;
import fr.univrennes.istic.l2gen.application.core.services.CoreServices;

public final class GUIController extends CoreController {

    public GUIController() {
        this(CoreServices.defaultServices());
    }

    public GUIController(CoreServices services) {
        super(services);
    }

    @Override
    public boolean init() {
        return true;
    }

}