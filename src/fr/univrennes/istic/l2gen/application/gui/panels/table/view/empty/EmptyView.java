package fr.univrennes.istic.l2gen.application.gui.panels.table.view.empty;

import fr.univrennes.istic.l2gen.application.core.lang.Lang;
import fr.univrennes.istic.l2gen.application.gui.GUIController;

import javax.swing.JButton;
import javax.swing.JPanel;
import java.awt.GridBagLayout;

public final class EmptyView extends JPanel {

    public EmptyView(GUIController controller) {
        super(new GridBagLayout());
        JButton loadButton = new JButton(Lang.get("tableempty.load"));
        loadButton.addActionListener(e -> controller.onOpenFileDialog());
        add(loadButton);
    }
}