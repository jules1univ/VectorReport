package fr.univrennes.istic.l2gen.application.gui;

import fr.univrennes.istic.l2gen.application.core.CoreController;
import fr.univrennes.istic.l2gen.application.core.services.CoreServices;
import fr.univrennes.istic.l2gen.application.gui.main.LayoutType;

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

    public void openFileOrFolder() {
    }

    public void refreshView() {

    }

    public void onColumnSelected(int colIndex) {

    }

    public void setCurrentView(LayoutType layout) {

    }

    public void openDocumentation() {

    }

    public void openAbout() {

    }
}